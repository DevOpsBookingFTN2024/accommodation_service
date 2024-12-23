package uns.ac.rs.accommodation_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "availabilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_column")
    private LocalDate date;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "is_reserved")
    private Boolean isReserved;

    @Column(name = "price_per_guest")
    private Double pricePerGuest;

    @Column(name = "price_per_unit")
    private Double pricePerUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_accommodation")
    private Accommodation accommodation;

    public Availability(LocalDate date,
                        Double pricePerGuest,
                        Double pricePerUnit) {
        this.date = date;
        this.pricePerGuest = pricePerGuest;
        this.pricePerUnit = pricePerUnit;
    }
}
