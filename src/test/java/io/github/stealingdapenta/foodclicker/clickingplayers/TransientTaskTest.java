package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.stealingdapenta.foodclicker.basics.Buildings;
import io.github.stealingdapenta.foodclicker.basics.Clickable;
import io.github.stealingdapenta.foodclicker.basics.GameInventory;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnEvents;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.SessionFixture;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;

class TransientTaskTest {

    @TempDir
    Path directory;

    @Test
    void buyMaxIsSingleOwnedTaskAndStopsOnCloseLogoutPrestigeAndStaleCallbacks() {
        try (SessionFixture fixture = new SessionFixture(directory);
             MockedConstruction<ItemBuilder> items = mockConstruction(ItemBuilder.class, withSettings().defaultAnswer(RETURNS_SELF))) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            List<Runnable> purchases = new ArrayList<>();
            when(fixture.scheduler.scheduleSyncRepeatingTask(any(), any(Runnable.class), anyLong(), anyLong()))
                    .thenAnswer(call -> {
                        purchases.add(call.getArgument(1));
                        return purchases.size();
                    });
            cp.openGame();
            Inventory window = mock(Inventory.class);
            GameInventory holder = mock(GameInventory.class);
            when(holder.getSession()).thenReturn(cp);
            when(window.getHolder()).thenReturn(holder);
            when(cp.getGui().getGameWindow()).thenReturn(window);
            when(cp.getGui().getGameState()).thenReturn(1);
            InventoryView view = mock(InventoryView.class);
            when(view.getTopInventory()).thenReturn(window);
            when(fixture.player.getOpenInventory()).thenReturn(view);
            cp.getData().getGeneralBigDecimals().put("money", new BigDecimal("1000000"));
            cp.doMaxPurchases(Buildings.MOM);
            Runnable oldTask = purchases.getLast();
            cp.doMaxPurchases(Buildings.MOM);
            Runnable activeTask = purchases.getLast();
            oldTask.run();
            assertEquals(0, cp.getData().getSpecificBuildingAmount(Buildings.MOM));
            activeTask.run();
            assertEquals(1, cp.getData().getSpecificBuildingAmount(Buildings.MOM));
            cp.closeGame();
            activeTask.run();
            assertEquals(1, cp.getData().getSpecificBuildingAmount(Buildings.MOM));
            fixture.finishIo();
            cp.openGame();
            cp.doMaxPurchases(Buildings.MOM);
            Runnable beforePrestige = purchases.getLast();
            cp.getData().doPrestige();
            beforePrestige.run();
            assertEquals(0, cp.getData().getSpecificBuildingAmount(Buildings.MOM));
            cp.doMaxPurchases(Buildings.MOM);
            Runnable beforeLogout = purchases.getLast();
            cp.shutdownAndSave();
            beforeLogout.run();
            assertEquals(0, cp.getData().getSpecificBuildingAmount(Buildings.MOM));
        }
    }

    @Test
    void boostTerminationCancelsExpiryAndUndoesExactlyOnceBeforePrestigeOrSaveRetry() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.openGame();
            List<Runnable> expiries = new ArrayList<>();
            BukkitTask expiryTask = mock(BukkitTask.class);
            when(fixture.scheduler.runTaskLater(any(), any(Runnable.class), anyLong()))
                    .thenAnswer(call -> {
                        expiries.add(call.getArgument(1));
                        return expiryTask;
                    });
            Clickable boost = mock(Clickable.class);
            when(boost.getBonus()).thenReturn(UpgradesBasedOnEvents.CLICKBONUS_1);
            cp.getData().getGeneralDoubleStats().put("Click Multiplier", 7D);
            cp.getActiveBoosts().add(boost);
            cp.autoTerminateUpgrades(boost);
            cp.getData().doPrestige();
            verify(expiryTask).cancel();
            assertTrue(cp.getActiveBoosts().isEmpty());
            assertEquals(1D, cp.getData().getGeneralDoubleStats().get("Click Multiplier"));
            expiries.getFirst().run();
            cp.possiblyTerminate();
            cp.shutdownAndSave();
            cp.shutdownAndSave();
            assertEquals(1D, cp.getData().getGeneralDoubleStats().get("Click Multiplier"));
            fixture.finishIo();
            assertEquals(1D, fixture.files.read(cp.getPlayerId()).getDouble("Click Multiplier"));
        }
    }
}
