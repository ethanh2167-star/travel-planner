package com.example.travelplanner.service;

import com.example.travelplanner.entity.User;
import com.example.travelplanner.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
                                 JavaMailSender mailSender,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean sendResetEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Travel Planner 密碼重設");
        msg.setText("請點擊以下連結重設密碼（30分鐘內有效）：\n\n"
                + "http://localhost:5566/reset-password?token=" + token
                + "\n\n如果您未申請重設密碼，請忽略此信件。");
        mailSender.send(msg);
        return true;
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token).orElse(null);
        if (user == null) return false;
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }
}