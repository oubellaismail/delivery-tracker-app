package com.delivery_tracker_app.app.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 20) String identityId
) {
}
