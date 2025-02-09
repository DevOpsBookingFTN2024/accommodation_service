package uns.ac.rs.accommodation_service.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.accommodation_service.dto.AvailabilityDTO;
import uns.ac.rs.accommodation_service.dto.request.CreateAvailabilityRequest;
import uns.ac.rs.accommodation_service.dto.request.UpdateAvailabilityRequest;
import uns.ac.rs.accommodation_service.dto.response.MessageResponse;
import uns.ac.rs.accommodation_service.service.AvailabilityService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/availabilities")
public class AvailabilityController {
    @Autowired
    private AvailabilityService availabilityService;

    @PostMapping("/create/{accommodationId}")
    public ResponseEntity<?> createAvailability(@PathVariable UUID accommodationId,
                                                @Valid @RequestBody CreateAvailabilityRequest createAvailabilityRequest,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.info("Creating availability for accommodation ID: {}", accommodationId);
        MessageResponse messageResponse = availabilityService.createAvailability(
                accommodationId, createAvailabilityRequest, jwtToken);
        log.info("Availability created successfully for accommodation ID: {}", accommodationId);
        return ResponseEntity.ok(messageResponse );
    }

    @GetMapping("/all/{accommodationId}")
    public ResponseEntity<?> getAllAvailabilitiesByAccommodation(@PathVariable UUID accommodationId) {
        log.info("Fetching all availabilities for accommodation ID: {}", accommodationId);
        List<AvailabilityDTO> availabilities = availabilityService.getAllAvailabilitiesByAccommodation(accommodationId);
        log.info("Found {} availabilities for accommodation ID: {}", availabilities.size(), accommodationId);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/{availabilityId}")
    public ResponseEntity<?> getAvailabilityById(@PathVariable UUID availabilityId) {
        log.info("Fetching availability with ID: {}", availabilityId);
        AvailabilityDTO availability = availabilityService.getAvailabilityById(availabilityId);
        log.info("Availability fetched successfully for ID: {}", availabilityId);
        return ResponseEntity.ok(availability);
    }

    @PutMapping("/update/{availabilityId}")
    public ResponseEntity<?> updateAvailability(@PathVariable UUID availabilityId,
                                                @Valid @RequestBody UpdateAvailabilityRequest updateAvailabilityRequest,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.info("Updating availability with ID: {}", availabilityId);
        MessageResponse messageResponse = availabilityService.updateAvailability(
                availabilityId, updateAvailabilityRequest, jwtToken);
        log.info("Availability updated successfully for ID: {}", availabilityId);
        return ResponseEntity.ok(messageResponse);
    }

    //endpoint koristi ReservationService
    @PutMapping("/reserve")
    public ResponseEntity<?> reserveAvailabilities(@RequestBody List<AvailabilityDTO> availabilityDTOs,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.info("Reserving {} availabilities", availabilityDTOs.size());
        MessageResponse messageResponse = availabilityService.reserveAvailabilities(availabilityDTOs, jwtToken);
        log.info("Availabilities reserved successfully");
        return ResponseEntity.ok(messageResponse);
    }

    //endpoint koristi ReservationService
    @PutMapping("/release/{accommodationId}")
    public ResponseEntity<?> releaseAvailabilities(@PathVariable UUID accommodationId,
                                                   @RequestParam LocalDate dateFrom,
                                                   @RequestParam LocalDate dateTo,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.info("Releasing availabilities for accommodation ID: {} from {} to {}", accommodationId, dateFrom, dateTo);
        MessageResponse messageResponse = availabilityService
                .releaseAvailabilities(accommodationId, dateFrom, dateTo, jwtToken);
        log.info("Availabilities released successfully for accommodation ID: {}", accommodationId);
        return ResponseEntity.ok(messageResponse);
    }
}
