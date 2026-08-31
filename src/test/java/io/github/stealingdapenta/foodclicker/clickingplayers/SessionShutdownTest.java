package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

class SessionShutdownTest {

    @Test
    void shutsDownReadyAndLoadingSessions() {
        ClickingPlayer readySession = Mockito.mock(ClickingPlayer.class);
        ClickingPlayer loadingSession = Mockito.mock(ClickingPlayer.class);

        List<RuntimeException> failures = SessionShutdown.shutdownSessions(List.of(readySession, loadingSession));

        verify(readySession).shutdownAndSave();
        verify(loadingSession).shutdownAndSave();
        assertEquals(0, failures.size());
    }

    @Test
    void removesSessionOnlyAfterItsDataIsSaved() {
        ClickingPlayer session = Mockito.mock(ClickingPlayer.class);
        Runnable removeSession = Mockito.mock(Runnable.class);

        SessionShutdown.saveThenRemove(session, removeSession);

        InOrder order = inOrder(session, removeSession);
        order.verify(session).shutdownAndSave();
        order.verify(removeSession).run();
    }

    @Test
    void continuesShutdownAfterOneSessionCannotBeSaved() {
        ClickingPlayer failingSession = Mockito.mock(ClickingPlayer.class);
        ClickingPlayer nextSession = Mockito.mock(ClickingPlayer.class);
        UncheckedIOException failure = new UncheckedIOException(new IOException("disk full"));
        doThrow(failure).when(failingSession).shutdownAndSave();

        List<RuntimeException> failures = SessionShutdown.shutdownSessions(List.of(failingSession, nextSession));

        verify(nextSession).shutdownAndSave();
        assertEquals(List.of(failure), failures);
    }
}
