package com.delivery_tracker_app.app.dto.v1.transportLog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransportLogRequest(
        @NotNull Long id,
        @NotNull Long clientId,
        @NotNull Long driverId,
        @NotNull LocalDate loadDate,
        @NotBlank String loadLocation,
        @NotNull LocalDate unloadDate,
        @NotBlank String unloadLocation,
        @NotBlank String destinationName,
        String deliveryNote,
        @NotNull BigDecimal advance,
        @NotNull BigDecimal fuelQuantity,
        @NotNull BigDecimal fuelPricePerLiter,
        @NotNull BigDecimal variableCharge,
        @NotNull BigDecimal chargePrice,
        @NotNull BigDecimal clientTariff,
        @NotNull BigDecimal tripPrice,
        @NotBlank String operator,
        @NotBlank String commercial
) {
}
