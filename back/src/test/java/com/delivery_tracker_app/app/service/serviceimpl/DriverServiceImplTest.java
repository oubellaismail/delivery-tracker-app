package com.delivery_tracker_app.app.service.serviceimpl;

import com.delivery_tracker_app.app.dto.v1.common.PagedResponse;
import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.DriverResponse;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;
import com.delivery_tracker_app.app.entity.Driver;
import com.delivery_tracker_app.app.exception.ResourceNotFoundException;
import com.delivery_tracker_app.app.mapper.DriverMapper;
import com.delivery_tracker_app.app.repository.DriverRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DriverServiceImpl class.
 * We use Mockito to mock dependencies (DriverRepo and DriverMapper)
 * to test the business logic of the service in isolation.
 */
@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    // @Mock creates a mock instance of the specified class.
    @Mock
    private DriverRepo driverRepo;

    @Mock
    private DriverMapper driverMapper;

    // @InjectMocks injects the mocked dependencies into the tested class.
    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    @DisplayName("should create a new driver successfully")
    void create_ShouldReturnDriverResponse() {
        // Arrange: Prepare data and mock behavior
        CreateDriverRequest request = new CreateDriverRequest("John Doe", "A12345");
        Driver driverEntity = new Driver();
        driverEntity.setName("John Doe");
        driverEntity.setPlateNumber("A12345");

        Driver savedDriver = new Driver();
        savedDriver.setId(1L);
        savedDriver.setName("John Doe");
        savedDriver.setPlateNumber("A12345");

        DriverResponse expectedResponse = new DriverResponse(1L, "John Doe", "A12345");

        // When the mapper's toEntity method is called with any CreateDriverRequest,
        // return the prepared driverEntity.
        when(driverMapper.toEntity(any(CreateDriverRequest.class))).thenReturn(driverEntity);
        // When the repository's save method is called with any Driver,
        // return the savedDriver with a generated ID.
        when(driverRepo.save(any(Driver.class))).thenReturn(savedDriver);
        // When the mapper's toDto method is called with the savedDriver,
        // return the expected DTO response.
        when(driverMapper.toDto(any(Driver.class))).thenReturn(expectedResponse);

        // Act: Call the method to be tested
        DriverResponse actualResponse = driverService.create(request);

        // Assert: Verify the result and mock interactions
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        // Verify that the mocked methods were called exactly once with the correct arguments
        verify(driverMapper, times(1)).toEntity(request);
        verify(driverRepo, times(1)).save(driverEntity);
        verify(driverMapper, times(1)).toDto(savedDriver);
    }

    @Test
    @DisplayName("should update an existing driver successfully")
    void update_ShouldReturnDriverResponse() {
        // Arrange
        UpdateDriverRequest request = new UpdateDriverRequest(1L, "Jane Smith", "B67890");
        Driver existingDriver = new Driver();
        existingDriver.setId(1L);
        existingDriver.setName("John Doe");
        existingDriver.setPlateNumber("A12345");

        Driver updatedDriver = new Driver();
        updatedDriver.setId(1L);
        updatedDriver.setName("Jane Smith");
        updatedDriver.setPlateNumber("B67890");

        DriverResponse expectedResponse = new DriverResponse(1L, "Jane Smith", "B67890");

        when(driverRepo.findById(1L)).thenReturn(Optional.of(existingDriver));
        when(driverRepo.save(any(Driver.class))).thenReturn(updatedDriver);
        when(driverMapper.toDto(any(Driver.class))).thenReturn(expectedResponse);

        // Act
        DriverResponse actualResponse = driverService.update(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        // Verify that the correct methods were called
        verify(driverRepo, times(1)).findById(1L);
        verify(driverRepo, times(1)).save(existingDriver);
        verify(driverMapper, times(1)).toDto(updatedDriver);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if driver to update is not found")
    void update_ShouldThrowExceptionIfNotFound() {
        // Arrange
        UpdateDriverRequest request = new UpdateDriverRequest(99L, "Jane Smith", "B67890");

        when(driverRepo.findById(99L)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> driverService.update(request));

        // Verify only findById was called
        verify(driverRepo, times(1)).findById(99L);
        verify(driverRepo, never()).save(any(Driver.class));
    }

    @Test
    @DisplayName("should return a paginated list of drivers successfully")
    void getAll_ShouldReturnPagedResponse() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Driver driver1 = new Driver();
        driver1.setId(1L);
        Driver driver2 = new Driver();
        driver2.setId(2L);
        List<Driver> driverList = List.of(driver1, driver2);
        Page<Driver> driverPage = new PageImpl<>(driverList, pageable, driverList.size());

        DriverResponse dto1 = new DriverResponse(1L, "Driver 1", "D001");
        DriverResponse dto2 = new DriverResponse(2L, "Driver 2", "D002");
        List<DriverResponse> dtoList = List.of(dto1, dto2);

        when(driverRepo.findAll(pageable)).thenReturn(driverPage);
        when(driverMapper.toDto(driver1)).thenReturn(dto1);
        when(driverMapper.toDto(driver2)).thenReturn(dto2);

        // Act
        PagedResponse<DriverResponse> actualResponse = driverService.getAll(page, size);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.data().size());
        assertEquals(1, actualResponse.totalPages());
        assertEquals(2, actualResponse.totalElements());
        assertEquals(0, actualResponse.page());
        assertEquals(10, actualResponse.size());
        assertTrue(actualResponse.last());

        verify(driverRepo, times(1)).findAll(pageable);
        verify(driverMapper, times(1)).toDto(driver1);
        verify(driverMapper, times(1)).toDto(driver2);
    }

    @Test
    @DisplayName("should return a driver by ID successfully")
    void getById_ShouldReturnDriverResponse() {
        // Arrange
        Long driverId = 1L;
        Driver driver = new Driver();
        driver.setId(driverId);
        DriverResponse expectedResponse = new DriverResponse(driverId, "John Doe", "A12345");

        when(driverRepo.findById(driverId)).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(expectedResponse);

        // Act
        DriverResponse actualResponse = driverService.getById(driverId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(driverRepo, times(1)).findById(driverId);
        verify(driverMapper, times(1)).toDto(driver);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException if driver by ID is not found")
    void getById_ShouldThrowExceptionIfNotFound() {
        // Arrange
        Long driverId = 99L;
        when(driverRepo.findById(driverId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> driverService.getById(driverId));

        verify(driverRepo, times(1)).findById(driverId);
        verify(driverMapper, never()).toDto(any(Driver.class));
    }

    @Test
    @DisplayName("should delete a driver by ID successfully")
    void delete_ShouldDeleteDriver() {
        // Arrange
        Long driverId = 1L;
        when(driverRepo.findById(driverId)).thenReturn(Optional.of(new Driver()));
        doNothing().when(driverRepo).deleteById(driverId);

        // Act
        assertDoesNotThrow(() -> driverService.delete(driverId));

        // Assert
        verify(driverRepo, times(1)).findById(driverId);
        verify(driverRepo, times(1)).deleteById(driverId);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when deleting a non-existent driver")
    void delete_ShouldThrowExceptionIfNotFound() {
        // Arrange
        Long driverId = 99L;
        when(driverRepo.findById(driverId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> driverService.delete(driverId));

        // Verify that findById was called, but deleteById was not
        verify(driverRepo, times(1)).findById(driverId);
        verify(driverRepo, never()).deleteById(anyLong());
    }
}
