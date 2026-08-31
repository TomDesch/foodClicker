package io.github.stealingdapenta.foodclicker.clickingplayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class InitializationGate {

    private final List<Runnable> pendingActions = new ArrayList<>();
    private boolean ready;
    private boolean cancelled;

    void runWhenReady(Runnable action) {
        Objects.requireNonNull(action, "action");
        if (cancelled) {
            return;
        }
        if (ready) {
            action.run();
            return;
        }
        pendingActions.add(action);
    }

    void markReady() {
        if (ready || cancelled) {
            return;
        }
        ready = true;
        List<Runnable> actions = new ArrayList<>(pendingActions);
        pendingActions.clear();
        actions.forEach(Runnable::run);
    }

    boolean isReady() {
        return ready;
    }

    void cancel() {
        cancelled = true;
        pendingActions.clear();
    }

    boolean isCancelled() {
        return cancelled;
    }
}
