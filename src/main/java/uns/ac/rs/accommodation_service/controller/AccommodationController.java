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

}
