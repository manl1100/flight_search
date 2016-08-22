package api;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Flight {

    private double agony;
    private double price;
    private String provider;
    private String arrival;
    private String flightNumber;
    private String departure;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @JsonProperty("flight_num")
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    @JsonProperty("depart_time")
    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    @JsonProperty("arrive_time")
    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public double getAgony() {
        return agony;
    }

    public void setAgony(double agony) {
        this.agony = agony;
    }

    @Override
    public String toString() {
        return String.format("%s %f %s %f %s %s",
                provider, price, flightNumber, agony, departure, arrival);
    }
}
