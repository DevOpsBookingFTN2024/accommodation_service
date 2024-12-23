package uns.ac.rs.accommodation_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import uns.ac.rs.accommodation_service.model.EApprovalStrategy;
import uns.ac.rs.accommodation_service.model.EPricingStrategy;
import java.util.Set;
import java.util.UUID;

@Data
public class UpdateAccommodationRequest {
    @NotBlank(message = "Name is required.")
    @Size(min = 5, max = 50)
    private String name;

    @NotBlank(message = "Address is required.")
    @Size(min = 5, max = 50)
    private String address;

    @NotBlank(message = "City is required.")
    @Size(min = 5, max = 50)
    private String city;

    @NotBlank(message = "Country is required.")
    @Size(min = 5, max = 50)
    private String country;

    @NotNull
    @Min(1)
    private Integer minimumGuests;

    @NotNull
    @Min(1)
    private Integer maximumGuests;

    @NotNull
    private EPricingStrategy pricingStrategy;

    @NotNull
    private EApprovalStrategy approvalStrategy;

    private Set<UUID> facilityIds;
}
