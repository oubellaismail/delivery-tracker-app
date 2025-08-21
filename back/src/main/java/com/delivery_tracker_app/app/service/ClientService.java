package com.delivery_tracker_app.app.service;

import com.delivery_tracker_app.app.dto.v1.client.ClientResponse;
import com.delivery_tracker_app.app.dto.v1.client.CreateClientRequest;
import com.delivery_tracker_app.app.dto.v1.client.UpdateClientRequest;
import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;

public interface ClientService {
    ClientResponse create(CreateClientRequest request);
    ClientResponse update(UpdateClientRequest request);
    PagedResponse<ClientResponse> getAll(int page, int size);
    ClientResponse getById(Long id);
    void delete(Long id);
}
