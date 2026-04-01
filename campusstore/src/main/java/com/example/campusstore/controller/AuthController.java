package com.example.campusstore.controller;

import com.example.campusstore.entity.User;
import com.example.campusstore.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private static final String SESSION_USER_ID    = "USER_ID";
    private static final String SESSION_USER_EMAIL = "USER_EMAIL";
    private static final String SESSION_USER_ROLE  = "USER_ROLE";
    private static final String SESSION_USER_NAME  = "USER_NAME";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ---------- REGISTER ----------
    @GetMapping("/register")
    public String showRegister(HttpSession session) {
        if (session.getAttribute(SESSION_USER_ID) != null) return "redirect:/catalog";
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {
        try {
            authService.register(name, email, password);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    // ---------- LOGIN ----------
    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        if (session.getAttribute(SESSION_USER_ID) != null) return "redirect:/catalog";
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            User user = authService.authenticate(email, password);
            session.setAttribute(SESSION_USER_ID,    user.getId());
            session.setAttribute(SESSION_USER_EMAIL, user.getEmail());
            session.setAttribute(SESSION_USER_ROLE,  user.getRole().name());
            session.setAttribute(SESSION_USER_NAME,  user.getName());

            if (user.getRole() == User.Role.ADMIN) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/catalog";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    // ---------- LOGOUT ----------
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
