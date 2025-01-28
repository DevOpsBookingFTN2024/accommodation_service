package uns.ac.rs.accommodation_service.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.UUID;

@Service
public class ReservationServiceClient {
    private final WebClient webClient;

    @Autowired
    public ReservationServiceClient(WebClient.Builder webClientBuilder,
                                    @Value("${reservation.service.url}") String reservationServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(reservationServiceUrl).build();
    }

    public boolean isAccommodationHasAcceptedReservation(UUID idAccommodation) {
        try {
            return Boolean.TRUE.equals(webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reservations/host/accommodation/has-accepted-reservation/{idAccommodation}")
                            .build(idAccommodation))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to ReservationService: ", e);
        }
    }
}
