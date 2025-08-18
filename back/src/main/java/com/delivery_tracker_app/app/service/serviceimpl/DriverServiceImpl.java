package com.delivery_tracker_app.app.service.serviceimpl;

import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;
import com.delivery_tracker_app.app.entity.Driver;
import com.delivery_tracker_app.app.exception.ResourceNotFoundException;
import com.delivery_tracker_app.app.mapper.DriverMapper;
import com.delivery_tracker_app.app.repository.DriverRepo;
import com.delivery_tracker_app.app.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    public final DriverRepo driverRepo;
    public final DriverMapper driverMapper;

    @Override
    public DriverResponse create(CreateDriverRequest req){
        Driver driver = driverRepo.save(driverMapper.toEntity(req));
        return driverMapper.toDto(driver);
    }

    @Override
    public DriverResponse update(UpdateDriverRequest req){
        Driver updated = driverRepo.findById(req.id()).orElseThrow(
                () -> new ResourceNotFoundException("Client not found !")
        );
        updated.setName(req.name());
        updated.setPlateNumber(req.plateNumber());

        updated =  driverRepo.save(updated);
        return driverMapper.toDto(updated);
    }

    @Override
    public PagedResponse<DriverResponse> getAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Driver> pageResult =  driverRepo.findAll(pageable);
        List<DriverResponse> data = pageResult.getContent().stream().map(driverMapper::toDto).toList();

        return new PagedResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }

    @Override
    public DriverResponse getById(Long id){
        // Changed exception to ResourceNotFoundException
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found !"));
        return driverMapper.toDto(driver);
    }

    @Override
    public void delete(Long id){
        // Changed exception to ResourceNotFoundException
        driverRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found !"));

        driverRepo.deleteById(id);
    }
}
