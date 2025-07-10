package com.delivery_tracker_app.app.mapper;

import com.delivery_tracker_app.app.dto.v1.client.ClientResponse;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.CreateTransportLogRequest;
import com.delivery_tracker_app.app.dto.v1.transportLog.TransportLogResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.UpdateTransportLogRequest;
import com.delivery_tracker_app.app.entity.Client;
import com.delivery_tracker_app.app.entity.Driver;
import com.delivery_tracker_app.app.entity.TransportLog;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransportLogMapper {

    private final ClientMapper clientMapper;
    private final DriverMapper driverMapper;

    public TransportLog toEntity(CreateTransportLogRequest request, Client client, Driver driver){
        return TransportLog.builder()
                .client(client)
                .driver(driver)
                .loadDate(request.loadDate())
                .loadLocation(request.loadLocation())
                .unloadDate(request.unloadDate())
                .unloadLocation(request.unloadLocation())
                .destinationName(request.destinationName())
                .deliveryNote(request.deliveryNote())
                .advance(request.advance())
                .fuelQuantity(request.fuelQuantity())
                .fuelPricePerLiter(request.fuelPricePerLiter())
                .variableCharge(request.variableCharge())
                .chargePrice(request.chargePrice())
                .clientTariff(request.clientTariff())
                .tripPrice(request.tripPrice())
                .operator(request.operator())
                .commercial(request.commercial())
                .build();
    }

    public TransportLog toEntity(UpdateTransportLogRequest request, Client client, Driver driver){
        return TransportLog.builder()
                .id(request.id())
                .client(client)
                .driver(driver)
                .loadDate(request.loadDate())
                .loadLocation(request.loadLocation())
                .unloadDate(request.unloadDate())
                .unloadLocation(request.unloadLocation())
                .destinationName(request.destinationName())
                .deliveryNote(request.deliveryNote())
                .advance(request.advance())
                .fuelQuantity(request.fuelQuantity())
                .fuelPricePerLiter(request.fuelPricePerLiter())
                .variableCharge(request.variableCharge())
                .chargePrice(request.chargePrice())
                .clientTariff(request.clientTariff())
                .tripPrice(request.tripPrice())
                .operator(request.operator())
                .commercial(request.commercial())
                .build();
    }

    public TransportLogResponse toDto(TransportLog transportLog){

        ClientResponse clientResponse = clientMapper.toDto(transportLog.getClient());
        DriverResponse driverResponse = driverMapper.toDto(transportLog.getDriver());

        return new TransportLogResponse(
                transportLog.getId(),
                clientResponse,
                driverResponse,
                transportLog.getLoadDate(),
                transportLog.getLoadLocation(),
                transportLog.getUnloadDate(),
                transportLog.getUnloadLocation(),
                transportLog.getDestinationName(),
                transportLog.getDeliveryNote(),
                transportLog.getAdvance(),
                transportLog.getFuelQuantity(),
                transportLog.getFuelPricePerLiter(),
                transportLog.getVariableCharge(),
                transportLog.getChargePrice(),
                transportLog.getClientTariff(),
                transportLog.getTripPrice(),
                transportLog.getOperator(),
                transportLog.getCommercial()
        );
    }
}
