import entity.DataPoint;
import entity.VwapData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VwapCalculator {
    private static final Duration WINDOW_DURATION = Duration.ofHours(1);

    private final Map<String, VwapData> dataMap = new ConcurrentHashMap<>();
    private final Map<String, Object> locks = new ConcurrentHashMap<>();
    
    public void addPriceUpdate(String currencyPair, LocalDateTime timestamp, double price, double volume) {
        VwapData data;
        Object lock;

        /*
        * synchronized block (this): Ensure Multiple Thread Won't Init VwapData and Lock Object For Same Currency Pair
        * currency-pair -> {unique VwapData, unique lock}
        * */
        synchronized (this) {
            data = dataMap.computeIfAbsent(currencyPair, k -> new VwapData());
            lock = locks.computeIfAbsent(currencyPair, k -> new Object());
        }

        /*
        * synchronized block (lock): Ensure For Each CurrencyPair, only one thread is modifying VwapData
        * */
        synchronized (lock) {
            data.getDeque().addLast(new DataPoint(timestamp, price, volume));
            data.setPriceVolumeSum(data.getPriceVolumeSum() + price * volume);
            data.setVolumeSum(data.getVolumeSum() + volume);

            cleanOldData(data, timestamp);

            double vwap = calculateVwap(data);
            System.out.printf("Vwap for %s at %s = %.6f%n", currencyPair, timestamp, vwap);
        }
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

    
}