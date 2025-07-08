package com.delivery_tracker_app.app.controller.v1;

import com.delivery_tracker_app.app.dto.v1.client.ClientResponse;
import com.delivery_tracker_app.app.dto.v1.client.CreateClientRequest;
import com.delivery_tracker_app.app.dto.v1.client.UpdateClientRequest;
import com.delivery_tracker_app.app.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor

public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request){
        ClientResponse response = clientService.create(request);
        URI location = URI.create("/api/v1/clients/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping
    public ResponseEntity<ClientResponse> update(@Valid @RequestBody UpdateClientRequest request){
        return ResponseEntity.ok(clientService.update(request));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAll(){
        return ResponseEntity.ok(clientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(clientService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
