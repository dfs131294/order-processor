package com.diego.orders.processor.application.adapter.in;

import com.diego.orders.processor.application.dto.AuthResponseDTO;
import com.diego.orders.processor.application.dto.ErrorResponseDTO;
import com.diego.orders.processor.application.dto.LoginRequestDTO;
import com.diego.orders.processor.infrastructure.adapters.in.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Operation(summary = "Login")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login correcto"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Credenciales incorrectas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid File",
                                            value = """
                                                    {
                                                        "code": "CODE_02",
                                                        "message": "Credenciales incorrectas",
                                                        "details": null,
                                                        "correlationId": "0fcaed7f-4a41-46f2-8668-8968e44a520b"
                                                    }
                                                    """
                                    )
                            }

                    ))
    })
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication.getName());

        return new AuthResponseDTO(token);
    }
}
