package io.github.stealingdapenta.foodclicker.utils;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * A bounded, ordered file queue. Work and completion handlers must not access Bukkit objects.
 */
public final class PlayerPersistence {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(256),
            action -> new Thread(action, "FoodClicker-persistence"), new ThreadPoolExecutor.AbortPolicy());
    private volatile long shutdownDeadline;

    public <T> CompletableFuture<T> submit(Supplier<T> operation) {
        CompletableFuture<T> result = new CompletableFuture<>();
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
            return executor.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
