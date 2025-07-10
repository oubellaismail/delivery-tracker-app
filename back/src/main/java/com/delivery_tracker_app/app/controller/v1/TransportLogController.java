package com.delivery_tracker_app.app.controller.v1;

import com.delivery_tracker_app.app.config.ApiPaths;
import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.CreateTransportLogRequest;
import com.delivery_tracker_app.app.dto.v1.transportLog.TransportLogResponse;
import com.delivery_tracker_app.app.dto.v1.transportLog.UpdateTransportLogRequest;
import com.delivery_tracker_app.app.service.TransportLogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(ApiPaths.BASE+ApiPaths.V1+"/trans_logs")
public class TransportLogController {
    private TransportLogService transportLogService;

    @PostMapping
    public ResponseEntity<TransportLogResponse> create(@Valid @RequestBody CreateTransportLogRequest request){
        TransportLogResponse res = transportLogService.create(request);
        URI location = URI.create(ApiPaths.BASE+ApiPaths.V1+"/drivers/"+res.id());
        return ResponseEntity.created(location).body(res);
    }

    @PutMapping
    public ResponseEntity<TransportLogResponse> update(@Valid @RequestBody UpdateTransportLogRequest request){
        return ResponseEntity.ok(transportLogService.update(request));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TransportLogResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(30)int size
    ){
        return ResponseEntity.ok(transportLogService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportLogResponse> getById(@PathVariable @Min(1) Long id){
        return ResponseEntity.ok(transportLogService.getById(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id){
        transportLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
