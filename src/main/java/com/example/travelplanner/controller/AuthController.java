package com.example.travelplanner.controller;

import com.example.travelplanner.service.UserService;
import jakarta.validation.constraints.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails user) {
        return user != null ? "redirect:/trips" : "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                             @RequestParam(required = false) String logout,
                             @RequestParam(required = false) String username,
                             Model model,
                             HttpServletRequest request) {
        if (error != null) {
            String lastUsername = (String) request.getSession().getAttribute("LAST_USERNAME");

            if (lastUsername != null) {
                var userOpt = userService.findByUsernameOptional(lastUsername);
                if (userOpt.isPresent()) {
                    var u = userOpt.get();
                    if (u.getLockTime() != null &&
                        u.getLockTime().plusMinutes(30).isAfter(java.time.LocalDateTime.now())) {
                        model.addAttribute("errorMsg", "帳號已因多次輸入錯誤被鎖定 30 分鐘，請稍後再試。");
                    } else {
                        int attempts = u.getFailedAttempts() == null ? 0 : u.getFailedAttempts();
                        int remaining = 5 - attempts;
                        if (remaining > 0) {
                            model.addAttribute("errorMsg",
                                "帳號或密碼錯誤，還剩 " + remaining + " 次機會。");
                        } else {
                            model.addAttribute("errorMsg", "帳號或密碼錯誤，請重試。");
                        }
                    }
                } else {
                    model.addAttribute("errorMsg", "帳號或密碼錯誤，請重試。");
                }
            } else {
                model.addAttribute("errorMsg", "帳號或密碼錯誤，請重試。");
            }
        }
        if (logout != null) model.addAttribute("successMsg", "已成功登出。");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(
            @RequestParam @NotBlank String username,
            @RequestParam @Email String email,
            @RequestParam @Size(min = 6) String password,
            @RequestParam String fullName,
            RedirectAttributes ra) {
        try {
            userService.register(username, email, password, fullName);
            ra.addFlashAttribute("successMsg", "註冊成功！請登入。");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/register";
        }
    }
}
