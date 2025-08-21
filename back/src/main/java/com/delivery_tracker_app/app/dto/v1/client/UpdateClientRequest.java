package com.delivery_tracker_app.app.dto.v1.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        @NotNull Long id,
        @NotBlank @Size(max = 20) String name,
        @NotBlank @Size(max = 20)String identityId
) {
}
