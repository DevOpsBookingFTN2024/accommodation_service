package uns.ac.rs.accommodation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uns.ac.rs.accommodation_service.model.Facility;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

public interface FacilityRepository extends JpaRepository<Facility, UUID> {
    boolean existsByName(String name);

    Set<Facility> findAllByIdIn(Set<UUID> ids);
}
