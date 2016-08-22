package api;

import java.util.LinkedList;

public class ProviderResponse {

    private LinkedList<Flight> results = new LinkedList<>();

    public ProviderResponse() {

    }

    public ProviderResponse(LinkedList<Flight> results) {
        this.results = results;
    }

    public void setResults(LinkedList<Flight> results) {
        this.results = results;
    }

    public LinkedList<Flight> getResults() {
        return results;
    }
}
