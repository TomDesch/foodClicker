package io.github.stealingdapenta.foodclicker.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.SessionFixture;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReadOnlyCommandsTest {

    @TempDir
    Path directory;

    @Test
    void everyUnsupportedSenderGetsAResponseInsteadOfAPlayerCast() {
        CommandSender sender = mock(CommandSender.class);
        new FoodClickerCommand().onCommand(sender, null, "fc", new String[0]);
        new StatsCommand().onCommand(sender, null, "fcstats", new String[0]);
        new TopCommand().onCommand(sender, null, "fctop", new String[0]);
        verify(sender, times(3)).sendMessage("FoodClicker: This command is only available to players.");
    }

    @Test
    void deniedPrestigeCommandsNeverStartGameplayOrConsumeAfkCounters() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            fixture.files.save(fixture.player.getUniqueId(), "money: '20'\nloggedOutTime: 100\ngameCloseTime: 50");
            new StatsCommand().onCommand(fixture.player, null, "fcstats", new String[]{"true"});
            fixture.finishIo();
            AntiSpamManager.getInstance().getSpammersMap().clear();
            new TopCommand().onCommand(fixture.player, null, "fctop", new String[0]);
            fixture.finishIo();
            assertFalse(InventoryManager.getInstance().isPlayerInMap(fixture.player));
            assertEquals(100, fixture.files.read(fixture.player.getUniqueId()).getLong("loggedOutTime"));
            assertEquals(50, fixture.files.read(fixture.player.getUniqueId()).getLong("gameCloseTime"));
            verify(fixture.files, times(1)).save(any(), anyString());
            verifyNoInteractions(fixture.scheduler);
        }
    }

    @Test
    void activeSessionSnapshotUsesUnsavedStateWithoutChangingSessionOrAfkCounters() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.getData().getGeneralIntegerStats().put("cookie", 123);
            cp.getData().getPrestigeShopBonusses().put(PrestigeEnum.PRESTIGEBRAG, 1);
            AtomicBoolean read = new AtomicBoolean();
            PlayerSnapshots.read(fixture.plugin, fixture.player, config -> {
                assertEquals(123, config.getInt("cookie"));
                assertEquals(1, config.getInt(PrestigeEnum.PRESTIGEBRAG.getKey()));
                read.set(true);
            });
            assertTrue(read.get());
            assertTrue(InventoryManager.getInstance().isCurrent(cp));
            assertNull(cp.getRepeatingClickerTask());
            verify(fixture.files, never()).save(any(), anyString());
        }
    }

    @Test
    void disconnectedRequesterCannotReceiveAnAsynchronousSnapshot() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            AtomicBoolean read = new AtomicBoolean();
            PlayerSnapshots.read(fixture.plugin, fixture.player, config -> read.set(true));
            when(fixture.player.isOnline()).thenReturn(false);
            fixture.finishIo();
            assertFalse(read.get());
        }
    }
}
