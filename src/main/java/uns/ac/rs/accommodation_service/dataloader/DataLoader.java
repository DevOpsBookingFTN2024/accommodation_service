package uns.ac.rs.accommodation_service.dataloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uns.ac.rs.accommodation_service.model.Facility;
import uns.ac.rs.accommodation_service.repository.FacilityRepository;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private FacilityRepository facilityRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!facilityRepository.existsByName("Wi-Fi")) {
            facilityRepository.save(new Facility(null, "Wi-Fi"));
        }
        if (!facilityRepository.existsByName("Parking lot")) {
            facilityRepository.save(new Facility(null, "Parking lot"));
        }
        if (!facilityRepository.existsByName("TV")) {
            facilityRepository.save(new Facility(null, "TV"));
        }
        if (!facilityRepository.existsByName("Kitchen")) {
            facilityRepository.save(new Facility(null, "Kitchen"));
        }
        if (!facilityRepository.existsByName("Air condition")) {
            facilityRepository.save(new Facility(null, "Air condition"));
        }
        if (!facilityRepository.existsByName("Swimming pool")) {
            facilityRepository.save(new Facility(null, "Swimming pool"));
        }
        if (!facilityRepository.existsByName("Fitness center")) {
            facilityRepository.save(new Facility(null, "Fitness center"));
        }
        if (!facilityRepository.existsByName("Terrace")) {
            facilityRepository.save(new Facility(null, "Terrace"));
        }
        if (!facilityRepository.existsByName("Barbecue")) {
            facilityRepository.save(new Facility(null, "Barbecue"));
        }
        if (!facilityRepository.existsByName("Additional toilet")) {
            facilityRepository.save(new Facility(null, "Additional toilet"));
        }
    }
}
