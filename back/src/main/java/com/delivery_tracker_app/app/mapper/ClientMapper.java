package com.delivery_tracker_app.app.mapper;

import com.delivery_tracker_app.app.dto.v1.client.ClientResponse;
import com.delivery_tracker_app.app.dto.v1.client.CreateClientRequest;
import com.delivery_tracker_app.app.dto.v1.client.UpdateClientRequest;
import com.delivery_tracker_app.app.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public Client toEntity(CreateClientRequest dto){
        return Client.builder()
                .name(dto.name())
                .identityId(dto.identityId())
                .build();
    }

    public Client toEntity(UpdateClientRequest dto){
        return Client.builder()
                .id(dto.id())
                .name(dto.name())
                .identityId(dto.identityId())
                .build();
    }

    public ClientResponse toDto(Client client){
        return new ClientResponse(client.getId(), client.getName(), client.getIdentityId());
    }
}
