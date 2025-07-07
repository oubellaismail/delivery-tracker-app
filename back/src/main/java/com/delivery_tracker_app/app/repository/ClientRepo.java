package com.delivery_tracker_app.app.repository;

import com.delivery_tracker_app.app.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<Client, Long> {

}
