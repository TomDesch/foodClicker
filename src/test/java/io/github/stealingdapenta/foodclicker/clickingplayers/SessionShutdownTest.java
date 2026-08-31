package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

class SessionShutdownTest {

    @Test
    void savesOnlyFullyInitializedSessions() {
        ClickingPlayer readySession = Mockito.mock(ClickingPlayer.class);
        ClickingPlayer loadingSession = Mockito.mock(ClickingPlayer.class);
        when(readySession.isReady()).thenReturn(true);
        when(loadingSession.isReady()).thenReturn(false);

        SessionShutdown.saveReadySessions(List.of(readySession, loadingSession));

        verify(readySession).shutdownAndSave();
        verify(loadingSession, never()).shutdownAndSave();
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
}
