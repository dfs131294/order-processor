package com.diego.orders.processor.application.adapter.in;

import com.diego.orders.processor.infrastructure.adapters.in.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldReturnJwtToken() throws Exception {

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("diego");

        when(jwtService.generateToken("diego"))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"diego",
                                  "password":"secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("jwt-token"));
    }
}