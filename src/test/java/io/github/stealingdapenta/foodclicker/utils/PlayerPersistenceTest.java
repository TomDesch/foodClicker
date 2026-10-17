package io.github.stealingdapenta.foodclicker.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
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
            assertEquals(IntStream.range(0, 20).boxed().toList(), read.join());
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
            assertThrows(CompletionException.class, failed::join);
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

    @Test
    void finalShutdownSnapshotsWaitForCapacityInsteadOfBeingDroppedByAFullQueue() throws Exception {
            PlayerPersistence persistence = new PlayerPersistence();
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            CountDownLatch submitting = new CountDownLatch(1);
            CountDownLatch returned = new CountDownLatch(1);
            AtomicReference<CompletableFuture<String>> finalWrite = new AtomicReference<>();
            Thread submitter = new Thread(() -> {
                submitting.countDown();
                finalWrite.set(persistence.submit(() -> "saved"));
                returned.countDown();
            });
            try {
                persistence.submit(() -> {
                    started.countDown();
                    try {
                        assertTrue(release.await(5, TimeUnit.SECONDS));
                    } catch (InterruptedException failure) {
                        throw new IllegalStateException(failure);
                    }
                    return null;
                });
                assertTrue(started.await(5, TimeUnit.SECONDS));
                for (int index = 0; index < 256; index++) {
                    persistence.submit(() -> null);
                }
                persistence.beginShutdown(Duration.ofSeconds(5));
                submitter.start();
                assertTrue(submitting.await(5, TimeUnit.SECONDS));
                assertFalse(returned.await(100, TimeUnit.MILLISECONDS));
                release.countDown();
                submitter.join(5000);
                assertFalse(submitter.isAlive());
                assertTrue(persistence.drain(Duration.ofSeconds(5)));
                assertEquals("saved", finalWrite.get().join());
            } finally {
                release.countDown();
                persistence.drain(Duration.ofSeconds(5));
                submitter.join(5000);
            }
    }
}
