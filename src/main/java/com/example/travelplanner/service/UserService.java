package com.example.travelplanner.service;

import com.example.travelplanner.entity.User;
import com.example.travelplanner.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public User register(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("使用者名稱已被使用");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email 已被註冊");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .build();

        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在：" + username));
    }
    public Optional<com.example.travelplanner.entity.User> findByUsernameOptional(String username) {
        return userRepository.findByUsername(username);
    }
}
