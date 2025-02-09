package uns.ac.rs.accommodation_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.accommodation_service.dto.PhotoDTO;
import uns.ac.rs.accommodation_service.service.PhotoService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;
    // private final Path externalFolder =  Paths.get("/app/uploads");

    private final Path externalFolder = Paths.get("uploads");

    @GetMapping("/all/{accommodationId}")
    public ResponseEntity<?> getAllPhotosByAccommodation(@PathVariable UUID accommodationId) {
        List<PhotoDTO> photos = photoService.getAllPhotosByAccommodation(accommodationId);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = externalFolder.resolve(filename).normalize();
            Resource fileResource = new UrlResource(filePath.toUri());
            if (!fileResource.exists() || !fileResource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +
                            fileResource.getFilename() + "\"")
                    .body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
