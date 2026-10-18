package io.github.stealingdapenta.foodclicker.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.SessionFixture;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FoodClickerCommandTest {

    @TempDir
    Path directory;

    @Test
    void reusedPlayerHandleGetsRecoveryMessageAndCanOpenOnlyTheReplacementSession() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer previous = fixture.connect();
            fixture.finishIo();
            previous.shutdownAndSave();

            FoodClickerCommand command = new FoodClickerCommand();
            assertTrue(command.onCommand(fixture.player, null, "foodclicker", new String[0]));
            verify(fixture.player).sendMessage(
                    "FoodClicker: Recovering your previous session. Use /foodclicker again when it has saved.");
            assertFalse(previous.isGameOpen());

            fixture.finishIo();
            fixture.finishIo();
            ClickingPlayer replacement = InventoryManager.getInstance().getClickingPlayerMap()
                    .get(fixture.player.getUniqueId());
            assertNotSame(previous, replacement);
            assertFalse(replacement.isGameOpen());
            AntiSpamManager.getInstance().getSpammersMap().remove(fixture.player.getUniqueId());
            command.onCommand(fixture.player, null, "foodclicker", new String[0]);
            assertTrue(replacement.isGameOpen());
            assertFalse(previous.isGameOpen());
        } finally {
            AntiSpamManager.getInstance().getSpammersMap().clear();
        }
    }
}
