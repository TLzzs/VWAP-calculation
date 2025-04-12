import entity.DataPoint;
import entity.VwapData;
import exceptions.InvalidVwapDataException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VwapCalculator {
    private static final Duration WINDOW_DURATION = Duration.ofHours(1);

    private final Map<String, VwapData> dataMap = new ConcurrentHashMap<>();
    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    public void addAllUpdates(String currencyPair, List<DataPoint> updates) {
        if (updates.isEmpty()) return;

        try {
            Object lock;
            VwapData data;
            synchronized (this) {
                data = dataMap.computeIfAbsent(currencyPair, k -> new VwapData());
                lock = locks.computeIfAbsent(currencyPair, k -> new Object());
            }

            synchronized (lock) {
                LocalDateTime latest = null;
                for (DataPoint dp : updates) {
                    validateInputDataPoint(currencyPair, dp);
                    data.getDeque().addLast(dp);
                    data.setPriceVolumeSum(data.getPriceVolumeSum() + dp.getPrice() * dp.getVolume());
                    data.setVolumeSum(data.getVolumeSum() + dp.getVolume());
                    if (latest == null || dp.getTimestamp().isAfter(latest)) {
                        latest = dp.getTimestamp();
                    }
                }
                cleanOldData(data, latest);

                double vwap = getCurrentVwap(currencyPair);
                System.out.printf("VWAP(%s) After Current Batch at %s : %.6f%n",currencyPair, LocalDateTime.now(), vwap);
            }
        } catch (Exception e) {
            throw new InvalidVwapDataException("VWAP update failed: " + e.getMessage());
        }

    }

    private void validateInputDataPoint(String currencyPair, DataPoint dp) {
        validateInput(currencyPair, dp.getTimestamp(), dp.getPrice(), dp.getVolume());
    }

    private void cleanOldData(VwapData data, LocalDateTime now) {
        while (true) {
            DataPoint head = data.getDeque().peekFirst();
            if (head == null) break;

            if (Duration.between(head.getTimestamp(), now).compareTo(WINDOW_DURATION) > 0) {
                DataPoint old = data.getDeque().pollFirst();
                data.setPriceVolumeSum(data.getPriceVolumeSum() - old.getPrice() * old.getVolume());
                data.setVolumeSum(data.getVolumeSum() - old.getVolume());
            } else {
                break;
            }
        }
    }

    private double calculateVwap(VwapData data) {
        return data.getVolumeSum() == 0 ? 0.0 : data.getPriceVolumeSum() / data.getVolumeSum();
    }

    public double getCurrentVwap(String currencyPair) {
        VwapData data = dataMap.get(currencyPair);
        Object lock = locks.get(currencyPair);
        if (data == null || lock == null) return 0.0;

        synchronized (lock) {
            return calculateVwap(data);
        }
    }

    private void validateInput(String currencyPair, LocalDateTime timestamp, double price, double volume) {
        if (currencyPair == null || timestamp == null) {
            throw new InvalidVwapDataException("currencyPair and timestamp must not be null");
        }
        if (Double.isNaN(price) || Double.isNaN(volume) || volume < 0) {
            throw new InvalidVwapDataException("Invalid price or volume: price=" + price + ", volume=" + volume);
        }
    }

    
}