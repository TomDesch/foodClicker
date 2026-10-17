package io.github.stealingdapenta.foodclicker.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.stealingdapenta.foodclicker.basics.GameInventory;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import java.nio.file.Path;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InventoryOwnershipTest {

    @TempDir
    Path directory;

    @Test
    void spoofTitleCannotTriggerGameplayAndActualOwnedGuiIgnoresDisplayNameChanges() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.openGame();
            Inventory inventory = mock(Inventory.class);
            InventoryView view = mock(InventoryView.class);
            when(view.getTopInventory()).thenReturn(inventory);
            when(view.getPlayer()).thenReturn(fixture.player);
            when(view.getTitle()).thenReturn("TestPlayer's Factory");
            when(inventory.getSize()).thenReturn(54);
            when(cp.getGui().getGameWindow()).thenReturn(inventory);
            Listeners listeners = new Listeners();
            InventoryClickEvent spoof = click(view);
            listeners.onInventoryClick(spoof);
            assertFalse(spoof.isCancelled());
            assertEquals(0, cp.getData().getGeneralIntegerStats().get("cookie"));

            GameInventory holder = mock(GameInventory.class);
            when(holder.getSession()).thenReturn(cp);
            when(inventory.getHolder()).thenReturn(holder);
            when(fixture.player.getDisplayName()).thenReturn("ChangedName");
            InventoryClickEvent legitimate = click(view);
            listeners.onInventoryClick(legitimate);
            assertTrue(legitimate.isCancelled());
            assertEquals(1, cp.getData().getGeneralIntegerStats().get("cookie"));

            Inventory replacement = mock(Inventory.class);
            when(cp.getGui().getGameWindow()).thenReturn(replacement);
            InventoryClickEvent stale = click(view);
            listeners.onInventoryClick(stale);
            assertTrue(stale.isCancelled());
            assertEquals(1, cp.getData().getGeneralIntegerStats().get("cookie"));
        }
    }

    @Test
    void dragsAreCancelledForOwnedAndStalePluginInventoriesButNotOrdinaryChests() {
        Inventory inventory = mock(Inventory.class);
        InventoryView view = mock(InventoryView.class);
        when(view.getTopInventory()).thenReturn(inventory);
        InventoryDragEvent drag = mock(InventoryDragEvent.class);
        when(drag.getView()).thenReturn(view);
        Listeners listeners = new Listeners();
        listeners.onInventoryDrag(drag);
        verify(drag, never()).setCancelled(true);
        when(inventory.getHolder()).thenReturn(mock(GameInventory.class));
        listeners.onInventoryDrag(drag);
        verify(drag).setCancelled(true);
    }

    private InventoryClickEvent click(InventoryView view) {
        return new InventoryClickEvent(view, InventoryType.SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ALL);
    }
}
