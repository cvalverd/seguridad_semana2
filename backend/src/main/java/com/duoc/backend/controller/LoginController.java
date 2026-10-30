package com.duoc.backend.controller;

import com.duoc.backend.config.JWTAuthtenticationConfig;
import com.duoc.backend.service.MyUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final JWTAuthtenticationConfig jwtAuthtenticationConfig;
    private final MyUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(JWTAuthtenticationConfig jwtAuthtenticationConfig,
                          MyUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthtenticationConfig = jwtAuthtenticationConfig;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("user") String username,
            @RequestParam("encryptedPass") String encryptedPass) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(encryptedPass, userDetails.getPassword())) {
            throw new RuntimeException("Invalid login");
        }

        return jwtAuthtenticationConfig.getJWTToken(username);
    }
}