package io.github.stealingdapenta.foodclicker.utils;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * A bounded, ordered file queue. Work and completion handlers must not access Bukkit objects.
 */
public final class PlayerPersistence {

    private static final Object OWNERSHIP_LOCK = new Object();
    private static ThreadPoolExecutor owner;

    private final ThreadPoolExecutor executor;
    private final Set<CompletableFuture<?>> pending = ConcurrentHashMap.newKeySet();
    private volatile long shutdownDeadline;

    public PlayerPersistence() {
        synchronized (OWNERSHIP_LOCK) {
            // Interruption cannot cancel a filesystem commit. Do not admit another writer until actual termination.
            if (owner != null && !owner.isTerminated()) {
                throw new IllegalStateException("Previous FoodClicker persistence worker is still running; re-enable refused");
            }
            executor = new ThreadPoolExecutor(
                    1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(256),
                    action -> {
                        Thread worker = new Thread(action, "FoodClicker-persistence");
                        worker.setDaemon(true);
                        return worker;
                    }, new ThreadPoolExecutor.AbortPolicy());
            owner = executor;
        }
    }

    public <T> CompletableFuture<T> submit(Supplier<T> operation) {
        CompletableFuture<T> result = new CompletableFuture<>();
        pending.add(result);
        result.whenComplete((ignored, failure) -> pending.remove(result));
        Runnable work = () -> {
            try {
                result.complete(operation.get());
            } catch (RuntimeException failure) {
                result.completeExceptionally(failure);
            }
        };
        try {
            executor.execute(work);
        } catch (RejectedExecutionException failure) {
            try {
                long remaining = shutdownDeadline - System.nanoTime();
                if (shutdownDeadline == 0 || remaining <= 0 || executor.isShutdown()
                        || !executor.getQueue().offer(work, remaining, TimeUnit.NANOSECONDS)) {
                    result.completeExceptionally(failure);
                } else if (executor.isShutdown() && executor.remove(work)) {
                    result.completeExceptionally(failure);
                }
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                result.completeExceptionally(interrupted);
            }
        }
        return result;
    }

    /** Only disable may wait for capacity; queued file work never depends on a server-thread callback. */
    public void beginShutdown(Duration timeout) {
        shutdownDeadline = System.nanoTime() + timeout.toNanos();
    }

    public boolean drain(Duration timeout) {
        executor.shutdown();
        try {
            if (executor.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                return true;
            }
        } catch (InterruptedException failure) {
            abortPending();
            Thread.currentThread().interrupt();
            return false;
        }
        abortPending();
        return false;
    }

    private void abortPending() {
        executor.shutdownNow();
        RejectedExecutionException failure = new RejectedExecutionException(
                "FoodClicker persistence shutdown did not finish; write completion is not guaranteed");
        pending.forEach(result -> result.completeExceptionally(failure));
    }
}
