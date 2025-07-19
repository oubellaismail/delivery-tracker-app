package com.delivery_tracker_app.app.controller.v1;

import com.delivery_tracker_app.app.config.ApiPaths;
import com.delivery_tracker_app.app.dto.v1.common.BaseResponse;
import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;
import com.delivery_tracker_app.app.exception.ErrorResponse; // Import your custom ErrorResponse
import com.delivery_tracker_app.app.service.DriverService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

// Import Swagger/OpenAPI annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
@Validated
@AllArgsConstructor
@RequestMapping(ApiPaths.BASE+ApiPaths.V1+"/drivers")
@Tag(name = "Driver Management", description = "Operations related to driver entities")
@SecurityRequirement(name = "BearerAuth")
public class DriverController {
    private final DriverService driverService;

    @PostMapping
    @Operation(summary = "Create a new driver", description = "Adds a new driver to the system.")
    @ApiResponse(responseCode = "201", description = "Driver created successfully",
                 content = @Content(schema = @Schema(implementation = BaseResponse.class))) // Document BaseResponse for success
    @ApiResponse(responseCode = "400", description = "Invalid driver data provided",
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<BaseResponse<DriverResponse>> create(@Valid @RequestBody CreateDriverRequest request){
        DriverResponse res = driverService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/drivers/" + res.id()))
                .body(BaseResponse.ok("Driver created successfully", res));
    }

    @PutMapping
    @Operation(summary = "Update an existing driver", description = "Updates details of an existing driver based on their ID.")
    @ApiResponse(responseCode = "200", description = "Driver updated successfully",
                 content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid driver data provided",
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Driver not found", // From ResourceNotFoundException
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<BaseResponse<DriverResponse>> update(@Valid @RequestBody UpdateDriverRequest request){
        DriverResponse res = driverService.update(request);
        return ResponseEntity.ok(BaseResponse.ok("Driver updated successfully", res));
    }

    @GetMapping
    @Operation(summary = "Get all drivers with pagination", description = "Retrieves a paginated list of all drivers.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of drivers",
                 content = @Content(schema = @Schema(implementation = PagedResponse.class))) // Document PagedResponse for success
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<BaseResponse<PagedResponse<DriverResponse>>> getAll(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100)
            @Parameter(description = "Number of items per page (1-100)", example = "10") int size
    ){
        PagedResponse<DriverResponse> res = driverService.getAll(page, size);
        return ResponseEntity.ok(BaseResponse.ok("Drivers retrieved successfully", res));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieves details of a single driver by their unique ID.")
    @ApiResponse(responseCode = "200", description = "Driver found and returned",
                 content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid driver ID format", // For @Min(1) validation
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Driver not found", // From IllegalArgumentException in service
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<BaseResponse<DriverResponse>> getById(
            @PathVariable
            @Parameter(description = "ID of the driver to retrieve", example = "1") @Min(1) Long id){
        DriverResponse res = driverService.getById(id);
        return ResponseEntity.ok(BaseResponse.ok("Driver retrieved successfully", res));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a driver by ID", description = "Deletes a driver record from the system based on their unique ID.")
    @ApiResponse(responseCode = "200", description = "Driver deleted successfully", // Your current impl returns 200 with BaseResponse
                 content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid driver ID format",
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Driver not found",
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable
            @Parameter(description = "ID of the driver to delete", example = "1") @Min(1) Long id){
        driverService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Driver deleted successfully", null));
    }
}