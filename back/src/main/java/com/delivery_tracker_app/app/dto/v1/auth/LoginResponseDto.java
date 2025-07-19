package com.delivery_tracker_app.app.dto.v1.auth;

public record LoginResponseDto(
        String token,
        String username,
        String expiresAt
) {
}
