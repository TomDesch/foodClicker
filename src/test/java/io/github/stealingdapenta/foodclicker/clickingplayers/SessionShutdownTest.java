package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class SessionShutdownTest {

    @Test
    void queuesEverySessionEvenIfOneSnapshotFails() {
        ClickingPlayer failing = mock(ClickingPlayer.class);
        ClickingPlayer loading = mock(ClickingPlayer.class);
        when(failing.shutdownAndSave()).thenThrow(new IllegalStateException("cannot snapshot"));
        when(loading.shutdownAndSave()).thenReturn(CompletableFuture.completedFuture(null));

        List<CompletableFuture<Void>> results = SessionShutdown.shutdownSessions(List.of(failing, loading));

        assertTrue(results.get(0).isCompletedExceptionally());
        assertFalse(results.get(1).isCompletedExceptionally());
        verify(loading).shutdownAndSave();
    }

    @Test
    void shutdownDoesNotWaitForWorkerCompletionsOnServerThread() {
        ClickingPlayer session = mock(ClickingPlayer.class);
        CompletableFuture<Void> pending = new CompletableFuture<>();
        when(session.shutdownAndSave()).thenReturn(pending);
        assertSame(pending, SessionShutdown.shutdownSessions(List.of(session)).getFirst());
        assertFalse(pending.isDone());
    }
}
