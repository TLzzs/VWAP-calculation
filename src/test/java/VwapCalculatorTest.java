//
//import exceptions.InvalidVwapDataException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class VwapCalculatorTest {
//
//    private VwapCalculator calculator;
//
//    @BeforeEach
//    void setUp() {
//        calculator = new VwapCalculator();
//    }
//
//    @Test
//    void testVwapCalculationSingleUpdate() {
//        calculator.addPriceUpdate("AUD/USD", LocalDateTime.now(), 0.70, 100000);
//        double vwap = calculator.getCurrentVwap("AUD/USD");
//        assertEquals(0.70, vwap, 0.000001);
//    }
//
//    @Test
//    void testVwapCalculationMultipleUpdates() {
//        calculator.addPriceUpdate("AUD/USD", LocalDateTime.now(), 0.70, 100000);
//        calculator.addPriceUpdate("AUD/USD", LocalDateTime.now(), 0.71, 200000);
//        double vwap = calculator.getCurrentVwap("AUD/USD");
//        double expected = ((0.70 * 100000) + (0.71 * 200000)) / (100000 + 200000);
//        assertEquals(expected, vwap, 0.000001);
//    }
//
//    @Test
//    void testOldDataIsRemoved() {
//        LocalDateTime now = LocalDateTime.now();
//        calculator.addPriceUpdate("AUD/USD", now.minusHours(2), 0.68, 100000);
//        calculator.addPriceUpdate("AUD/USD", now, 0.72, 200000);
//        double vwap = calculator.getCurrentVwap("AUD/USD");
//        assertEquals(0.72, vwap, 0.000001);
//    }
//
//    @Test
//    void testVwapWithZeroVolume() {
//        assertEquals(0.0, calculator.getCurrentVwap("NON/EXIST"));
//    }
//
//    @Test
//    void testHighConcurrencyPerformance() throws InterruptedException {
//        int threads = 10;
//        int updatesPerThread = 10000;
//        ExecutorService executor = Executors.newFixedThreadPool(threads);
//        CountDownLatch latch = new CountDownLatch(threads);
//
//        Runnable task = () -> {
//            for (int i = 0; i < updatesPerThread; i++) {
//                calculator.addPriceUpdate("AUD/USD", LocalDateTime.now(), 0.6 + Math.random() * 0.1, 100000 + Math.random() * 10000);
//            }
//            latch.countDown();
//        };
//
//        long start = System.nanoTime();
//        for (int i = 0; i < threads; i++) {
//            executor.submit(task);
//        }
//        latch.await();
//        long duration = System.nanoTime() - start;
//        double vwap = calculator.getCurrentVwap("AUD/USD");
//
//        System.out.printf("High concurrency test completed in %.2f ms. Final VWAP: %.6f%n", duration / 1000000.0, vwap);
//
//        assertTrue(vwap > 0.0);
//        executor.shutdown();
//    }
//
//    @Test
//    void testInvalidCurrencyPairThrowsException() {
//        InvalidVwapDataException ex = assertThrows(InvalidVwapDataException.class, () -> {
//            calculator.addPriceUpdate(null, LocalDateTime.now(), 1.10, 100000);
//        });
//        assertTrue(ex.getMessage().contains("currencyPair"));
//    }
//
//    @Test
//    void testInvalidTimestampThrowsException() {
//        InvalidVwapDataException ex = assertThrows(InvalidVwapDataException.class, () -> {
//            calculator.addPriceUpdate("AUD/USD", null, 1.10, 100000);
//        });
//        assertTrue(ex.getMessage().contains("timestamp"));
//    }
//
//    @Test
//    void testInvalidVolumeThrowsException() {
//        InvalidVwapDataException ex = assertThrows(InvalidVwapDataException.class, () -> {
//            calculator.addPriceUpdate("AUD/USD", LocalDateTime.now(), 1.10, -100);
//        });
//        assertTrue(ex.getMessage().contains("volume"));
//    }
//
//    @Test
//    void testNaNPriceThrowsException() {
//        InvalidVwapDataException ex = assertThrows(InvalidVwapDataException.class, () -> {
//            calculator.addPriceUpdate("AUD/USD", LocalDateTime.now(), Double.NaN, 100000);
//        });
//        assertTrue(ex.getMessage().contains("price"));
//    }
//}