package com.example.travelplanner.service;

import com.example.travelplanner.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者：" + username));


        if (user.getLockTime() != null) {
            if (user.getLockTime().plusMinutes(30).isAfter(LocalDateTime.now())) {

                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        false, 
                        true, true, true,
                        List.of(new SimpleGrantedAuthority(user.getRole()))
                );
            } else {

                user.setEnabled(true);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}