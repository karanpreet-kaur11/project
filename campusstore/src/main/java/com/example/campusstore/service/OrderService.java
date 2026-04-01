package com.example.campusstore.service;

import com.example.campusstore.entity.*;
import com.example.campusstore.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrder(Long customerId, Map<Long, Integer> productQtyMap) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(Order.Status.NEW);

        BigDecimal total = BigDecimal.ZERO;

        Order savedOrder = orderRepository.save(order);

        for (Map.Entry<Long, Integer> entry : productQtyMap.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) continue;

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (!product.isActive()) {
                throw new IllegalArgumentException("Product " + product.getName() + " is not available");
            }
            if (product.getStockQty() < qty) {
                throw new IllegalArgumentException("Not enough stock for " + product.getName());
            }

            // Deduct stock
            product.setStockQty(product.getStockQty() - qty);
            productRepository.save(product);

            // Capture price at purchase time
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQty(qty);
            item.setUnitPrice(product.getPrice());
            orderItemRepository.save(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        savedOrder.setTotal(total);
        return orderRepository.save(savedOrder);
    }

    public Page<Order> getMyOrders(Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    public Order getMyOrder(Long orderId, Long customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new SecurityException("Forbidden");
        }
        return order;
    }

    public Page<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Transactional
    public void updateStatus(Long orderId, Order.Status newStatus) {
        Order order = getOrderById(orderId);

        if (order.getStatus() == Order.Status.FULFILLED ||
            order.getStatus() == Order.Status.CANCELLED) {
            throw new IllegalArgumentException("Order is already " + order.getStatus());
        }

        if (newStatus == Order.Status.CANCELLED) {
            // Restore stock
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQty(product.getStockQty() + item.getQty());
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
