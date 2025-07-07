package com.delivery_tracker_app.app.repository;

import com.delivery_tracker_app.app.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepo extends JpaRepository<Driver, Long> {
}
