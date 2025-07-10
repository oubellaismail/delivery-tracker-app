package com.delivery_tracker_app.app.controller.v1;

import com.delivery_tracker_app.app.config.ApiPaths;
import com.delivery_tracker_app.app.dto.v1.common.BaseResponse;
import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;
import com.delivery_tracker_app.app.service.DriverService;
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
@RequestMapping(ApiPaths.BASE+ApiPaths.V1+"/drivers")
public class DriverController {
    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<BaseResponse<DriverResponse>> create(@Valid @RequestBody CreateDriverRequest request){
        DriverResponse res = driverService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/drivers/" + res.id()))
                .body(BaseResponse.ok("Driver created successfully", res));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<DriverResponse>> update(@Valid @RequestBody UpdateDriverRequest request){
        DriverResponse res = driverService.update(request);
        return ResponseEntity.ok(BaseResponse.ok("Driver updated successfully", res));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PagedResponse<DriverResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ){
        PagedResponse<DriverResponse> res = driverService.getAll(page, size);
        return ResponseEntity.ok(BaseResponse.ok("Drivers retrieved successfully", res));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<DriverResponse>> getById(@PathVariable @Min(1) Long id){
        DriverResponse res = driverService.getById(id);
        return ResponseEntity.ok(BaseResponse.ok("Driver retrieved successfully", res));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable @Min(1) Long id){
        driverService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Driver deleted successfully", null));
    }

}
