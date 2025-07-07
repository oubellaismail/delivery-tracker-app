package com.delivery_tracker_app.app.dto.client;

import com.delivery_tracker_app.app.entity.Client;

public class ClientMapper {
    public static Client toEntity(CreateClientRequest dto){
        return Client.builder()
                .name(dto.name())
                .identityId(dto.identityId())
                .build();
    }

    public static Client toEntity(UpdateClientRequest dto){
        return Client.builder()
                .id(dto.id())
                .name(dto.name())
                .identityId(dto.identityId())
                .build();
    }

    public static ClientResponse toDto(Client client){
        return new ClientResponse(client.getId(), client.getName(), client.getIdentityId());
    }
}
