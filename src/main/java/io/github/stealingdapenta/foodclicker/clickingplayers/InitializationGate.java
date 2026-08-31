package io.github.stealingdapenta.foodclicker.clickingplayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class InitializationGate {

    private final List<Runnable> pendingActions = new ArrayList<>();
    private boolean ready;

    void runWhenReady(Runnable action) {
        Objects.requireNonNull(action, "action");
        if (ready) {
            action.run();
            return;
        }
        pendingActions.add(action);
    }

    void markReady() {
        if (ready) {
            return;
        }
        ready = true;
        List<Runnable> actions = new ArrayList<>(pendingActions);
        pendingActions.clear();
        actions.forEach(Runnable::run);
    }
}
