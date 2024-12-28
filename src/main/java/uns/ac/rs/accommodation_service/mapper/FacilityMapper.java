package uns.ac.rs.accommodation_service.mapper;

import uns.ac.rs.accommodation_service.dto.FacilityDTO;
import uns.ac.rs.accommodation_service.model.Facility;

public class FacilityMapper {
    public static FacilityDTO toFacilityDTO(Facility facility) {
        return FacilityDTO.builder()
                .id(facility.getId())
                .name(facility.getName())
                .build();
    }
}
