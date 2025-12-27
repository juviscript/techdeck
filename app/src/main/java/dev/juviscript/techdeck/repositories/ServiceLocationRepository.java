package dev.juviscript.techdeck.repositories;

import dev.juviscript.techdeck.models.ServiceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceLocationRepository extends JpaRepository<ServiceLocation, UUID> {

    List<ServiceLocation> findByCustomerId(UUID customerId);
    void deleteByCustomerId(UUID customerId);
}