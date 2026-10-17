package io.github.stealingdapenta.foodclicker.clickingplayers;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SessionShutdown {

    private SessionShutdown() {
    }

    public static List<CompletableFuture<Void>> shutdownSessions(Collection<ClickingPlayer> sessions) {
        List<CompletableFuture<Void>> results = new ArrayList<>();
        for (ClickingPlayer session : sessions) {
            try {
                results.add(session.shutdownAndSave());
            } catch (UncheckedIOException | IllegalStateException failure) {
                results.add(CompletableFuture.failedFuture(failure));
            }
        }
        return results;
    }
}
