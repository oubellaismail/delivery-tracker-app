package com.delivery_tracker_app.app.integration.v1;

import com.delivery_tracker_app.app.dto.v1.driver.CreateDriverRequest;
import com.delivery_tracker_app.app.dto.v1.driver.UpdateDriverRequest;
import com.delivery_tracker_app.app.entity.Driver;
import com.delivery_tracker_app.app.repository.DriverRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Driver resource.
 * This test uses @SpringBootTest to load the full application context, including all
 * controllers, services, repositories, and the security configuration.
 *
 * @WithMockUser is used to simulate an authenticated user, allowing access
 * to secured endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
// Provide test properties to resolve all missing placeholders from the application's security configuration
@TestPropertySource(properties = {
        "app.jwt.secret=test-secret-key-that-is-long-enough",
        "app.user.username=testuser",
        "app.user.password=testpass",
        "app.user.roles=ADMIN"
})
class DriverIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DriverRepo driverRepo;

    // A setup method to clear the database before each test
    @BeforeEach
    void setUp() {
        driverRepo.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/drivers should save a new driver and return HTTP 201 Created")
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void createDriver_ShouldSaveToDatabase() throws Exception {
        // Arrange
        CreateDriverRequest request = new CreateDriverRequest("Ali Badr", "Z98765");

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isCreated());

        // Check the actual database to verify persistence
        List<Driver> drivers = driverRepo.findAll();
        assertThat(drivers).hasSize(1);
        assertThat(drivers.get(0).getName()).isEqualTo("Ali Badr");
        assertThat(drivers.get(0).getPlateNumber()).isEqualTo("Z98765");
    }

    @Test
    @DisplayName("PUT /api/v1/drivers should update an existing driver and return HTTP 200 OK")
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void updateDriver_ShouldUpdateExistingDriverInDatabase() throws Exception {
        // Arrange: first, save a driver to the database
        Driver savedDriver = driverRepo.save(Driver.builder().name("Original Name").plateNumber("X11111").build());
        UpdateDriverRequest request = new UpdateDriverRequest(savedDriver.getId(), "Updated Name", "Y22222");

        // Act
        ResultActions result = mockMvc.perform(put("/api/v1/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.plateNumber").value("Y22222"));

        // Verify that the driver was actually updated in the database
        assertThat(driverRepo.findById(savedDriver.getId()).get().getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("GET /api/v1/drivers should return a list of all drivers with HTTP 200 OK")
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void getAllDrivers_ShouldReturnAllDriversFromDatabase() throws Exception {
        // Arrange: save a few drivers to the database
        driverRepo.save(Driver.builder().name("Driver One").plateNumber("D11111").build());
        driverRepo.save(Driver.builder().name("Driver Two").plateNumber("D22222").build());

        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/drivers")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.data.length()").value(2))
                .andExpect(jsonPath("$.data.data[0].name").value("Driver One"));
    }

    @Test
    @DisplayName("GET /api/v1/drivers/{id} should return a driver by its ID with HTTP 200 OK")
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void getDriverById_ShouldReturnCorrectDriver() throws Exception {
        // Arrange: save a driver to the database
        Driver savedDriver = driverRepo.save(Driver.builder().name("Unique Driver").plateNumber("U99999").build());

        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/drivers/{id}", savedDriver.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Unique Driver"));
    }

    @Test
    @DisplayName("DELETE /api/v1/drivers/{id} should delete a driver and return HTTP 200 OK")
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void deleteDriver_ShouldDeleteFromDatabase() throws Exception {
        // Arrange: save a driver to the database
        Driver savedDriver = driverRepo.save(Driver.builder().name("To Delete").plateNumber("DEL111").build());

        // Act
        ResultActions result = mockMvc.perform(delete("/api/v1/drivers/{id}", savedDriver.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());

        // Verify the driver is no longer in the database
        assertThat(driverRepo.findById(savedDriver.getId())).isEmpty();
    }
}
