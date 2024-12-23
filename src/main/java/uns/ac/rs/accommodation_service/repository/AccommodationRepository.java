package uns.ac.rs.accommodation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uns.ac.rs.accommodation_service.model.Accommodation;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, UUID> {
    List<Accommodation> findByHost(String host);

    @Query("SELECT a FROM Accommodation a " +
            "JOIN a.availabilities av " +
            "WHERE (:city IS NULL OR a.city = :city) " +
            "AND (:country IS NULL OR a.country = :country) " +
            "AND a.minimumGuests <= :guestCount " +
            "AND a.maximumGuests >= :guestCount " +
            "AND av.isAvailable = true " +
            "AND av.isReserved = false " +
            "AND av.date BETWEEN :startDate AND :endDate " +
            "GROUP BY a.id " +
            "HAVING COUNT(av) = :daysInRange " )
    List<Accommodation> findMatchingAccommodations(
            @Param("city") String city,
            @Param("country") String country,
            @Param("guestCount") int guestCount,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("daysInRange") long daysInRange
    );
}
