package entity;

import java.time.LocalDateTime;

public class DataPoint {
    private LocalDateTime timestamp;
    private double price;
    private double volume;
    private final String currencyPair;


    public DataPoint(LocalDateTime timestamp, double price, double volume, String currenyPair) {
        this.timestamp = timestamp;
        this.price = price;
        this.volume = volume;
        this.currencyPair = currenyPair;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}