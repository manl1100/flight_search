package api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
public class FlightController {

    private static Logger logger = Logger.getLogger(FlightController.class);

    @Autowired
    private FlightAggregatorService flightAggregatorService;

    @RequestMapping("/flights/search")
    public ProviderResponse search() {
        List<String> providers = Arrays.asList("expedia", "orbitz", "priceline", "travelocity", "united");
        List<LinkedList<Flight>> flights = flightAggregatorService.getAggregatedFlights(providers);
        return new ProviderResponse(sortByAgony(flights));
    }

    private LinkedList<Flight> sortByAgony(List<LinkedList<Flight>> aggregatedResponses) {
        LinkedList<Flight> sortedOutput = new LinkedList<>();

        Comparator<LinkedList<Flight>> compareByAgony = (x, y) -> {
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

}