package uns.ac.rs.accommodation_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.accommodation_service.dto.AccommodationDTO;
import uns.ac.rs.accommodation_service.dto.FacilityDTO;
import uns.ac.rs.accommodation_service.dto.SearchAccommodationDTO;
import uns.ac.rs.accommodation_service.dto.SelectAccommodationDTO;
import uns.ac.rs.accommodation_service.dto.request.CreateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.request.UpdateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.response.MessageResponse;
import uns.ac.rs.accommodation_service.service.AccommodationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    @Autowired
    private AccommodationService accommodationService;

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createAccommodation(@ModelAttribute CreateAccommodationRequest createAccommodationRequest,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.info("Creating accommodation for current user.");
        MessageResponse messageResponse = accommodationService
                .createAccommodation(createAccommodationRequest, jwtToken);
        log.info("Accommodation created successfully.");
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccommodations() {
        log.info("Fetching all accommodations.");
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        log.info("Fetched {} accommodations.", accommodations.size());
        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/{accommodationId}")
    public ResponseEntity<?> getAccommodationById(@PathVariable UUID accommodationId) {
        log.info("Fetching accommodation with ID: {}", accommodationId);
        AccommodationDTO accommodation = accommodationService.getAccommodationById(accommodationId);
        log.info("Accommodation fetched successfully.");
        return ResponseEntity.ok(accommodation);
    }

    @GetMapping("/host/{accommodationId}")
    public ResponseEntity<?> getAccommodationByIdForHost(@PathVariable UUID accommodationId) {
        log.info("Fetching accommodation for host with ID: {}", accommodationId);
        SearchAccommodationDTO accommodation = accommodationService.getAccommodationByIdForHost(accommodationId);
        log.info("Accommodation fetched successfully.");
        return ResponseEntity.ok(accommodation);
    }

    @GetMapping("/all/{host}")
    public ResponseEntity<?> getAllAccommodationsByHost(@PathVariable String host) {
        log.info("Fetching all accommodations for host: {}", host);
        List<SearchAccommodationDTO> accommodations = accommodationService.getAllAccommodationsByHost(host);
        log.info("Fetched {} accommodations for host: {}", accommodations.size(), host);
        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/allSelect/{host}")
    public ResponseEntity<?> getAllSelectAccommodationsByHost(@PathVariable String host) {
        log.info("Fetching selected accommodations for host: {}", host);
        List<SelectAccommodationDTO> accommodations = accommodationService.getAllSelectAccommodationsByHost(host);
        log.info("Fetched {} selected accommodations for host: {}", accommodations.size(), host);
        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/allSelect")
    public ResponseEntity<?> getAllSelectAccommodations() {
        log.info("Fetching all selected accommodations.");
        List<SelectAccommodationDTO> accommodations = accommodationService.getAllSelectAccommodations();
        log.info("Fetched {} selected accommodations.", accommodations.size());
        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/allFacilities")
    public ResponseEntity<?> getAllFacilities() {
        log.info("Fetching all facilities.");
        List<FacilityDTO> facilities = accommodationService.getAllFacilities();
        log.info("Fetched {} facilities.", facilities.size());
        return ResponseEntity.ok(facilities);
    }

    @PutMapping(value="/update/{accommodationId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateAccommodation(@PathVariable UUID accommodationId,
                                                 @ModelAttribute UpdateAccommodationRequest updateAccommodationRequest,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.info("Updating accommodation with ID: {} .", accommodationId);
        MessageResponse messageResponse = accommodationService
                .updateAccommodation(accommodationId, updateAccommodationRequest, jwtToken);
        log.info("Accommodation with ID: {} updated successfully.", accommodationId);
        return ResponseEntity.ok(messageResponse );
    }

    @DeleteMapping("/delete/{accommodationId}")
    public ResponseEntity<?> deleteAccommodation(@PathVariable UUID accommodationId,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.warn("Deleting accommodation with ID: {}.", accommodationId);
        MessageResponse messageResponse = accommodationService.deleteAccommodation(accommodationId, jwtToken);
        log.info("Accommodation with ID: {} deleted successfully.", accommodationId);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/delete/all-host")
    public ResponseEntity<?> deleteAllAccommodationsByHost(@RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.warn("Deleting all accommodations for deleted host.");
        MessageResponse messageResponse = accommodationService.deleteAllAccommodationsByHost(jwtToken);
        log.info("All accommodations for the deleted host deleted successfully.");
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAccommodations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam Integer guestCount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Searching accommodations in city: {}, country: {}, for {} guests, from {} to {}",
                city, country, guestCount, startDate, endDate);
        List<SearchAccommodationDTO> searchAccommodationDTOS = accommodationService
                .searchAccommodations(city, country, guestCount, startDate.toLocalDate(), endDate.toLocalDate());
        log.info("Found {} accommodations matching search criteria.", searchAccommodationDTOS.size());
        return ResponseEntity.ok(searchAccommodationDTOS);
    }
}
