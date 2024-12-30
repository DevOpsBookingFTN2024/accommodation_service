package uns.ac.rs.accommodation_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.accommodation_service.dto.PhotoDTO;
import uns.ac.rs.accommodation_service.service.PhotoService;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @GetMapping("/all/{accommodationId}")
    public ResponseEntity<?> getAllPhotosByAccommodation(@PathVariable UUID accommodationId) {
        List<PhotoDTO> photos = photoService.getAllPhotosByAccommodation(accommodationId);
        return ResponseEntity.ok(photos);
    }
}
