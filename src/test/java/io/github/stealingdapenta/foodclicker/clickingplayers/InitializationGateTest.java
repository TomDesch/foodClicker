package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class InitializationGateTest {

    @Test
    void queuesActionsUntilInitializationCompletes() {
        InitializationGate gate = new InitializationGate();
        AtomicInteger calls = new AtomicInteger();

        gate.runWhenReady(calls::incrementAndGet);
        assertEquals(0, calls.get());

        gate.markReady();
        assertEquals(1, calls.get());
        assertTrue(gate.isReady());
    }

    @Test
    void runsActionsImmediatelyAfterInitialization() {
        InitializationGate gate = new InitializationGate();
        AtomicInteger calls = new AtomicInteger();
        gate.markReady();

        gate.runWhenReady(calls::incrementAndGet);

        assertEquals(1, calls.get());
    }

    @Test
    void completesQueuedActionsOnlyOnce() {
        InitializationGate gate = new InitializationGate();
        AtomicInteger calls = new AtomicInteger();
        gate.runWhenReady(calls::incrementAndGet);

        gate.markReady();
        gate.markReady();

        assertEquals(1, calls.get());
    }

    @Test
    void cancellationDiscardsActionsAndPreventsInitialization() {
        InitializationGate gate = new InitializationGate();
        AtomicInteger calls = new AtomicInteger();
        gate.runWhenReady(calls::incrementAndGet);

        gate.cancel();
        gate.markReady();
        gate.runWhenReady(calls::incrementAndGet);

        assertEquals(0, calls.get());
        assertTrue(gate.isCancelled());
    }
}
