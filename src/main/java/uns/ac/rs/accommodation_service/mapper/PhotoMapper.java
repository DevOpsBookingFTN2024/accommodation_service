package uns.ac.rs.accommodation_service.mapper;

import uns.ac.rs.accommodation_service.dto.PhotoDTO;
import uns.ac.rs.accommodation_service.model.Photo;

public class PhotoMapper {
    public static PhotoDTO toPhotoDTO(Photo photo){
        return PhotoDTO.builder()
                .id(photo.getId())
                .url(photo.getUrl())
                .accommodationId(photo.getAccommodation().getId())
                .build();
    }
}
