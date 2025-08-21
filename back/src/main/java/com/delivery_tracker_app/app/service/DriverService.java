package com.delivery_tracker_app.app.service;

import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;

public interface DriverService {
    DriverResponse create(CreateDriverRequest req);
    DriverResponse update(UpdateDriverRequest req);
    PagedResponse<DriverResponse> getAll(int page, int size);
    DriverResponse getById(Long id);
    void delete(Long id);
}
