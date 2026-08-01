package com.jobboard.jobboard.module.auth;

import com.jobboard.jobboard.module.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("request", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register/candidat")
    public String registerCandidat(@Valid @ModelAttribute("request") RegisterRequest request,
            BindingResult result, Model model) {
        if (result.hasErrors())
            return "auth/register";
        try {
            authService.registerCandidat(request);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/register/recruteur")
    public String registerRecruteur(@Valid @ModelAttribute("request") RegisterRequest request,
            BindingResult result, Model model) {
        if (result.hasErrors())
            return "auth/register";
        try {
            authService.registerRecruteur(request);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            return "auth/register";
        }
    }
}