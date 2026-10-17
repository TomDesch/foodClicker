package io.github.stealingdapenta.foodclicker.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class PlayerPersistenceTest {

    @Test
    void writesAndReadsAreOrderedOffCallerThreadAndDrainNeedsNoMainCallback() {
        PlayerPersistence persistence = new PlayerPersistence();
        Thread caller = Thread.currentThread();
        List<Integer> writes = new ArrayList<>();
        try {
            for (int index = 0; index < 20; index++) {
                int value = index;
                persistence.submit(() -> {
                    assertNotSame(caller, Thread.currentThread());
                    writes.add(value);
                    return null;
                });
            }
            CompletableFuture<List<Integer>> read = persistence.submit(() -> List.copyOf(writes));
            assertTrue(persistence.drain(Duration.ofSeconds(5)));
            assertEquals(java.util.stream.IntStream.range(0, 20).boxed().toList(), read.join());
            assertTrue(persistence.submit(() -> null).isCompletedExceptionally());
        } finally {
            persistence.drain(Duration.ofSeconds(5));
        }
    }

    @Test
    void operationFailureDoesNotStopLaterWritesAndQueueRejectsInsteadOfBlocking() throws Exception {
        PlayerPersistence persistence = new PlayerPersistence();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try {
            CompletableFuture<Void> failed = persistence.submit(() -> {
                throw new IllegalStateException("disk failed");
            });
            assertThrows(java.util.concurrent.CompletionException.class, failed::join);
            persistence.submit(() -> {
                started.countDown();
                try {
                    assertTrue(release.await(5, TimeUnit.SECONDS));
                } catch (InterruptedException failure) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(failure);
                }
                return null;
            });
            assertTrue(started.await(5, TimeUnit.SECONDS));
            for (int index = 0; index < 256; index++) {
                persistence.submit(() -> null);
            }
            assertTrue(persistence.submit(() -> null).isCompletedExceptionally());
        } finally {
            release.countDown();
            assertTrue(persistence.drain(Duration.ofSeconds(5)));
        }
    }
}
