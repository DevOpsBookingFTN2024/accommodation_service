package uns.ac.rs.accommodation_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uns.ac.rs.accommodation_service.dto.UserDTO;
import java.util.UUID;

@Service
public class UserServiceClient {
    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    @Value("${user.service.url}")
    private String userServiceUrl;

    @Autowired
    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public UserDTO getUserDetails(UUID userId) {

        return webClient.get()
                .uri(userServiceUrl+"/users/{userId}", userId)  // URI je sada relativan, jer je osnovni URL postavljen na http://user-service:8080
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnTerminate(() -> log.info("Fetched user details"))
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("Error fetching user details for ID: {} - Status: {}", userId, e.getStatusCode());
                    return new RuntimeException("Error fetching user details: " + e.getMessage(), e);
                })
                .block();  // Možete koristiti block() da biste sačekali rezultat ako koristite sinhroni pristup
    }
}

