package com.duoc.backend.config;

import com.duoc.backend.entity.User;
import com.duoc.backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        User admin = userRepository.findByUsername("admin");

        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@local");
            admin.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(admin);
            return;
        }

        if (!passwordEncoder.matches("admin", admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(admin);
        }
    }
}
