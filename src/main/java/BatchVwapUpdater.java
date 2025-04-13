import entity.DataPoint;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class BatchVwapUpdater {

    private final List<DataPoint> buffer = new ArrayList<>();
    private final ScheduledThreadPoolExecutor workerPool = new ScheduledThreadPoolExecutor(4);
    private final VwapCalculator calculator = new VwapCalculator();
    private final Integer FLUSH_THRESHOLD = 1000;
    private final Integer FLUSH_INTERVAL_MS = 1000;

    private ScheduledFuture<?> scheduledFlush;

    public BatchVwapUpdater() {
//        workerPool.scheduleAtFixedRate(this::flush, 0, 1000, TimeUnit.MILLISECONDS);
        scheduleFlush();
    }

    private void scheduleFlush() {
        scheduledFlush = workerPool.scheduleWithFixedDelay(this::flush, FLUSH_INTERVAL_MS, FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void rescheduleFlush() {
        if (scheduledFlush != null && !scheduledFlush.isCancelled()) {
            scheduledFlush.cancel(false);
        }
        scheduleFlush();
    }

    public void addPriceUpdate(String currencyPair, double price, double volume) {
        boolean shouldFlush = false;

        synchronized (buffer) {
            buffer.add(new DataPoint(LocalDateTime.now(), price, volume, currencyPair));

            if (buffer.size() >= FLUSH_THRESHOLD) {
                shouldFlush = true;
            }
        }

        if (shouldFlush) {
            flush();
            rescheduleFlush();
        }
    }

    private void flush() {
        List<DataPoint> batch;
        synchronized (buffer) {
            if (buffer.isEmpty()) {
                System.out.println("buffer is empty");
                return;
            }
            batch = new ArrayList<>(buffer);
            buffer.clear();
        }

        workerPool.submit(() -> {
            Map<String, List<DataPoint>> grouped = new HashMap<>();
            System.out.printf("Thread id %s is processing %s records\n",Thread.currentThread(), batch.size());
            for (DataPoint dp : batch) {
                grouped.computeIfAbsent(dp.getCurrencyPair(), k -> new ArrayList<>()).add(dp);
            }

            for (Map.Entry<String, List<DataPoint>> entry : grouped.entrySet()) {
                calculator.addAllUpdates(entry.getKey(), entry.getValue());
            }

        });
    }

    public void shutdown() {
        workerPool.shutdown();
    }
}