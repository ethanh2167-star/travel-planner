package com.example.travelplanner.controller;

import com.example.travelplanner.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@RequestParam String email,
                                        RedirectAttributes ra) {
        boolean sent = passwordResetService.sendResetEmail(email);
        if (sent) {
            ra.addFlashAttribute("successMsg", "重設密碼連結已寄送到您的 Email！");
        } else {
            ra.addFlashAttribute("errorMsg", "找不到此 Email 對應的帳號。");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token,
                                       @RequestParam String newPassword,
                                       RedirectAttributes ra) {
        boolean success = passwordResetService.resetPassword(token, newPassword);
        if (success) {
            ra.addFlashAttribute("successMsg", "密碼已重設成功，請重新登入！");
            return "redirect:/login";
        } else {
            ra.addFlashAttribute("errorMsg", "連結已失效或無效，請重新申請。");
            return "redirect:/forgot-password";
        }
    }
}