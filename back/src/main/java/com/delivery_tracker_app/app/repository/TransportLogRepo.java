package com.delivery_tracker_app.app.repository;

import com.delivery_tracker_app.app.entity.TransportLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportLogRepo extends JpaRepository<TransportLog, Long> {
}
