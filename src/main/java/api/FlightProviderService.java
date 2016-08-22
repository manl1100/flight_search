package api;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

@Service
public class FlightProviderService {

    private RestTemplate restTemplate = new RestTemplate();

    @Async
    public Future<ProviderResponse> findFlights(String provider) {
        String url = String.format("http://localhost:9000/scrapers/%s", provider);
        ProviderResponse results = restTemplate.getForObject(url, ProviderResponse.class);
        return new AsyncResult<>(results);
    }
}
