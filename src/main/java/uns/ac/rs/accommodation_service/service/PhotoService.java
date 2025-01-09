package uns.ac.rs.accommodation_service.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uns.ac.rs.accommodation_service.dto.PhotoDTO;
import uns.ac.rs.accommodation_service.mapper.PhotoMapper;
import uns.ac.rs.accommodation_service.model.Accommodation;
import uns.ac.rs.accommodation_service.model.Photo;
import uns.ac.rs.accommodation_service.repository.PhotoRepository;
import uns.ac.rs.accommodation_service.repository.AccommodationRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public PhotoService(PhotoRepository photoRepository,
                        AccommodationRepository accommodationRepository,
                        UserServiceClient userServiceClient) {
        this.photoRepository = photoRepository;
        this.accommodationRepository = accommodationRepository;
        this.userServiceClient = userServiceClient;
    }

    /*public MessageResponse uploadPhoto(UUID accommodationId, MultipartFile file, String jwtToken) throws Exception {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }

        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission to upload a photo.");
        }

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));
        if (!userDetails.getUsername().equals(accommodation.getHost())) {
            throw new SecurityException("User is not the owner of this accommodation.");
        }

        String url = saveFile(file);

        Photo newPhoto = new Photo(
                url,
                accommodation
        );

        photoRepository.save(newPhoto);
        return new MessageResponse("Photo uploaded successfully.");
    }*/

    public Photo uploadPhoto(Accommodation accommodation, MultipartFile file) throws Exception {
        String url = saveFile(file);

        Photo newPhoto = new Photo(url);
        newPhoto.setAccommodation(accommodation);

        photoRepository.save(newPhoto);
        return newPhoto;
    }

    /*public MessageResponse deletePhoto(UUID photoId, String jwtToken) {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }

        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission to delete a photo.");
        }

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NoSuchElementException("Photo not found with id: " + photoId));
        if (!userDetails.getUsername().equals(photo.getAccommodation().getHost())) {
            throw new SecurityException("User is not the owner of this accommodation.");
        }

        deleteFile(photo.getUrl());

        photoRepository.delete(photo);
        return new MessageResponse("Photo deleted successfully.");
    }*/

    /*public MessageResponse updatePhoto(UUID photoId, MultipartFile file, String jwtToken) throws Exception {
        UserDTO userDetails = userServiceClient.getUserDetails(jwtToken);
        if (userDetails == null) {
            throw new IllegalStateException("User details could not be retrieved.");
        }

        if (!userDetails.getRoles().contains("ROLE_HOST")) {
            throw new SecurityException("User do not have permission to update a photo.");
        }

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NoSuchElementException("Photo not found with id: " + photoId));
        if (!userDetails.getUsername().equals(photo.getAccommodation().getHost())) {
            throw new SecurityException("User is not the owner of this accommodation.");
        }

        deleteFile(photo.getUrl());

        String newUrl = saveFile(file);
        photo.setUrl(newUrl);

        photoRepository.save(photo);
        return new MessageResponse("Photo updated successfully.");
    }*/

    public String saveFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

//        String uploadDir = "/uploads/";
//        Path path = Paths.get(uploadDir + filename);
//        Files.createDirectories(path.getParent());
//        Files.write(path, file.getBytes());

        // Define the upload directory
        String uploadDir = "uploads"; // Relative directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(uploadPath); // Ensure the directory exists

        // Save the file
        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, file.getBytes());

        return filename;
    }

    private void deleteFile(String url) {
        try {
            String baseDir = System.getProperty("user.dir");
            String fullPath = baseDir + url;
            Path path = Paths.get(fullPath);

            if (Files.exists(path)) {
                Files.delete(path);
            } else {
                throw new NoSuchElementException("File not found.");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error while deleting file: " + url, e);
        }
    }

    /*public PhotoDTO getPhotoById(UUID photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NoSuchElementException("Photo not found with id: " + photoId));

        return PhotoMapper.toPhotoDTO(photo);
    }*/

    public List<PhotoDTO> getAllPhotosByAccommodation(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new NoSuchElementException("Accommodation not found with id: " + accommodationId));

        return photoRepository.findByAccommodation(accommodation)
                .stream()
                .map(PhotoMapper::toPhotoDTO)
                .collect(Collectors.toList());
    }
}
