package com.delivery_tracker_app.app.mapper;

import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;
import com.delivery_tracker_app.app.entity.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {
    public Driver toEntity(CreateDriverRequest dto){
        return Driver.builder()
                .name(dto.name())
                .plateNumber(dto.plateNumber())
                .build();
    }

    public Driver toEntity(UpdateDriverRequest dto){
        return Driver.builder()
                .id(dto.id())
                .name(dto.name())
                .plateNumber(dto.plateNumber())
                .build();
    }

    public DriverResponse toDto(Driver driver){
        return new DriverResponse(driver.getId(), driver.getName(), driver.getPlateNumber());
    }
}
