package com.example.campusstore.controller;

import com.example.campusstore.entity.Product;
import com.example.campusstore.service.CategoryService;
import com.example.campusstore.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CatalogController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public CatalogController(ProductService productService,
                              CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/catalog";
    }

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "5")   int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc")  String sortDir,
            HttpSession session,
            Model model) {

        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        Page<Product> productPage = productService.search(
                name, categoryId, inStock, page, size, sortBy, sortDir);

        model.addAttribute("productPage",  productPage);
        model.addAttribute("categories",   categoryService.findAll());
        model.addAttribute("currentPage",  page);
        model.addAttribute("pageSize",     size);
        model.addAttribute("sortBy",       sortBy);
        model.addAttribute("sortDir",      sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("name",         name);
        model.addAttribute("categoryId",   categoryId);
        model.addAttribute("inStock",      inStock);

        return "catalog/catalog";
    }

    @GetMapping("/catalog/{id}")
    public String productDetail(@PathVariable Long id,
                                HttpSession session,
                                Model model) {
        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }
        model.addAttribute("product", productService.findById(id));
        return "catalog/detail";
    }
}