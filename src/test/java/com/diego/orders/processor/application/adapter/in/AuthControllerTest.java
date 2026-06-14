package com.diego.orders.processor.application.adapter.in;

import com.diego.orders.processor.application.dto.AuthResponseDTO;
import com.diego.orders.processor.application.dto.LoginRequestDTO;
import com.diego.orders.processor.infrastructure.adapters.in.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldAuthenticateAndReturnJwtToken() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("diego");
        request.setPassword("secret");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("diego");

        when(jwtService.generateToken("diego"))
                .thenReturn("jwt-token");

        AuthResponseDTO response =
                authController.login(request);

        assertNotNull(response);
        assertEquals(response.getToken(), "jwt-token");

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class));

        verify(jwtService)
                .generateToken("diego");
    }

    @Test
    void shouldAuthenticateUsingRequestCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("diego");
        request.setPassword("secret");

        Authentication authentication =
                mock(Authentication.class);

        ArgumentCaptor<UsernamePasswordAuthenticationToken>
                captor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("diego");

        when(jwtService.generateToken(anyString()))
                .thenReturn("jwt-token");

        authController.login(request);

        verify(authenticationManager)
                .authenticate(captor.capture());

        UsernamePasswordAuthenticationToken token =
                captor.getValue();

        assertEquals(token.getPrincipal(), "diego");

        assertEquals(token.getCredentials(), "secret");
    }
}