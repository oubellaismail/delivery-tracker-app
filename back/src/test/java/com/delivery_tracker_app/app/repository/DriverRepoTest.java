package com.delivery_tracker_app.app.repository;


import com.delivery_tracker_app.app.entity.Driver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

/**
 * Repository tests for the DriverRepo interface.
 * The @DataJpaTest annotation configures an in-memory database for testing
 * and only loads the components related to JPA. This makes the tests fast
 * and isolated from the rest of the application.
 *
 * It uses TestEntityManager to interact with the database, which is a
 * convenient way to set up and tear down test data.
 */
@DataJpaTest
@ActiveProfiles("test") // Use a dedicated test profile if needed
class DriverRepoTest {

    // The repository to be tested, injected by Spring.
    @Autowired
    private DriverRepo driverRepo;

    // TestEntityManager is used to interact with the underlying database
    // directly, allowing us to manage test data.
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("should save a driver and retrieve it by ID")
    void saveAndFindById_ShouldReturnSavedDriver() {
        // Arrange
        Driver driver = new Driver();
        driver.setName("John Doe");
        driver.setPlateNumber("A12345");

        // Act
        Driver savedDriver = driverRepo.save(driver);
        Optional<Driver> foundDriver = driverRepo.findById(savedDriver.getId());

        // Assert
        assertThat(foundDriver).isPresent();
        assertThat(foundDriver.get().getName()).isEqualTo("John Doe");
        assertThat(foundDriver.get().getPlateNumber()).isEqualTo("A12345");
    }

    @Test
    @DisplayName("should find all drivers")
    void findAll_ShouldReturnAllDrivers() {
        // Arrange
        Driver driver1 = new Driver();
        driver1.setName("John Doe");
        driver1.setPlateNumber("A12345");
        entityManager.persist(driver1);

        Driver driver2 = new Driver();
        driver2.setName("Jane Smith");
        driver2.setPlateNumber("B67890");
        entityManager.persist(driver2);

        // Act
        List<Driver> drivers = driverRepo.findAll();

        // Assert
        assertThat(drivers).hasSize(2);
        assertThat(drivers).extracting(Driver::getName).containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    @DisplayName("should delete a driver by ID")
    void deleteById_ShouldRemoveDriver() {
        // Arrange
        Driver driver = new Driver();
        driver.setName("John Doe");
        driver.setPlateNumber("A12345");
        entityManager.persistAndFlush(driver);

        // Act
        driverRepo.deleteById(driver.getId());
        Optional<Driver> deletedDriver = driverRepo.findById(driver.getId());

        // Assert
        assertThat(deletedDriver).isNotPresent();
    }

    @Test
    @DisplayName("should throw an exception when finding a non-existent driver")
    void findById_ShouldReturnEmptyOptionalForNonExistentId() {
        // Arrange
        Long nonExistentId = 99L;

        // Act
        Optional<Driver> foundDriver = driverRepo.findById(nonExistentId);

        // Assert
        assertThat(foundDriver).isNotPresent();
    }
}

