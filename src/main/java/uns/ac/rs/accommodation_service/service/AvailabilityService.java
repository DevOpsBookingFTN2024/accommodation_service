package uns.ac.rs.accommodation_service.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import uns.ac.rs.accommodation_service.dto.AvailabilityDTO;
import uns.ac.rs.accommodation_service.dto.UserDTO;
import uns.ac.rs.accommodation_service.dto.request.CreateAvailabilityRequest;
import uns.ac.rs.accommodation_service.dto.request.UpdateAvailabilityRequest;
import uns.ac.rs.accommodation_service.dto.response.MessageResponse;
import uns.ac.rs.accommodation_service.mapper.AvailabilityMapper;
import uns.ac.rs.accommodation_service.model.Accommodation;
import uns.ac.rs.accommodation_service.model.Availability;
import uns.ac.rs.accommodation_service.repository.AccommodationRepository;
import uns.ac.rs.accommodation_service.repository.AvailabilityRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserServiceClient userServiceClient;

    public AvailabilityService(AvailabilityRepository availabilityRepository,
                               AccommodationRepository accommodationRepository,
                               UserServiceClient userServiceClient) {
        this.availabilityRepository = availabilityRepository;
        this.accommodationRepository = accommodationRepository;
        this.userServiceClient = userServiceClient;
    }

    public MessageResponse createAvailability(UUID accommodationId,
                                              CreateAvailabilityRequest createAvailabilityRequest,
                                              String jwtToken) {
        if (createAvailabilityRequest.getDateFrom().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        if (createAvailabilityRequest.getDateTo().isBefore(createAvailabilityRequest.getDateFrom())) {
            throw new IllegalArgumentException("End date must be equal to or after the start date.");
        }
        Set<LocalDate> dates = createAvailabilityRequest.getDateFrom()
                .datesUntil(createAvailabilityRequest.getDateTo())
                .collect(Collectors.toSet());

        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }
        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission for this action.");
        }

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));
        if (!userDetails.getUsername().equals(accommodation.getHost())) {
            throw new SecurityException("User is not the owner of this accommodation.");
        }

        for (LocalDate date : dates) {
            if (availabilityRepository.existsByAccommodationAndDate(accommodation, date)) {
                throw new SecurityException("Availability already exists for date: " + date);
            }

            Availability newAvailability = new Availability(
                    date,
                    createAvailabilityRequest.getPricePerGuest(),
                    createAvailabilityRequest.getPricePerUnit()
            );
            newAvailability.setIsAvailable(true);
            newAvailability.setIsReserved(false);
            newAvailability.setAccommodation(accommodation);

            availabilityRepository.save(newAvailability);
        }

        return new MessageResponse("Availability created successfully.");
    }

    public List<AvailabilityDTO> getAllAvailabilitiesByAccommodation(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));

        return availabilityRepository.findByAccommodation(accommodation)
                .stream()
                .filter(availability -> !availability.getDate().isBefore(LocalDate.now()))
                .map(AvailabilityMapper::toAvailabilityDTO)
                .collect(Collectors.toList());
    }

    public AvailabilityDTO getAvailabilityById(UUID availabilityId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new NoSuchElementException("Availability not found with id: " + availabilityId));

        return AvailabilityMapper.toAvailabilityDTO(availability);
    }

    public MessageResponse updateAvailability(UUID availabilityId,
                                              UpdateAvailabilityRequest updateAvailabilityRequest,
                                              String jwtToken) {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }
        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission for this action.");
        }

        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new NoSuchElementException("Availability not found with id: " + availabilityId));

        Accommodation accommodation = accommodationRepository.findById(availability.getAccommodation().getId())
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: "
                        + availability.getAccommodation().getId()));
        if (!userDetails.getUsername().equals(accommodation.getHost())) {
            throw new SecurityException("User is not the owner of this accommodation.");
        }

        if (availability.getIsReserved()) {
            throw new SecurityException("Cannot modify availability for reserved date.");
        }

        availability.setIsAvailable(updateAvailabilityRequest.getIsAvailable());
        availability.setPricePerGuest(updateAvailabilityRequest.getPricePerGuest());
        availability.setPricePerUnit(updateAvailabilityRequest.getPricePerUnit());

        availabilityRepository.save(availability);
        return new MessageResponse("Availability updated successfully.");
    }

    public MessageResponse reserveAvailabilities(List<AvailabilityDTO> availabilityDTOs, String jwtToken) {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }
        if (!userDetails.getRoles().contains("ROLE_GUEST") && !userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission for this action.");
        }

        List<Availability> availabilitiesToReserve = new ArrayList<>();
        for (AvailabilityDTO dto : availabilityDTOs) {
            Availability availability = availabilityRepository.findById(dto.getId())
                    .orElseThrow(() -> new NoSuchElementException("Availability not found with id: " + dto.getId()));

            availability.setIsReserved(true);
            availability.setIsAvailable(false);
            availabilitiesToReserve.add(availability);
        }

        availabilityRepository.saveAll(availabilitiesToReserve);
        return new MessageResponse("Selected availabilities have been successfully reserved.");
    }

    public MessageResponse releaseAvailabilities(UUID accommodationId,
                                                 LocalDate dateFrom,
                                                 LocalDate dateTo,
                                                 String jwtToken) {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }
        if (!userDetails.getRoles().contains("ROLE_GUEST")) {
            throw new SecurityException("User do not have permission for this action.");
        }

        Set<LocalDate> dates = dateFrom.datesUntil(dateTo).collect(Collectors.toSet());
        List<AvailabilityDTO> availabilities = getAllAvailabilitiesByAccommodation(accommodationId);

        List<Optional<AvailabilityDTO>> selectedAvailabilities = new ArrayList<>();
        for (LocalDate date : dates) {
            Optional<AvailabilityDTO> availability = availabilities.stream()
                    .filter(a -> a.getDate().equals(date))
                    .findFirst();

            if (availability.isEmpty()) {
                throw new NoSuchElementException("No availability data found for the date: " + date);
            }

            selectedAvailabilities.add(availability);
        }

        List<AvailabilityDTO> convertedAvailabilities = selectedAvailabilities.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<Availability> availabilitiesToRelease = new ArrayList<>();
        for (AvailabilityDTO dto : convertedAvailabilities) {
            Availability availability = availabilityRepository.findById(dto.getId())
                    .orElseThrow(() -> new NoSuchElementException("Availability not found with id: " + dto.getId()));

            availability.setIsReserved(false);
            availability.setIsAvailable(true);
            availabilitiesToRelease.add(availability);
        }

        availabilityRepository.saveAll(availabilitiesToRelease);
        return new MessageResponse("Selected availabilities have been successfully released.");
    }
}
