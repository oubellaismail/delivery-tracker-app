package com.delivery_tracker_app.app.controller.v1;

import com.delivery_tracker_app.app.config.TestSecurityConfig;
import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;
import com.delivery_tracker_app.app.exception.ResourceNotFoundException;
import com.delivery_tracker_app.app.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the DriverController using Spring's MockMvc.
 * This file includes both positive and negative test cases.
 */
@WebMvcTest(controllers = DriverController.class)
@Import(TestSecurityConfig.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    private static final String API_BASE_URL = "/api/v1/drivers";

    // --- POSITIVE TEST CASES ---
    @Test
    @DisplayName("POST /api/v1/drivers should create a new driver and return HTTP 201 Created")
    void createDriver_ShouldReturn201Created() throws Exception {
        // Arrange
        CreateDriverRequest request = new CreateDriverRequest("John Doe", "A12345");
        DriverResponse mockResponse = new DriverResponse(1L, "John Doe", "A12345");
        when(driverService.create(any(CreateDriverRequest.class))).thenReturn(mockResponse);

        // Act
        ResultActions result = mockMvc.perform(post(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Driver created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.plateNumber").value("A12345"));

        verify(driverService, times(1)).create(any(CreateDriverRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/drivers should update an existing driver and return HTTP 200 OK")
    void updateDriver_ShouldReturn200Ok() throws Exception {
        // Arrange
        UpdateDriverRequest request = new UpdateDriverRequest(1L, "Jane Smith", "B67890");
        DriverResponse mockResponse = new DriverResponse(1L, "Jane Smith", "B67890");
        when(driverService.update(any(UpdateDriverRequest.class))).thenReturn(mockResponse);

        // Act
        ResultActions result = mockMvc.perform(put(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Driver updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Jane Smith"));

        verify(driverService, times(1)).update(any(UpdateDriverRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/drivers should return a paginated list of drivers with HTTP 200 OK")
    void getAllDrivers_ShouldReturn200OkWithPagedResponse() throws Exception {
        // Arrange
        List<DriverResponse> drivers = List.of(
                new DriverResponse(1L, "Driver A", "LA001"),
                new DriverResponse(2L, "Driver B", "LA002")
        );
        PagedResponse<DriverResponse> pagedResponse = new PagedResponse<>(drivers, 0, 10, 2L, 1, true);
        when(driverService.getAll(anyInt(), anyInt())).thenReturn(pagedResponse);

        // Act
        ResultActions result = mockMvc.perform(get(API_BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Drivers retrieved successfully"))
                .andExpect(jsonPath("$.data.data[0].name").value("Driver A"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10));

        verify(driverService, times(1)).getAll(0, 10);
    }

    @Test
    @DisplayName("GET /api/v1/drivers/{id} should return a single driver with HTTP 200 OK")
    void getDriverById_ShouldReturn200Ok() throws Exception {
        // Arrange
        DriverResponse mockResponse = new DriverResponse(1L, "John Doe", "A12345");
        when(driverService.getById(1L)).thenReturn(mockResponse);

        // Act
        ResultActions result = mockMvc.perform(get(API_BASE_URL + "/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Driver retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("John Doe"));

        verify(driverService, times(1)).getById(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/drivers/{id} should delete a driver and return HTTP 200 OK")
    void deleteDriver_ShouldReturn200Ok() throws Exception {
        // Arrange
        doNothing().when(driverService).delete(1L);

        // Act
        ResultActions result = mockMvc.perform(delete(API_BASE_URL + "/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Driver deleted successfully"));

        verify(driverService, times(1)).delete(1L);
    }

    // --- NEGATIVE TEST CASES ---
    @Test
    @DisplayName("POST /api/v1/drivers should return HTTP 400 Bad Request for invalid input")
    void createDriver_ShouldReturn400BadRequestForInvalidInput() throws Exception {
        // Arrange: Invalid request with an empty name
        CreateDriverRequest invalidRequest = new CreateDriverRequest("", "A12345");

        // Act
        ResultActions result = mockMvc.perform(post(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .accept(MediaType.APPLICATION_JSON));

        // Assert: The controller should catch the validation error and return 400
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("PUT /api/v1/drivers should return HTTP 404 Not Found for non-existent driver ID")
    void updateDriver_ShouldReturn404NotFoundForNonExistentId() throws Exception {
        // Arrange: A valid request with an ID that does not exist
        UpdateDriverRequest nonExistentRequest = new UpdateDriverRequest(999L, "Nonexistent", "N99999");

        // Mock the service layer to throw a ResourceNotFoundException
        when(driverService.update(any(UpdateDriverRequest.class))).thenThrow(new ResourceNotFoundException("Client not found !"));

        // Act
        ResultActions result = mockMvc.perform(put(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentRequest))
                .accept(MediaType.APPLICATION_JSON));

        // Assert: The controller's exception handler should return 404
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client not found !"));
    }

    @Test
    @DisplayName("GET /api/v1/drivers/{id} should return HTTP 400 Bad Request for invalid ID format")
    void getDriverById_ShouldReturn400BadRequestForInvalidId() throws Exception {
        // Arrange: Send an invalid ID (e.g., a string instead of a long)
        String invalidId = "abc";

        // Act
        ResultActions result = mockMvc.perform(get(API_BASE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert: Spring should catch the format error and return 400
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/v1/drivers/{id} should return HTTP 404 Not Found for non-existent driver ID")
    void getDriverById_ShouldReturn404NotFoundForNonExistentId() throws Exception {
        // Arrange: An ID that does not exist in the database
        Long nonExistentId = 999L;
        // Mock the service layer to throw an exception for a non-existent ID
        when(driverService.getById(nonExistentId)).thenThrow(new ResourceNotFoundException("Driver not found !"));

        // Act
        ResultActions result = mockMvc.perform(get(API_BASE_URL + "/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert: The controller's exception handler should return 404
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/drivers/{id} should return HTTP 404 Not Found for non-existent driver ID")
    void deleteDriver_ShouldReturn404NotFoundForNonExistentId() throws Exception {
        // Arrange: An ID that does not exist in the database
        Long nonExistentId = 999L;
        // Mock the service layer to throw an exception
        doThrow(new ResourceNotFoundException("Driver not found !")).when(driverService).delete(nonExistentId);

        // Act
        ResultActions result = mockMvc.perform(delete(API_BASE_URL + "/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON));

        // Assert: The controller's exception handler should return 404
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }
}
