package vwap.com.au.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Deque;
import java.util.LinkedList;

@Getter
@Setter
@NoArgsConstructor
public class VwapData {
    private Deque<DataPoint> deque = new LinkedList<>();
    private double priceVolumeSum = 0.0;
    private double volumeSum = 0.0;
}
