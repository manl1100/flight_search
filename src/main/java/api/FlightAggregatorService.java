package api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@EnableAsync
public class FlightAggregatorService {

    private static Logger logger = Logger.getLogger(FlightAggregatorService.class);

    @Autowired
    private FlightProviderService flightProviderService;

    public List<LinkedList<Flight>> getAggregatedFlights(List<String> providers) {
        List<Future<ProviderResponse>> requests = providers.stream()
                .map(x -> flightProviderService.findFlights(x))
                .collect(Collectors.toList());

        while (isPending(requests)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.info(e.getMessage());
            }
        }

        return getFlightsFromResponse(requests);
    }

    private boolean isPending(List<Future<ProviderResponse>> futures) {
        for (Future future : futures) {
            if (!future.isDone()) return false;
        }
        return true;
    }

    private List<LinkedList<Flight>> getFlightsFromResponse(List<Future<ProviderResponse>> response) {
        List<LinkedList<Flight>> flightsFromProviders = new ArrayList<>();
        for (Future<ProviderResponse> future : response) {
            try {
                flightsFromProviders.add(future.get().getResults());
            } catch (Exception e) {
                flightsFromProviders.add(new LinkedList<>());
            }
        }
        return flightsFromProviders;
    }
}
