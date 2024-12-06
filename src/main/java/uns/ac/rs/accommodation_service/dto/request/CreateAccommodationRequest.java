package uns.ac.rs.accommodation_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccommodationRequest {
    @NotBlank
    private String name;
}
