package uns.ac.rs.accommodation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.accommodation_service.model.Accommodation;
import uns.ac.rs.accommodation_service.model.Availability;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    List<Availability> findByAccommodation(Accommodation accommodation);

    boolean existsByAccommodationAndDate(Accommodation accommodation, LocalDate date);
}
