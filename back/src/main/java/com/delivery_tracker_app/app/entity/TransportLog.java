package com.delivery_tracker_app.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TransportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Driver driver;

    private LocalDate loadDate;
    private String loadLocation;
    private LocalDate unloadDate;
    private String unloadLocation;
    private String destinationName;
    private String deliveryNote;

    private BigDecimal advance;
    private BigDecimal fuelQuantity;
    private BigDecimal fuelPricePerLiter;
    private BigDecimal variableCharge;
    private BigDecimal chargePrice;
    private BigDecimal clientTariff;
    private BigDecimal tripPrice;

    private String operator;
    private String commercial;
}
