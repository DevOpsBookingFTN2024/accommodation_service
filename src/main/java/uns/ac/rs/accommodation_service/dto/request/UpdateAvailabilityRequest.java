package uns.ac.rs.accommodation_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAvailabilityRequest {
    @NotNull(message = "Availability status is required.")
    private Boolean isAvailable;

    @DecimalMin(value = "0.1", inclusive = true, message = "Price per guest must be at least 0.1.")
    private Double pricePerGuest;

    @DecimalMin(value = "0.1", inclusive = true, message = "Price per unit must be at least 0.1.")
    private Double pricePerUnit;
}
