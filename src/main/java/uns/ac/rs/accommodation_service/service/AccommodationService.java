package uns.ac.rs.accommodation_service.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import uns.ac.rs.accommodation_service.dto.UserDTO;
import uns.ac.rs.accommodation_service.dto.request.CreateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.response.MessageResponse;
import uns.ac.rs.accommodation_service.model.Accommodation;
import uns.ac.rs.accommodation_service.repository.AccommodationRepository;

import java.util.UUID;

@Service
@Transactional
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final UserServiceClient userServiceClient;

    public AccommodationService(AccommodationRepository accommodationRepository, UserServiceClient userServiceClient) {
        this.accommodationRepository = accommodationRepository;
        this.userServiceClient = userServiceClient;
    }

    public MessageResponse createAccommodation(CreateAccommodationRequest createAccommodationRequest, UUID id) {

        UserDTO userDetails = null;
        try {
            userDetails = userServiceClient.getUserDetails(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user details: " + e.getMessage());
        }

        if (userDetails == null || userDetails.getId() == null) {
            throw new RuntimeException("User details could not be retrieved");
        }

        Accommodation newAccommodation = new Accommodation(
                createAccommodationRequest.getName(),
                userDetails.getId()
        );

        accommodationRepository.save(newAccommodation);

        return new MessageResponse("Accommodation created successfully.");
    }
}

