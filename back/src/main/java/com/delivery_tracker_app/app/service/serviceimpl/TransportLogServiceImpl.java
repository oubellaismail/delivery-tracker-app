package com.delivery_tracker_app.app.service.serviceimpl;

import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.CreateTransportLogRequest;
import com.delivery_tracker_app.app.dto.v1.transportLog.TransportLogResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.UpdateTransportLogRequest;
import com.delivery_tracker_app.app.entity.Client;
import com.delivery_tracker_app.app.entity.Driver;
import com.delivery_tracker_app.app.entity.TransportLog;
import com.delivery_tracker_app.app.exception.ResourceNotFoundException;
import com.delivery_tracker_app.app.mapper.TransportLogMapper;
import com.delivery_tracker_app.app.repository.ClientRepo;
import com.delivery_tracker_app.app.repository.DriverRepo;
import com.delivery_tracker_app.app.repository.TransportLogRepo;
import com.delivery_tracker_app.app.service.TransportLogService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransportLogServiceImpl implements TransportLogService {

    private final TransportLogRepo transportLogRepo;
    private final ClientRepo clientRepo;
    private final DriverRepo driverRepo;
    private final TransportLogMapper transportLogMapper;

    @Transactional
    @Override
    public TransportLogResponse create(CreateTransportLogRequest request) {
        Client client = clientRepo.findById(request.clientId()).orElseThrow(
                () -> new ResourceNotFoundException("Client not found !")
        );

        Driver driver = driverRepo.findById(request.driverId()).orElseThrow(
                () -> new ResourceNotFoundException("Driver not found !")
        );

        TransportLog saved = transportLogRepo.save(transportLogMapper.toEntity(request, client, driver));
        return transportLogMapper.toDto(saved);
    }

    @Transactional
    @Override
    public TransportLogResponse update(UpdateTransportLogRequest request) {
        TransportLog existing = transportLogRepo.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Transport log not found!"));

        Client client = clientRepo.findById(request.clientId()).orElseThrow(
                () -> new ResourceNotFoundException("Client not found !")
        );

        Driver driver = driverRepo.findById(request.driverId()).orElseThrow(
                () -> new ResourceNotFoundException("Driver not found !")
        );

        TransportLog updated = transportLogRepo.save(transportLogMapper.toEntity(request, client, driver));
        return transportLogMapper.toDto(updated);
    }

    @Override
    public PagedResponse<TransportLogResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransportLog> pageResult =  transportLogRepo.findAll(pageable);
        List<TransportLogResponse> data = pageResult.getContent().stream().map(transportLogMapper::toDto).toList();

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
    public TransportLogResponse getById(Long id) {
        TransportLog transportLog = transportLogRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Transport log not found !")
        );
        return transportLogMapper.toDto(transportLog);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        TransportLog existing = transportLogRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transport log not found!"));

        transportLogRepo.delete(existing);
    }
}
