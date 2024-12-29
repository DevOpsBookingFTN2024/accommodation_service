package uns.ac.rs.accommodation_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uns.ac.rs.accommodation_service.dto.AccommodationDTO;
import uns.ac.rs.accommodation_service.dto.SearchAccommodationDTO;
import uns.ac.rs.accommodation_service.dto.request.CreateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.request.UpdateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.response.MessageResponse;
import uns.ac.rs.accommodation_service.service.AccommodationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    @Autowired
    private AccommodationService accommodationService;

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createAccommodation(@ModelAttribute CreateAccommodationRequest createAccommodationRequest,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = accommodationService.createAccommodation(createAccommodationRequest, jwtToken);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccommodations() {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/{accommodationId}")
    public ResponseEntity<?> getAccommodationById(@PathVariable UUID accommodationId) {
        AccommodationDTO accommodation = accommodationService.getAccommodationById(accommodationId);
        return ResponseEntity.ok(accommodation);
    }

    @GetMapping("/all/{host}")
    public ResponseEntity<?> getAllAccommodationsByHost(@PathVariable String host) {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodationsByHost(host);
        return ResponseEntity.ok(accommodations);
    }

    @PutMapping("/update/{accommodationId}")
    public ResponseEntity<?> updateAccommodation(@PathVariable UUID accommodationId,
                                                 @Valid @RequestBody UpdateAccommodationRequest updateAccommodationRequest,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = accommodationService.updateAccommodation(
                accommodationId, updateAccommodationRequest, jwtToken);
        return ResponseEntity.ok(messageResponse );
    }

    @DeleteMapping("/delete/{accommodationId}")
    public ResponseEntity<?> deleteAccommodation(@PathVariable UUID accommodationId,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = accommodationService.deleteAccommodation(accommodationId, jwtToken);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/delete/all-host")
    public ResponseEntity<?> deleteAllAccommodationsByHost(@RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = accommodationService.deleteAllAccommodationsByHost(jwtToken);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAccommodations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam Integer guestCount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SearchAccommodationDTO> searchAccommodationDTOS =
                accommodationService.searchAccommodations(city, country, guestCount, startDate, endDate);
        return ResponseEntity.ok(searchAccommodationDTOS);
    }
}
