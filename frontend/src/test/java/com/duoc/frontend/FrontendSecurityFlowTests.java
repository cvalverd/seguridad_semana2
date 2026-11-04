package com.duoc.frontend;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

@SpringBootTest
@AutoConfigureMockMvc
class FrontendSecurityFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackendApiClient backendApiClient;

    @Test
    void homeShowsLoginActionToAnonymousVisitors() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Iniciar sesión")))
                .andExpect(content().string(not(containsString("anonymousUser"))));
    }

    @Test
    void protectedGreetingRedirectsUnauthenticatedVisitorsToLogin() throws Exception {
        mockMvc.perform(get("/greetings").param("name", "Alumno"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void loginStoresBackendTokenInSessionAndUsesItForProtectedGreeting() throws Exception {
        when(backendApiClient.authenticate("admin", "admin")).thenReturn("jwt-token");
        when(backendApiClient.greeting("Alumno", "jwt-token")).thenReturn("Hello Alumno");

        var loginResult = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        mockMvc.perform(get("/greetings")
                        .session(session)
                        .param("name", "Alumno"))
                .andExpect(status().isOk());

        verify(backendApiClient).greeting("Alumno", "jwt-token");
    }

    @Test
    @WithMockUser
    void logoutRequiresPostAndCsrfProtection() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
