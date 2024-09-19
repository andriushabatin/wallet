package com.batin.wallet.thread;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.*;

@Component
public class RequestQueueProcessor {

    private final Queue<Runnable> requestQueue = new ConcurrentLinkedQueue<>();
    private final Semaphore semaphore = new Semaphore(1000);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RequestQueueProcessor() {
        scheduler.scheduleAtFixedRate(this::processQueue, 0, 1, TimeUnit.MILLISECONDS);
    }

    public void addRequest(Runnable request) {
        requestQueue.add(request);
    }

    private void processQueue() {
        int maxRequestsPerSecond = 1000;
        for (int i = 0; i < maxRequestsPerSecond && !requestQueue.isEmpty(); i++) {
            semaphore.acquireUninterruptibly();
            Runnable request = requestQueue.poll();
            if (request != null) {
                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        request.run();
                    } finally {
                        semaphore.release();
                    }
                });
            }
        }
    }
}
