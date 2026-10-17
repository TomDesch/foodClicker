package io.github.stealingdapenta.foodclicker.basics;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class GameInventory implements InventoryHolder {

    private final ClickingPlayer session;
    private final Inventory inventory;

    public GameInventory(ClickingPlayer session, String title) {
        this.session = session;
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public ClickingPlayer getSession() {
        return session;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
