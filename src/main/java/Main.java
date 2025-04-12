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
                updater.addPriceUpdate("AUD/USD", 0.70 + Math.random() * 0.01, 100000 + Math.random() * 1000);
            }
//            System.out.println("AUD/USD done");
        });

//        executor.submit(() -> {
//            for (int i = 0; i < 10000; i++) {
//                updater.addPriceUpdate("EUR/JPY", 141.0 + Math.random() * 0.1, 120000 + Math.random() * 1500);
//            }
//            System.out.println("EUR/JPY done");
//        });
//
//        executor.submit(() -> {
//            for (int i = 0; i < 10000; i++) {
//                updater.addPriceUpdate("GBP/USD", 1.25 + Math.random() * 0.02, 95000 + Math.random() * 800);
//            }
//            System.out.println("GBP/USD done");
//        });
//
//        executor.submit(() -> {
//            for (int i = 0; i < 10000; i++) {
//                updater.addPriceUpdate("USD/JPY", 110.0 + Math.random() * 0.2, 105000 + Math.random() * 1200);
//            }
//            System.out.println("USD/JPY done");
//        });

        executor.shutdown();

        // Wait for final flush
        Thread.sleep(5000);
        updater.shutdown();
    }
}