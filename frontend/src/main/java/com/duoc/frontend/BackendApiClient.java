package com.duoc.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class BackendApiClient {

    private final RestClient restClient;

    public BackendApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${backend.base-url}") String backendBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(backendBaseUrl).build();
    }

    public String authenticate(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("user", username);
        form.add("encryptedPass", password);

        try {
            String token = restClient.post()
                    .uri("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);
            if (token == null || token.isBlank()) {
                throw new AuthenticationServiceException("El backend no devolvió un token de acceso.");
            }
            return token;
        } catch (RestClientResponseException exception) {
            throw new BadCredentialsException("No fue posible iniciar sesión con esas credenciales.", exception);
        } catch (ResourceAccessException exception) {
            throw new AuthenticationServiceException("No se pudo conectar con el servicio de autenticación.", exception);
        }
    }

    public String greeting(String name, String token) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/private/greetings")
                        .queryParam("name", name)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
}
