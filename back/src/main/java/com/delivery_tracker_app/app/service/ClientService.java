package com.delivery_tracker_app.app.service;


import com.delivery_tracker_app.app.dto.v1.client.ClientMapper;
import com.delivery_tracker_app.app.dto.v1.client.ClientResponse;
import com.delivery_tracker_app.app.dto.v1.client.CreateClientRequest;
import com.delivery_tracker_app.app.dto.v1.client.UpdateClientRequest;
import com.delivery_tracker_app.app.entity.Client;
import com.delivery_tracker_app.app.repository.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    @Autowired
    private final ClientRepo clientRepo;

    public ClientResponse create(CreateClientRequest request){
        Client saved = clientRepo.save(ClientMapper.toEntity(request));
        return ClientMapper.toDto(saved);
    }

    public  ClientResponse update(UpdateClientRequest request){

        if(!clientRepo.existsById(request.id())){
            throw new IllegalArgumentException("Client not found !");
        }

        Client updated = clientRepo.save(ClientMapper.toEntity(request));
        return ClientMapper.toDto(updated);
    }

    public List<ClientResponse> getAll(){
        return clientRepo.findAll().stream().map(ClientMapper::toDto).toList();
    }

    public ClientResponse getById(Long id){
        Client response = clientRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found !"));

        return ClientMapper.toDto(response);
    }

    public void delete(Long id){
        if(!clientRepo.existsById(id)){
            throw new IllegalArgumentException("Client not found !");
        }

        clientRepo.deleteById(id);
    }
}
