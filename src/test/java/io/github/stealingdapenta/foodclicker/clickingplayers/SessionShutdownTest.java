package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
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
}
