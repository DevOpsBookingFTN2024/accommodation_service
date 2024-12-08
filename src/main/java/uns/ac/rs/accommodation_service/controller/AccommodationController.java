package uns.ac.rs.accommodation_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.accommodation_service.dto.request.CreateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.response.MessageResponse;
import uns.ac.rs.accommodation_service.service.AccommodationService;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    @Autowired
    private AccommodationService accommodationService;

    @PostMapping("/create/{id}")
    public ResponseEntity<MessageResponse> createAccommodation(
            @RequestBody CreateAccommodationRequest createAccommodationRequest,
            @PathVariable("id") UUID id) {
        try {
            MessageResponse messageResponse = accommodationService.createAccommodation(createAccommodationRequest, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);  // Status 201 za uspešan kreacija
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Error while creating accommodation: " + e.getMessage()));
        }
    }
}
