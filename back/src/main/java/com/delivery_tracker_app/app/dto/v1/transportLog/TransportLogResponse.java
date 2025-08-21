package com.delivery_tracker_app.app.dto.v1.transportLog;

import com.delivery_tracker_app.app.dto.v1.client.ClientResponse;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransportLogResponse(
        Long id,
        ClientResponse client,
        DriverResponse driver,
        LocalDate loadDate,
        String loadLocation,
        LocalDate unloadDate,
        String unloadLocation,
        String destinationName,
        String deliveryNote,
        BigDecimal advance,
        BigDecimal fuelQuantity,
        BigDecimal fuelPricePerLiter,
        BigDecimal variableCharge,
        BigDecimal chargePrice,
        BigDecimal clientTariff,
        BigDecimal tripPrice,
        String operator,
        String commercial
) {
}
