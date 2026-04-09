package com.example.campusstore.controller;

import com.example.campusstore.entity.Order;
import com.example.campusstore.service.OrderService;
import com.example.campusstore.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;

    public OrderController(OrderService orderService,
                           ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/build")
    public String showOrderBuilder(HttpSession session, Model model) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if ("ADMIN".equals(session.getAttribute("USER_ROLE"))) {
            return "error/forbidden";
        }
        model.addAttribute("products", productService.findAllActive());
        return "order/build";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam Map<String, String> params,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if ("ADMIN".equals(session.getAttribute("USER_ROLE"))) {
            return "error/forbidden";
        }

        Long customerId = (Long) session.getAttribute("USER_ID");

        Map<Long, Integer> productQtyMap = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("qty_")) {
                try {
                    Long productId = Long.parseLong(entry.getKey().substring(4));
                    int qty = Integer.parseInt(entry.getValue());
                    if (qty < 0) {
                        redirectAttributes.addFlashAttribute("error", "Quantity cannot be negative.");
                        return "redirect:/order/build";
                    }
                    if (qty > 0) productQtyMap.put(productId, qty);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (productQtyMap.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Please select at least one product with quantity > 0.");
            return "redirect:/order/build";
        }

        try {
            Order order = orderService.createOrder(customerId, productQtyMap);
            redirectAttributes.addFlashAttribute("message",
                    "Order placed! Order ID: " + order.getId() +
                    " | Total: $" + order.getTotal());
            return "redirect:/order/my-orders";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/build";
        }
    }

    @GetMapping("/my-orders")
    public String myOrders(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size,
                           HttpSession session,
                           Model model) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if ("ADMIN".equals(session.getAttribute("USER_ROLE"))) {
            return "error/forbidden";
        }
        Long customerId = (Long) session.getAttribute("USER_ID");
        Page<Order> orders = orderService.getMyOrders(customerId, page, size);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "order/my-orders";
    }

    @GetMapping("/my-orders/{id}")
    public String orderDetail(@PathVariable Long id,
                              HttpSession session,
                              Model model) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if ("ADMIN".equals(session.getAttribute("USER_ROLE"))) {
            return "error/forbidden";
        }
        Long customerId = (Long) session.getAttribute("USER_ID");
        try {
            Order order = orderService.getMyOrder(id, customerId);
            model.addAttribute("order", order);
            return "order/detail";
        } catch (SecurityException e) {
            return "error/forbidden";
        }
    }
}