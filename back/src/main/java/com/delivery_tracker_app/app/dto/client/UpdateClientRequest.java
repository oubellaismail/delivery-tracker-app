package com.delivery_tracker_app.app.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        @NotNull Long id,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100)String identityId
) {
}
