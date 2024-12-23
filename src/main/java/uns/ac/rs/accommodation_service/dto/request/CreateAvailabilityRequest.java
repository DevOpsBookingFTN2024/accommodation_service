package uns.ac.rs.accommodation_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateAvailabilityRequest {
    @NotNull(message = "Start date is required.")
    private LocalDate dateFrom;

    @NotNull(message = "End date is required.")
    private LocalDate dateTo;

    @DecimalMin(value = "0.1", inclusive = true, message = "Price per guest must be at least 0.1.")
    private Double pricePerGuest;

    @DecimalMin(value = "0.1", inclusive = true, message = "Price per unit must be at least 0.1.")
    private Double pricePerUnit;
}
