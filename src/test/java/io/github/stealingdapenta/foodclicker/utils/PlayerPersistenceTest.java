package io.github.stealingdapenta.foodclicker.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PlayerPersistenceTest {

    @TempDir
    Path directory;

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failedDrainBlocksReplacementUntilEvenUninterruptibleFileWorkHasFinished(boolean interruptDrain) throws Exception {
        CountDownLatch moving = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean interrupted = new AtomicBoolean();
        FileManager files = new FileManager(directory::toFile) {
            @Override
            void replace(Path source, Path destination) throws IOException {
                moving.countDown();
                boolean finished = false;
                while (!finished) {
                    try {
                        finished = release.await(5, TimeUnit.SECONDS);
                    } catch (InterruptedException failure) {
                        interrupted.set(true);
                    }
                }
                super.replace(source, destination);
            }
        };
        UUID id = UUID.randomUUID();
        PlayerPersistence old = new PlayerPersistence();
        AtomicBoolean queuedRan = new AtomicBoolean();
        try {
            CompletableFuture<Void> write = old.submit(() -> {
                files.save(id, "money: '10'");
                return null;
            });
            assertTrue(moving.await(5, TimeUnit.SECONDS));
            CompletableFuture<Void> queued = old.submit(() -> {
                queuedRan.set(true);
                return null;
            });
            if (interruptDrain) {
                Thread.currentThread().interrupt();
            }
            boolean drained = old.drain(Duration.ZERO);
            boolean drainInterrupted = Thread.interrupted();
            assertFalse(drained);
            assertEquals(interruptDrain, drainInterrupted);
            assertThrows(IllegalStateException.class, PlayerPersistence::new);
            assertTrue(write.isCompletedExceptionally());
            assertTrue(queued.isCompletedExceptionally());
            assertTrue(old.submit(() -> null).isCompletedExceptionally());
            release.countDown();
            assertTrue(old.drain(Duration.ofSeconds(5)));
            assertTrue(interrupted.get());
            assertFalse(queuedRan.get());
            PlayerPersistence replacement = new PlayerPersistence();
            try {
                replacement.submit(() -> {
                    files.save(id, "money: '20'");
                    return null;
                }).join();
                assertEquals("20", files.read(id).getString("money"));
            } finally {
                assertTrue(replacement.drain(Duration.ofSeconds(5)));
            }
        } finally {
            release.countDown();
            assertTrue(old.drain(Duration.ofSeconds(5)));
        }
    }

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
