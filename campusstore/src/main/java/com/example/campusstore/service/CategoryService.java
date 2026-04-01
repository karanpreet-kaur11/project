package com.example.campusstore.service;

import com.example.campusstore.entity.Category;
import com.example.campusstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public void create(String name) {
        Category category = new Category();
        category.setName(name.trim());
        categoryRepository.save(category);
    }
}
