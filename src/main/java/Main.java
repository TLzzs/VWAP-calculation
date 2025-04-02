import exceptions.InvalidVwapDataException;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        VwapCalculator calculator = new VwapCalculator();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10000; i++) {
            executor.execute(() -> {
                try {
                    calculator.addPriceUpdate("AUD/USD", LocalDateTime.now(), 0.69 + Math.random() * 0.01, 100000 + Math.random() * 10000);
                } catch (InvalidVwapDataException e) {
                    System.err.println("AUD/USD update skipped: " + e.getMessage());
                }
            });
            executor.execute(() -> {
                try {
                    calculator.addPriceUpdate("CNY/AUD", LocalDateTime.now(), 0.22 + Math.random() * 1.0, 200000 + Math.random() * 50000);
                } catch (InvalidVwapDataException e) {
                    System.err.println("CNY/AUD update skipped: " + e.getMessage());
                }
            });
            executor.execute(() -> {
                try {
                    calculator.addPriceUpdate("NZD/GBP", LocalDateTime.now(), 0.47 + Math.random() * 0.01, 150000 + Math.random() * 20000);
                } catch (InvalidVwapDataException e) {
                    System.err.println("NZD/GBP update skipped: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
    }
}
