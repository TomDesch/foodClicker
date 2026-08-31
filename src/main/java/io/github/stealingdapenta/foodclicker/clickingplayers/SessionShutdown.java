package io.github.stealingdapenta.foodclicker.clickingplayers;

import java.util.Collection;

public final class SessionShutdown {

    private SessionShutdown() {
    }

    public static void saveReadySessions(Collection<ClickingPlayer> sessions) {
        sessions.stream()
                .filter(ClickingPlayer::isReady)
                .forEach(ClickingPlayer::shutdownAndSave);
    }
}
