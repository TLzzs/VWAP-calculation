import exceptions.InvalidVwapDataException;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        BatchVwapUpdater updater = new BatchVwapUpdater();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Hardcoded: Each thread sends updates for a specific pair
        executor.submit(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    updater.addPriceUpdate("AUD/USD", 0.69 + Math.random() * 0.01, 100000 + Math.random() * 1000);
                } catch (InvalidVwapDataException e) {
                    System.err.println("AUD/USD update skipped: " + e.getMessage());
                }

                try {
                    updater.addPriceUpdate("CNY/AUD", 0.22 + Math.random() * 1.0, 200000 + Math.random() * 50000);
                } catch (InvalidVwapDataException e) {
                    System.err.println("CNY/AUD update skipped: " + e.getMessage());
                }


                try {
                    updater.addPriceUpdate("NZD/GBP",  0.47 + Math.random() * 0.01, 150000 + Math.random() * 20000);
                } catch (InvalidVwapDataException e) {
                    System.err.println("NZD/GBP update skipped: " + e.getMessage());
                }
            }
//            System.out.println("AUD/USD done");
        });

        executor.shutdown();

        // Wait for final flush
        Thread.sleep(5000);
        updater.shutdown();
    }
}