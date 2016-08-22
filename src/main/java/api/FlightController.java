package api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


@SpringBootApplication
@RestController
@EnableAsync
public class FlightController {

    private static Logger logger = Logger.getLogger(FlightController.class);

    @Autowired
    private FlightProviderService flightProviderService;

    @RequestMapping("/flights/search")
    public ProviderResponse search() {
        List<String> providers = Arrays.asList("expedia", "orbitz", "priceline", "travelocity", "united");
        List<LinkedList<Flight>> flights = getFlightsFromProviders(providers);
        return new ProviderResponse(sortByAgony(flights));
    }

    private List<LinkedList<Flight>> getFlightsFromProviders(List<String> providers) {
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

    private LinkedList<Flight> sortByAgony(List<LinkedList<Flight>> aggregatedResponses) {
        LinkedList<Flight> sortedOutput = new LinkedList<>();

        Comparator<LinkedList<Flight>> compareByAgony = (x, y)-> {
            if (x.isEmpty() && y.isEmpty()) return 0;
            if (x.isEmpty()) return -1;
            if (y.isEmpty()) return 1;
            return Double.compare(x.getFirst().getAgony(), y.getFirst().getAgony());
        };

        Queue<LinkedList<Flight>> queue = new PriorityQueue<>(aggregatedResponses.size(), compareByAgony);
        queue.addAll(aggregatedResponses);

        while (!queue.isEmpty()) {
            LinkedList<Flight> peek = queue.poll();
            if (!peek.isEmpty()) {
                sortedOutput.add(peek.removeFirst());
                queue.add(peek);
            }
        }

        return sortedOutput;
    }

    public static void main(String[] args) {
        SpringApplication.run(FlightController.class, args);
    }

}