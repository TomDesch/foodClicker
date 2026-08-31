package io.github.stealingdapenta.foodclicker.clickingplayers;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SessionShutdown {

    private SessionShutdown() {
    }

    public static List<RuntimeException> shutdownSessions(Collection<ClickingPlayer> sessions) {
        List<RuntimeException> failures = new ArrayList<>();
        for (ClickingPlayer session : sessions) {
            try {
                session.shutdownAndSave();
            } catch (UncheckedIOException | IllegalStateException failure) {
                failures.add(failure);
            }
        }
        return failures;
    }

    public static void saveThenRemove(ClickingPlayer session, Runnable removeSession) {
        session.shutdownAndSave();
        removeSession.run();
    }
}
