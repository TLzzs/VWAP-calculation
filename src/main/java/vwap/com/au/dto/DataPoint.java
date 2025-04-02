package vwap.com.au.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DataPoint {
    private String currencyPair;
    private LocalDateTime timestamp;
    private double price;
    private double volume;
}