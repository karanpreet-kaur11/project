package com.example.campusstore.service;

import com.example.campusstore.entity.Category;
import com.example.campusstore.entity.Product;
import com.example.campusstore.repository.CategoryRepository;
import com.example.campusstore.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Product> search(String name, Long categoryId,
                                 Boolean inStock, int page, int size,
                                 String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.searchActive(name, categoryId, inStock, pageable);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<Product> findAllActive() {
        return productRepository.findAll().stream()
                .filter(Product::isActive).toList();
    }

    public void create(String name, String description, BigDecimal price,
                       int stockQty, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Product p = new Product();
        p.setName(name.trim());
        p.setDescription(description.trim());
        p.setPrice(price);
        p.setStockQty(stockQty);
        p.setActive(true);
        p.setCategory(category);
        productRepository.save(p);
    }

    public void update(Long id, String name, String description,
                       BigDecimal price, int stockQty, Long categoryId) {
        Product p = findById(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        p.setName(name.trim());
        p.setDescription(description.trim());
        p.setPrice(price);
        p.setStockQty(stockQty);
        p.setCategory(category);
        productRepository.save(p);
    }

    public void deactivate(Long id) {
        Product p = findById(id);
        p.setActive(false);
        productRepository.save(p);
    }
}
