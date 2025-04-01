package entity;

import java.util.Deque;
import java.util.LinkedList;

public class VwapData {
    private Deque<DataPoint> deque = new LinkedList<>();
    private double priceVolumeSum = 0.0;
    private double volumeSum = 0.0;

    public Deque<DataPoint> getDeque() {
        return deque;
    }

    public void setDeque(Deque<DataPoint> deque) {
        this.deque = deque;
    }

    public double getPriceVolumeSum() {
        return priceVolumeSum;
    }

    public void setPriceVolumeSum(double priceVolumeSum) {
        this.priceVolumeSum = priceVolumeSum;
    }

    public double getVolumeSum() {
        return volumeSum;
    }

    public void setVolumeSum(double volumeSum) {
        this.volumeSum = volumeSum;
    }
}
