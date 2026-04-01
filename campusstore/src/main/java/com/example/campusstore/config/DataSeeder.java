package com.example.campusstore.config;

import com.example.campusstore.entity.*;
import com.example.campusstore.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Only seed if no users exist
            if (userRepository.count() > 0) return;

            // --- ADMIN user ---
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin123@campusstore.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);

            // --- CUSTOMER user ---
            User customer = new User();
            customer.setName("Karan");
            customer.setEmail("karan@campusstore.com");
            customer.setPasswordHash(passwordEncoder.encode("Karan@123"));
            customer.setRole(User.Role.CUSTOMER);
            userRepository.save(customer);

            // --- Categories ---
            Category electronics = new Category();
            electronics.setName("Electronics");
            categoryRepository.save(electronics);

            Category clothing = new Category();
            clothing.setName("Clothing");
            categoryRepository.save(clothing);

            Category stationery = new Category();
            stationery.setName("Stationery");
            categoryRepository.save(stationery);

            // --- Products (at least 6 active for pagination demo) ---
            String[][] products = {
                {"Laptop",        "High performance laptop",     "999.99",  "10"},
                {"T-Shirt",       "Campus logo t-shirt",         "19.99",   "50"},
                {"Notebook",      "200-page ruled notebook",     "4.99",    "100"},
                {"Headphones",    "Noise cancelling headphones", "149.99",  "15"},
                {"Hoodie",        "Campus hoodie",               "49.99",   "30"},
                {"USB-C Cable",   "2m braided USB-C cable",      "12.99",   "75"},
                {"Backpack",      "Waterproof laptop backpack",  "79.99",   "20"},
                {"Pen Set",       "Pack of 10 ballpoint pens",   "3.99",    "200"}
            };

            Category[] cats = {electronics, clothing, stationery,
                               electronics, clothing, electronics,
                               clothing, stationery};

            for (int i = 0; i < products.length; i++) {
                Product p = new Product();
                p.setName(products[i][0]);
                p.setDescription(products[i][1]);
                p.setPrice(new BigDecimal(products[i][2]));
                p.setStockQty(Integer.parseInt(products[i][3]));
                p.setActive(true);
                p.setCategory(cats[i]);
                productRepository.save(p);
            }

            System.out.println("=== Seed data loaded ===");
            System.out.println("ADMIN: admin123@campusstore.com / Admin@123");
            System.out.println("CUSTOMER: karan@campusstore.com / Karan@123");
        };
    }
}
