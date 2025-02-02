package uns.ac.rs.accommodation_service.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
public class RatingServiceClient {
    private final WebClient webClient;

    @Autowired
    public RatingServiceClient(WebClient.Builder webClientBuilder,
                                    @Value("${rating.service.url}") String ratingServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(ratingServiceUrl).build();
    }

    public Double getAccommodationAverageRating(UUID idAccommodation) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("ratings/accommodation/average/{idAccommodation}")
                            .build(idAccommodation))
                    .retrieve()
                    .bodyToMono(Double.class)  // Expect a Double, not Boolean
                    .block();  // Blocking call to get the result
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to RatingService: ", e);
        }
    }

    public Double getHostAverageRating(String host) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("ratings/host/average/{host}")
                            .build(host))
                    .retrieve()
                    .bodyToMono(Double.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to RatingService: ", e);
        }
    }
}
