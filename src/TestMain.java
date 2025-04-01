import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMain {

    public static void main(String[] args) throws InterruptedException {
        VwapCalculator calculator = new VwapCalculator();
        ExecutorService executor = Executors.newFixedThreadPool(16);

        String currencyPair = "AUD/USD";
        LocalDateTime now = LocalDateTime.now();

        int updates = 10000;
        double price = 0.68;
        double volume = 8000;

        CountDownLatch latch = new CountDownLatch(updates);

        for (int i = 0; i < updates; i++) {
            executor.submit(() -> {
                calculator.addPriceUpdate(currencyPair, now, price, volume);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        double vwap = calculator.getCurrentVwap(currencyPair);

        System.out.printf("VWAP after %,d updates: %.6f\n", updates, vwap);

        if (Math.abs(vwap - price) < 0.000001) {
            System.out.println("√√√√√ Test Passed: VWAP is accurate.");
        } else {
            System.out.println("xxxxx Test Failed: VWAP mismatch.");
        }
    }
}
