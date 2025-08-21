package com.delivery_tracker_app.app.service;

import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.CreateTransportLogRequest;
import com.delivery_tracker_app.app.dto.v1.transportLog.TransportLogResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.UpdateTransportLogRequest;

public interface TransportLogService {
    TransportLogResponse create(CreateTransportLogRequest request);
    TransportLogResponse update(UpdateTransportLogRequest request);
    PagedResponse<TransportLogResponse> getAll(int page, int size);
    TransportLogResponse getById(Long id);
    void delete(Long id);
}
