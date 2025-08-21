package com.delivery_tracker_app.app.dto.v1.driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDriverRequest(
    @NotBlank @Size(max = 20) String name,
    @NotBlank @Size(max = 20) String plateNumber
) {
}
