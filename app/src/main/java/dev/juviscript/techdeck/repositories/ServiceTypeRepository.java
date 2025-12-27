package dev.juviscript.techdeck.repositories;

import dev.juviscript.techdeck.models.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, UUID> {

    Optional<ServiceType> findByName(String name);

    boolean existsByName(String name);

    List<ServiceType> findByIsActiveTrue();
}
