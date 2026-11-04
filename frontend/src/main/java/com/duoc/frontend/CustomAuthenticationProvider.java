package com.duoc.frontend;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final BackendApiClient backendApiClient;
    private final TokenStore tokenStore;

    public CustomAuthenticationProvider(BackendApiClient backendApiClient, TokenStore tokenStore) {
        this.backendApiClient = backendApiClient;
        this.tokenStore = tokenStore;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Object credentials = authentication.getCredentials();
        if (username == null || username.isBlank() || credentials == null) {
            return null;
        }

        String token = backendApiClient.authenticate(username, credentials.toString());
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new AuthenticationServiceException("No se encontró la sesión de la solicitud de acceso.");
        }

        HttpServletRequest request = requestAttributes.getRequest();
        tokenStore.save(request.getSession(true), token);
        return UsernamePasswordAuthenticationToken.authenticated(
                username,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
