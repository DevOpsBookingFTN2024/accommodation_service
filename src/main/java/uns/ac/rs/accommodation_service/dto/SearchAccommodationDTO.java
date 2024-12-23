package uns.ac.rs.accommodation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAccommodationDTO {
    private AccommodationDTO accommodationDTO;

    private Double pricePerGuest;

    private Double pricePerUnit;

    private Double priceAll;
}
