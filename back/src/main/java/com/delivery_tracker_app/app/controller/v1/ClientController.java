package com.delivery_tracker_app.app.controller.v1;

import com.delivery_tracker_app.app.dto.v1.client.ClientResponse;
import com.delivery_tracker_app.app.dto.v1.client.CreateClientRequest;
import com.delivery_tracker_app.app.dto.v1.client.UpdateClientRequest;
import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.service.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;;

@RestController
@Validated
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
    public ResponseEntity<PagedResponse<ClientResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ){
        return ResponseEntity.ok(clientService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getById(@PathVariable @Min(1) Long id){
        return ResponseEntity.ok(clientService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id){
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
