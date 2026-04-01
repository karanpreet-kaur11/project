package com.example.campusstore.controller;

import com.example.campusstore.entity.Order;
import com.example.campusstore.service.CategoryService;
import com.example.campusstore.service.OrderService;
import com.example.campusstore.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(CategoryService categoryService,
                           ProductService productService,
                           OrderService orderService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderService = orderService;
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("USER_ROLE"));
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            HttpSession session,
                            Model model) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("products",   productService.findAllActive());
        model.addAttribute("orders",     orderService.getAllOrders(page, size));
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize",   size);
        return "admin/dashboard";
    }

    // --- Category ---
    @PostMapping("/category/create")
    public String createCategory(@RequestParam String name,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";
        categoryService.create(name);
        redirectAttributes.addFlashAttribute("message", "Category created: " + name);
        return "redirect:/admin/dashboard";
    }

    // --- Product ---
    @GetMapping("/product/new")
    public String showCreateProduct(HttpSession session, Model model) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isNew", true);
        return "admin/product-form";
    }

    @PostMapping("/product/create")
    public String createProduct(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam BigDecimal price,
                                @RequestParam int stockQty,
                                @RequestParam Long categoryId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";
        productService.create(name, description, price, stockQty, categoryId);
        redirectAttributes.addFlashAttribute("message", "Product created: " + name);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/product/{id}/edit")
    public String showEditProduct(@PathVariable Long id,
                                   HttpSession session,
                                   Model model) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";
        model.addAttribute("product",    productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isNew", false);
        return "admin/product-form";
    }

    @PostMapping("/product/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam BigDecimal price,
                                @RequestParam int stockQty,
                                @RequestParam Long categoryId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";
        productService.update(id, name, description, price, stockQty, categoryId);
        redirectAttributes.addFlashAttribute("message", "Product updated: " + name);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/product/{id}/deactivate")
    public String deactivateProduct(@PathVariable Long id,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";
        productService.deactivate(id);
        redirectAttributes.addFlashAttribute("message", "Product deactivated.");
        return "redirect:/admin/dashboard";
    }

    // --- Orders ---
    @PostMapping("/order/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam String status,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
        if (!isAdmin(session)) return "error/forbidden";
        try {
            orderService.updateStatus(id, Order.Status.valueOf(status));
            redirectAttributes.addFlashAttribute("message", "Order " + id + " updated to " + status);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}