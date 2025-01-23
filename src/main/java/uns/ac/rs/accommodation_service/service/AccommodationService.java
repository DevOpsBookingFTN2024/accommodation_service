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
import java.time.ZonedDateTime;
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
    private final PhotoService photoService;

    @Autowired
    public AccommodationService(AccommodationRepository accommodationRepository,
                                FacilityRepository facilityRepository,
                                UserServiceClient userServiceClient,
                                AvailabilityService availabilityService,
                                PhotoService photoService) {
        this.accommodationRepository = accommodationRepository;
        this.facilityRepository = facilityRepository;
        this.userServiceClient = userServiceClient;
        this.availabilityService = availabilityService;
        this.photoService = photoService;
    }

    @Transactional
    public MessageResponse createAccommodation(CreateAccommodationRequest createAccommodationRequest, String jwtToken) {
        if (createAccommodationRequest.getMaximumGuests() < createAccommodationRequest.getMinimumGuests()) {
            throw new IllegalArgumentException("Maximum guests must be greater than or equal to minimum guests.");
        }

        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }
        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission for this action.");
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

        if(createAccommodationRequest.getFiles()!=null && !createAccommodationRequest.getFiles().isEmpty()) {
            photoInsertion(newAccommodation, createAccommodationRequest.getFiles());
        }

        accommodationRepository.save(newAccommodation);

        return new MessageResponse("Accommodation created successfully.");
    }

    public List<AccommodationDTO> getAllAccommodations() {
        List<AccommodationDTO> accommodationDTOS = accommodationRepository.findAll()
                .stream()
                .map(AccommodationMapper::toAccommodationDTO)
                .toList();
        return accommodationDTOS
                .stream()
                .peek(accommodationDTO -> {
                    Set<PhotoDTO> photos = new HashSet<>(photoService.getAllPhotosByAccommodation(accommodationDTO.getId()));
                    accommodationDTO.setPhotos(photos);
                })
                .collect(Collectors.toList());
    }

    public AccommodationDTO getAccommodationById(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));
        AccommodationDTO accommodationDTO = AccommodationMapper.toAccommodationDTO(accommodation);
        accommodationDTO.setPhotos(new HashSet<>(photoService.getAllPhotosByAccommodation(accommodationDTO.getId())));
        return accommodationDTO;
    }

    public List<SearchAccommodationDTO> getAllAccommodationsByHost(String host) {
        List<Accommodation> accommodations = accommodationRepository.findByHost(host);
        List<SearchAccommodationDTO> searchAccommodationDTOS = accommodations.stream()
                .map(accommodation -> {

                    List<AvailabilityDTO> availabilitiesAll = availabilityService.getAllAvailabilitiesByAccommodation(accommodation.getId());

                    List<AvailabilityDTO> availabilities = availabilitiesAll.stream()
                            .filter(av -> av.getDate() != null &&
                                    av.getDate().isEqual(LocalDate.from(ZonedDateTime.now())))
                            .toList();

                    return getSearchAccommodationObject(accommodation, availabilities, 1, 1);
                })
                .toList();
        return searchAccommodationDTOS
                .stream()
                .peek(searchAccommodationDTO -> {
                    Set<PhotoDTO> photos = new HashSet<>(photoService.getAllPhotosByAccommodation(searchAccommodationDTO.getAccommodationDTO().getId()));
                    searchAccommodationDTO.getAccommodationDTO().setPhotos(photos);
                })
                .collect(Collectors.toList());
    }

    public List<SelectAccommodationDTO> getAllSelectAccommodationsByHost(String host) {
        return  accommodationRepository.findByHost(host)
                .stream()
                .map(accommodation -> {
                    return new SelectAccommodationDTO(accommodation.getId(), accommodation.getName());
                })
                .collect(Collectors.toList());

    }

    @Transactional
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
            throw new SecurityException("User do not have permission for this action.");
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

        if(updateAccommodationRequest.getFiles()!=null && !updateAccommodationRequest.getFiles().isEmpty()) {
            for(PhotoDTO photo : photoService.getAllPhotosByAccommodation(accommodationId)) {
                photoService.deletePhoto(photo.getId(), jwtToken);
            }
            photoInsertion(accommodation, updateAccommodationRequest.getFiles());
        }

        accommodationRepository.save(accommodation);
        return new MessageResponse("Accommodation updated successfully.");
    }

    private void photoInsertion(Accommodation accommodation, Set<MultipartFile> files)  {
        try {
            for (MultipartFile photoFile : files) {
                if(!photoFile.isEmpty()) {
                    photoService.uploadPhoto(accommodation, photoFile);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<SearchAccommodationDTO> searchAccommodations(String city,
                                                             String country,
                                                             Integer guestCount,
                                                             LocalDate startDate,
                                                             LocalDate endDate) {
        Integer daysInRange = Math.toIntExact(ChronoUnit.DAYS.between(startDate, endDate));
        LocalDate finalEndDate = endDate.minusDays(1);

        List<Accommodation> accommodations = accommodationRepository
                .findMatchingAccommodations(city, country, guestCount, startDate, finalEndDate, daysInRange);

        return accommodations.stream()
                .map(accommodation -> {

                    List<AvailabilityDTO> availabilitiesAll = availabilityService.getAllAvailabilitiesByAccommodation(accommodation.getId());

                    List<AvailabilityDTO> availabilities = availabilitiesAll.stream()
                            .filter(av -> av.getDate() != null &&
                                    !av.getDate().isBefore(startDate) &&
                                    !av.getDate().isAfter(finalEndDate))
                            .toList();

                    return getSearchAccommodationObject(accommodation, availabilities, daysInRange, guestCount);
                })
                .toList();
    }

    private SearchAccommodationDTO getSearchAccommodationObject(Accommodation accommodation, List<AvailabilityDTO> availabilities, Integer daysInRange, Integer guestCount ){
        Double pricePerGuest = availabilities.stream()
                .map(AvailabilityDTO::getPricePerGuest)
                .findFirst()
                .orElse(null);

        Double pricePerUnit = availabilities.stream()
                .map(AvailabilityDTO::getPricePerUnit)
                .findFirst()
                .orElse(null);

        Double totalPrice = 0.0;

        if (accommodation.getPricingStrategy() == EPricingStrategy.PER_UNIT){
            totalPrice = availabilities.stream()
                    .mapToDouble(AvailabilityDTO::getPricePerUnit)
                    .sum();
            pricePerUnit = totalPrice/daysInRange;
        } else {
            if (pricePerGuest != null)
                totalPrice = pricePerGuest * guestCount * daysInRange;
        }

        AccommodationDTO accommodationDTO = AccommodationMapper.toAccommodationDTO(accommodation);
        accommodationDTO.setPhotos(new HashSet<>(photoService.getAllPhotosByAccommodation(accommodationDTO.getId())));
        return SearchAccommodationDTO.builder()
                .accommodationDTO(accommodationDTO)
                .pricePerGuest(pricePerGuest)
                .pricePerUnit(pricePerUnit)
                .priceAll(totalPrice)
                .build();
    }

    //potrebno je doraditi kad se zavrsi ReservationService
    public MessageResponse deleteAccommodation(UUID accommodationId, String jwtToken) {
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
            throw new SecurityException("User do not have permission for this action.");
        }

        List<Accommodation> accommodations = accommodationRepository.findByHost(userDetails.getUsername());
        if (accommodations.isEmpty()) {
            throw new NoSuchElementException("No accommodations found for the host.");
        }

        accommodationRepository.deleteAll(accommodations);
        return new MessageResponse("All accommodations deleted successfully.");
    }
}
