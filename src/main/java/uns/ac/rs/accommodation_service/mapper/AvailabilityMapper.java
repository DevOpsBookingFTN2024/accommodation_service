package uns.ac.rs.accommodation_service.mapper;

import uns.ac.rs.accommodation_service.dto.AvailabilityDTO;
import uns.ac.rs.accommodation_service.model.Availability;

public class AvailabilityMapper {
    public static AvailabilityDTO toAvailabilityDTO(Availability availability) {
        return AvailabilityDTO.builder()
                .id(availability.getId())
                .date(availability.getDate())
                .isAvailable(availability.getIsAvailable())
                .isReserved(availability.getIsReserved())
                .pricePerGuest(availability.getPricePerGuest())
                .pricePerUnit(availability.getPricePerUnit())
                .build();
    }
}
