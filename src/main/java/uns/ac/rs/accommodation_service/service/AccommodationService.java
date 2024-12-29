package uns.ac.rs.accommodation_service.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uns.ac.rs.accommodation_service.dto.*;
import uns.ac.rs.accommodation_service.dto.request.CreateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.request.UpdateAccommodationRequest;
import uns.ac.rs.accommodation_service.dto.response.MessageResponse;
import uns.ac.rs.accommodation_service.mapper.AccommodationMapper;
import uns.ac.rs.accommodation_service.model.*;
import uns.ac.rs.accommodation_service.model.Accommodation;
import uns.ac.rs.accommodation_service.model.Facility;
import uns.ac.rs.accommodation_service.repository.AccommodationRepository;
import uns.ac.rs.accommodation_service.repository.FacilityRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final FacilityRepository facilityRepository;
    private final UserServiceClient userServiceClient;
    private final AvailabilityService availabilityService;


    @Autowired
    public AccommodationService(AccommodationRepository accommodationRepository,
                                FacilityRepository facilityRepository,
                                UserServiceClient userServiceClient,
                                AvailabilityService availabilityService
                              ) {
        this.accommodationRepository = accommodationRepository;
        this.facilityRepository = facilityRepository;
        this.userServiceClient = userServiceClient;
        this.availabilityService = availabilityService;

    }

    public MessageResponse createAccommodation(CreateAccommodationRequest createAccommodationRequest, String jwtToken) {
        if (createAccommodationRequest.getMaximumGuests() < createAccommodationRequest.getMinimumGuests()) {
            throw new IllegalArgumentException("Maximum guests must be greater than or equal to minimum guests.");
        }

        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }

        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission to create an accommodation.");
        }

        Accommodation newAccommodation = new Accommodation(
                createAccommodationRequest.getName(),
                userDetails.getUsername(),
                createAccommodationRequest.getAddress(),
                createAccommodationRequest.getCity(),
                createAccommodationRequest.getCountry(),
                createAccommodationRequest.getMinimumGuests(),
                createAccommodationRequest.getMaximumGuests(),
                createAccommodationRequest.getPricingStrategy(),
                createAccommodationRequest.getApprovalStrategy()
        );

        Set<Facility> facilities = new HashSet<>(facilityRepository
                .findAllByIdIn(createAccommodationRequest.getFacilityIds()));
        newAccommodation.setFacilities(facilities);
        accommodationRepository.save(newAccommodation);

        return new MessageResponse("Accommodation created successfully.");
    }

    public List<AccommodationDTO> getAllAccommodations() {
        return accommodationRepository.findAll()
                .stream()
                .map(AccommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    public AccommodationDTO getAccommodationById(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));

        return AccommodationMapper.toAccommodationDTO(accommodation);
    }

    public List<AccommodationDTO> getAllAccommodationsByHost(String host) {
        return accommodationRepository.findByHost(host)
                .stream()
                .map(AccommodationMapper::toAccommodationDTO)
                .collect(Collectors.toList());
    }

    public MessageResponse updateAccommodation(UUID accommodationId,
                                               UpdateAccommodationRequest updateAccommodationRequest,
                                               String jwtToken) {
        if (updateAccommodationRequest.getMaximumGuests() < updateAccommodationRequest.getMinimumGuests()) {
            throw new IllegalArgumentException("Maximum guests must be greater than or equal to minimum guests.");
        }

        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }

        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission to update an accommodation.");
        }

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));
        if (!userDetails.getUsername().equals(accommodation.getHost())) {
            throw new SecurityException("User is not the owner of this accommodation.");
        }

        accommodation.setName(updateAccommodationRequest.getName());
        accommodation.setAddress(updateAccommodationRequest.getAddress());
        accommodation.setCity(updateAccommodationRequest.getCity());
        accommodation.setCountry(updateAccommodationRequest.getCountry());
        accommodation.setMinimumGuests(updateAccommodationRequest.getMinimumGuests());
        accommodation.setMaximumGuests(updateAccommodationRequest.getMaximumGuests());
        accommodation.setPricingStrategy(updateAccommodationRequest.getPricingStrategy());
        accommodation.setApprovalStrategy(updateAccommodationRequest.getApprovalStrategy());

        Set<Facility> facilities = new HashSet<>(facilityRepository
                .findAllByIdIn(updateAccommodationRequest.getFacilityIds()));
        accommodation.setFacilities(facilities);

        accommodationRepository.save(accommodation);
        return new MessageResponse("Accommodation updated successfully.");
    }

    //potrebno je doraditi kad se zavrsi ReservationService
    public MessageResponse deleteAccommodation(UUID accommodationId, String jwtToken) {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }

        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission to delete an accommodation.");
        }

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));
        if (!userDetails.getUsername().equals(accommodation.getHost())) {
            throw new SecurityException("User is not the owner of this accommodation.");
        }

        accommodationRepository.delete(accommodation);
        return new MessageResponse("Accommodation deleted successfully.");
    }

    //potrebno je doraditi kad se zavrsi ReservationService
    public MessageResponse deleteAllAccommodationsByHost(String jwtToken) {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }

        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission to delete accommodations.");
        }

        List<Accommodation> accommodations = accommodationRepository.findByHost(userDetails.getUsername());
        if (accommodations.isEmpty()) {
            throw new NoSuchElementException("No accommodations found for the host.");
        }

        accommodationRepository.deleteAll(accommodations);
        return new MessageResponse("All accommodations deleted successfully.");
    }

    public List<SearchAccommodationDTO> searchAccommodations(String city,
                                                             String country,
                                                             Integer guestCount,
                                                             LocalDate startDate,
                                                             LocalDate endDate) {
        Integer daysInRange = Math.toIntExact(ChronoUnit.DAYS.between(startDate, endDate) + 1);

        List<Accommodation> accommodations = accommodationRepository
                .findMatchingAccommodations(city, country, guestCount, startDate, endDate, daysInRange);

        return accommodations.stream()
                .map(accommodation -> {
                    Double totalPrice = 0.0;
                    List<AvailabilityDTO> availabilitiesAll = availabilityService.getAllAvailabilitiesByAccommodation(accommodation.getId());

                    List<AvailabilityDTO> availabilities = availabilitiesAll.stream()
                            .filter(av -> av.getDate() != null &&
                                    !av.getDate().isBefore(startDate) &&
                                    !av.getDate().isAfter(endDate))
                            .toList();

                    Double pricePerGuest = availabilities.stream()
                            .map(AvailabilityDTO::getPricePerGuest)
                            .findFirst()
                            .orElse(null);

                    Double pricePerUnit = availabilities.stream()
                            .map(AvailabilityDTO::getPricePerUnit)
                            .findFirst()
                            .orElse(null);

                    if (accommodation.getPricingStrategy() == EPricingStrategy.PER_UNIT){
                        totalPrice = availabilities.stream()
                                .mapToDouble(AvailabilityDTO::getPricePerUnit)
                                .sum();
                    } else {
                        if (pricePerGuest != null)
                            totalPrice = pricePerGuest * guestCount * daysInRange;
                    }

                    Set<Facility> facilities = accommodation.getFacilities();

                    AccommodationDTO accommodationDTO = AccommodationMapper.toAccommodationDTO(accommodation);

                    return SearchAccommodationDTO.builder()
                            .accommodationDTO(accommodationDTO)
                            .pricePerGuest(pricePerGuest)
                            .pricePerUnit(pricePerUnit)
                            .priceAll(totalPrice)
                            .build();
                })
                .toList();
    }

}
