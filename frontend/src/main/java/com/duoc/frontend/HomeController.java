package com.duoc.frontend;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;

    public HomeController(BackendApiClient backendApiClient, TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("username", authentication.getName());
        }
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/greetings")
    public String greeting(
            @RequestParam(defaultValue = "Alumno") String name,
            HttpSession session,
            Model model) {
        String greeting = backendApiClient.greeting(name, tokenStore.requireToken(session));
        model.addAttribute("greeting", greeting);
        model.addAttribute("name", name);
        return "greetings";
    }
}
