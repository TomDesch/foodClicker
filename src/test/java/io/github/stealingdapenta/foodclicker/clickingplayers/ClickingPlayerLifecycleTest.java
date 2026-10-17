package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.SessionFixture;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClickingPlayerLifecycleTest {

    @TempDir
    Path directory;

    @Test
    void initializationIsPassiveAndSnapshotsDoNotConsumeAfkIncome() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            fixture.files.save(fixture.player.getUniqueId(), "money: '20'\nloggedOutTime: 100\ngameCloseTime: 50");
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            assertTrue(cp.isReady());
            assertNull(cp.getGui());
            assertNull(cp.getRepeatingClickerTask());
            String first = cp.getData().snapshot("TestPlayer");
            assertEquals(first, cp.getData().snapshot("TestPlayer"));
            assertEquals(new BigDecimal("20"), cp.getData().getGeneralBigDecimals().get("money"));
            assertTrue(first.contains("loggedOutTime: 100"));
            assertTrue(first.contains("gameCloseTime: 50"));
        }
    }

    @Test
    void failedLoadClearsPendingCommandsPreservesCorruptDataAndAllowsRetry() throws IOException {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            Path file = fixture.files.getUserFiles().toPath().resolve(fixture.player.getUniqueId() + ".yml");
            Files.writeString(file, "money: [broken");
            ClickingPlayer failed = fixture.connect();
            AtomicBoolean ran = new AtomicBoolean();
            failed.runWhenReady(() -> ran.set(true));
            fixture.finishIo();
            assertFalse(ran.get());
            assertFalse(failed.isReady());
            assertFalse(InventoryManager.getInstance().isCurrent(failed));
            assertEquals("money: [broken", Files.readString(file));
            Files.writeString(file, "money: '42'");
            ClickingPlayer recovered = fixture.connect();
            fixture.finishIo();
            assertTrue(recovered.isReady());
            assertEquals(new BigDecimal("42"), recovered.getData().getGeneralBigDecimals().get("money"));
        }
    }

    @Test
    void closeReopenAndDisconnectSaveOrderedSnapshotsAndBlockReopenAfterFailure() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.openGame();
            cp.getData().getGeneralBigDecimals().put("money", new BigDecimal("100"));
            UUID playerId = fixture.player.getUniqueId();
            doThrow(new UncheckedIOException(new IOException("disk full")))
                    .when(fixture.files).save(eq(playerId), anyString());
            cp.closeGame();
            fixture.finishIo();
            assertNull(cp.getRepeatingClickerTask());
            assertTrue(InventoryManager.getInstance().isCurrent(cp));
            cp.openGame();
            fixture.finishIo();
            assertNull(cp.getRepeatingClickerTask());
            doCallRealMethod().when(fixture.files).save(eq(playerId), anyString());
            cp.openGame();
            fixture.finishIo();
            assertNotNull(cp.getRepeatingClickerTask());
            cp.getData().getGeneralBigDecimals().put("money", new BigDecimal("200"));
            cp.closeGame();
            cp.shutdownAndSave();
            fixture.finishIo();
            assertEquals("200", fixture.files.read(fixture.player.getUniqueId()).getString("money"));
            cp.openGame();
            assertNull(cp.getRepeatingClickerTask());
        }
    }

    @Test
    void reconnectWaitsForPreviousSaveAndNeverReusesOldPlayerOrStaleCallback() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer first = fixture.connect();
            fixture.finishIo();
            first.getData().getGeneralBigDecimals().put("money", new BigDecimal("123"));
            first.shutdownAndSave();
            Player reconnected = mock(Player.class);
            UUID playerId = fixture.player.getUniqueId();
            when(reconnected.getUniqueId()).thenReturn(playerId);
            when(reconnected.getName()).thenReturn("Renamed");
            when(reconnected.isOnline()).thenReturn(true);
            InventoryManager.getInstance().connect(reconnected);
            assertSame(first, InventoryManager.getInstance().getClickingPlayerMap().get(reconnected.getUniqueId()));
            fixture.finishIo();
            fixture.finishIo();
            ClickingPlayer current = InventoryManager.getInstance().getClickingPlayerMap().get(reconnected.getUniqueId());
            assertNotSame(first, current);
            assertSame(reconnected, current.getPlayer());
            assertEquals(new BigDecimal("123"), current.getData().getGeneralBigDecimals().get("money"));
            InventoryManager.getInstance().remove(first);
            assertTrue(InventoryManager.getInstance().isCurrent(current));
        }
    }

    @Test
    void disconnectDuringLoadAccountsConnectionOnceWithoutWritingIncompleteGameplayState() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            fixture.files.save(fixture.player.getUniqueId(), "money: '42'\nloggedOutDate: 900");
            AtomicLong clock = new AtomicLong(1000);
            ClickingPlayer cp = new ClickingPlayer(fixture.player, clock::get);
            InventoryManager.getInstance().addClickingPlayerToMap(cp);
            AtomicBoolean ran = new AtomicBoolean();
            cp.runWhenReady(() -> ran.set(true));
            clock.set(1020);
            cp.shutdownAndSave();
            cp.shutdownAndSave();
            fixture.finishIo();
            assertFalse(ran.get());
            assertNull(cp.getGui());
            assertEquals("42", fixture.files.read(cp.getPlayerId()).getString("money"));
            assertEquals(20, fixture.files.read(cp.getPlayerId()).getLong("gameCloseTime"));
            assertEquals(100, fixture.files.read(cp.getPlayerId()).getLong("loggedOutTime"));
            assertFalse(fixture.files.read(cp.getPlayerId()).contains("cookie"));
        }
    }
}
