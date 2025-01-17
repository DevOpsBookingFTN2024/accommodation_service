package uns.ac.rs.accommodation_service.controller;

import jakarta.validation.Valid;
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
        MessageResponse messageResponse = availabilityService.createAvailability(
                accommodationId, createAvailabilityRequest, jwtToken);
        return ResponseEntity.ok(messageResponse );
    }

    @GetMapping("/all/{accommodationId}")
    public ResponseEntity<?> getAllAvailabilitiesByAccommodation(@PathVariable UUID accommodationId) {
        List<AvailabilityDTO> availabilities = availabilityService.getAllAvailabilitiesByAccommodation(accommodationId);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/{availabilityId}")
    public ResponseEntity<?> getAvailabilityById(@PathVariable UUID availabilityId) {
        AvailabilityDTO availability = availabilityService.getAvailabilityById(availabilityId);
        return ResponseEntity.ok(availability);
    }

    @PutMapping("/update/{availabilityId}")
    public ResponseEntity<?> updateAvailability(@PathVariable UUID availabilityId,
                                                @Valid @RequestBody UpdateAvailabilityRequest updateAvailabilityRequest,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = availabilityService.updateAvailability(
                availabilityId, updateAvailabilityRequest, jwtToken);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/reserve")
    public ResponseEntity<?> reserveAvailabilities(@RequestBody List<AvailabilityDTO> availabilityDTOs,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = availabilityService.reserveAvailabilities(availabilityDTOs, jwtToken);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/release/{accommodationId}")
    public ResponseEntity<?> releaseAvailabilities(@PathVariable UUID accommodationId,
                                                   @RequestParam LocalDate dateFrom,
                                                   @RequestParam LocalDate dateTo,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = availabilityService.releaseAvailabilities(accommodationId,
                dateFrom, dateTo, jwtToken);
        return ResponseEntity.ok(messageResponse);
    }
}
