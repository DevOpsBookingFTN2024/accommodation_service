package uns.ac.rs.accommodation_service.mapper;

import uns.ac.rs.accommodation_service.dto.AccommodationDTO;
import uns.ac.rs.accommodation_service.model.Accommodation;
import java.util.stream.Collectors;

public class AccommodationMapper {
    public static AccommodationDTO toAccommodationDTO(Accommodation accommodation) {
        return AccommodationDTO.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .host(accommodation.getHost())
                .address(accommodation.getAddress())
                .city(accommodation.getCity())
                .country(accommodation.getCountry())
                .minimumGuests(accommodation.getMinimumGuests())
                .maximumGuests(accommodation.getMaximumGuests())
                .pricingStrategy(accommodation.getPricingStrategy().name())
                .approvalStrategy(accommodation.getApprovalStrategy().name())
                .facilities(accommodation.getFacilities()
                        .stream()
                        .map(FacilityMapper::toFacilityDTO)
                        .collect(Collectors.toSet()))
                .build();
    }
}