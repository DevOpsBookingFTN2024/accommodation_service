package uns.ac.rs.accommodation_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accommodations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "host")
    private String host;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "minimum_guests")
    private Integer minimumGuests;

    @Column(name = "maximum_guests")
    private Integer maximumGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_strategy")
    private EPricingStrategy pricingStrategy;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_strategy")
    private EApprovalStrategy approvalStrategy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "accommodations_facilities",
            joinColumns = @JoinColumn(name = "id_accommodation"),
            inverseJoinColumns = @JoinColumn(name = "id_facility"))
    private Set<Facility> facilities = new HashSet<>();

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private Set<Photo> photos = new HashSet<>();

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private Set<Availability> availabilities = new HashSet<>();

    public Accommodation(String name,
                         String host,
                         String address,
                         String city,
                         String country,
                         Integer minimumGuests,
                         Integer maximumGuests,
                         EPricingStrategy pricingStrategy,
                         EApprovalStrategy approvalStrategy) {
        this.name = name;
        this.host = host;
        this.address = address;
        this.city = city;
        this.country = country;
        this.minimumGuests = minimumGuests;
        this.maximumGuests = maximumGuests;
        this.pricingStrategy = pricingStrategy;
        this.approvalStrategy = approvalStrategy;
    }
}
