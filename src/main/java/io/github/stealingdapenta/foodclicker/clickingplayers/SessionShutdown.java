package io.github.stealingdapenta.foodclicker.clickingplayers;

import java.util.Collection;

public final class SessionShutdown {

    private SessionShutdown() {
    }

    public static void shutdownSessions(Collection<ClickingPlayer> sessions) {
        sessions.forEach(ClickingPlayer::shutdownAndSave);
    }

    public static void saveThenRemove(ClickingPlayer session, Runnable removeSession) {
        session.shutdownAndSave();
        removeSession.run();
    }
}
