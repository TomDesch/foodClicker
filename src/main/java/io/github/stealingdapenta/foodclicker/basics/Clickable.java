package io.github.stealingdapenta.foodclicker.basics;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnEvents;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Clickable {
    private static final Random r = new Random();
    int slotPosition;
    ItemStack itemStack;
    int duration; // howlong it stays, in seconds
    Inventory inv;
    ItemStack replacedItem;
    UpgradesBasedOnEvents bonus;

    public Clickable(int duration, Inventory inv, UpgradesBasedOnEvents bonus, ClickingPlayer cp) {
        this.inv = inv;
        this.slotPosition = getNewRandomSlotposition(getInv());
        this.duration = duration;
        this.bonus = bonus;
        this.replacedItem = getItemAtSlot(getInv(), getSlotPosition());
        this.itemStack = createItemStack(cp);

    }

    private ItemStack createItemStack(ClickingPlayer cp) {
        if (bonus.getBoostDuration(cp) == -1) {
            return new ItemBuilder(bonus.getMaterial()).setDisplayName("&6" + bonus.getName())
                                                       .addLore("&eGet a permanent &b" + bonus.getMultiplierIncrease() + "x " + bonus.getName() + "&e.")
                                                       .addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setGlowing(bonus.getMultiplierIncrease() < 1).create();
        } else {
            if (bonus.getBuildings() != null && bonus.getMultiplierIncrease() < 1) {
                return new ItemBuilder(bonus.getMaterial()).setDisplayName("&6" + bonus.getName())
                                                           .addLore("&eGet a &b" + bonus.getMultiplierIncrease() + "x " + bonus.getBuildings().getName() + " &efor &b" + bonus.getBoostDuration(cp) + "&bs&e.")
                                                           .addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setGlowing(bonus.getMultiplierIncrease() < 1).create();
            } else {
                return new ItemBuilder(bonus.getMaterial()).setDisplayName("&6" + bonus.getName())
                                                           .addLore("&eGet a &b" + bonus.getMultiplierIncrease() + "x " + bonus.getName() + " &efor &b" + bonus.getBoostDuration(cp) + "&bs&e.")
                                                           .addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setGlowing(bonus.getMultiplierIncrease() < 1).create();
            }
        }
    }

    public Inventory getInv() {
        return inv;
    }

    public ItemStack getReplacedItem() {
        return replacedItem;
    }

    public UpgradesBasedOnEvents getBonus() {
        return bonus;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNewRandomSlotposition(Inventory inv) {
        int goodSlot = -1;
        if (inv == null) return goodSlot;
        ArrayList<Integer> possibleSpots = new ArrayList<>();

        for (int i = 0; i < inv.getSize(); i++) {
            if (itemIsPane(inv.getItem(i))) {
                possibleSpots.add(i);
            }
        }
        goodSlot = possibleSpots.get(r.nextInt(possibleSpots.size()));
        return goodSlot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    private ItemStack getItemAtSlot(Inventory inv, int slot) {
        return inv.getItem(slot);
    }

    private boolean itemIsPane(ItemStack i) {
        return (i != null) && i.getType().toString().toLowerCase().contains("pane");
    }

    public int getSlotPosition() {
        return slotPosition;
    }

    public void setSlotPosition(int slotPosition) {
        this.slotPosition = slotPosition;
    }

}
