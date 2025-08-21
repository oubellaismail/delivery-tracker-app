package com.delivery_tracker_app.app.repository;

import com.delivery_tracker_app.app.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client, Long> {

}
