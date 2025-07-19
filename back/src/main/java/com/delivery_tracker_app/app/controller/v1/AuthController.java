package com.delivery_tracker_app.app.controller.v1;

import com.delivery_tracker_app.app.config.ApiPaths;
import com.delivery_tracker_app.app.config.JwtTokenProvider;
import com.delivery_tracker_app.app.dto.v1.auth.LoginRequestDto;
import com.delivery_tracker_app.app.dto.v1.auth.LoginResponseDto;
import com.delivery_tracker_app.app.dto.v1.common.BaseResponse;
import com.delivery_tracker_app.app.exception.ErrorResponse;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.BASE + ApiPaths.V1 + "/auth")
@Tag(name = "Authentication", description = "Handles user login and token generation")
@SecurityRequirement(name = "BearerAuth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user and generate JWT",
            description = "Takes username and password, returns access token and expiry time.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @SecurityRequirements({})
    public ResponseEntity<BaseResponse<LoginResponseDto>> authenticateUser(
            @Valid @RequestBody LoginRequestDto loginRequest
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        String username = authentication.getName();
        String expiresAt = jwtTokenProvider.getExpirationDate(jwt).toString();

        LoginResponseDto response = new LoginResponseDto(jwt, username, expiresAt);
        return ResponseEntity.ok(BaseResponse.ok("Login successful", response));
    }
}
