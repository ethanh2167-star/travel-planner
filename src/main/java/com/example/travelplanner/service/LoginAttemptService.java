package com.example.travelplanner.service;

import com.example.travelplanner.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 30;

    private final UserRepository userRepository;

    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void loginFailed(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
            attempts++;
            user.setFailedAttempts(attempts);
            if (attempts >= MAX_ATTEMPTS) {
                user.setLockTime(LocalDateTime.now());
                user.setEnabled(false);
            }
            userRepository.save(user);
        });
    }

    @Transactional
    public void loginSuccess(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedAttempts(0);
            user.setLockTime(null);
            user.setEnabled(true);
            userRepository.save(user);
        });
    }

    public boolean isLocked(String username) {
        return userRepository.findByUsername(username).map(user -> {
            if (user.getLockTime() == null) return false;
            if (user.getLockTime().plusMinutes(LOCK_MINUTES).isAfter(LocalDateTime.now())) {
                return true;
            }

            user.setEnabled(true);
            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
            return false;
        }).orElse(false);
    }
}