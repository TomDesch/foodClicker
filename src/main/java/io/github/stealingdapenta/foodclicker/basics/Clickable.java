package io.github.stealingdapenta.foodclicker.basics;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnEvents;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.Random;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Getter
public class Clickable {

    private static final Random r = new Random();
    int slotPosition;
    ItemStack itemStack;
    int duration; // how long it stays, in seconds
    Inventory inv;
    ItemStack replacedItem;
    UpgradesBasedOnEvents bonus;

    public Clickable(int duration, Inventory inv, UpgradesBasedOnEvents bonus, ClickingPlayer cp) {
        this.inv = inv;
        this.slotPosition = getNewRandomSlotPosition(getInv());
        this.duration = duration;
        this.bonus = bonus;
        this.replacedItem = getItemAtSlot(getInv(), getSlotPosition());
        this.itemStack = createItemStack(cp);

    }

    private ItemStack createItemStack(ClickingPlayer cp) {
        if (bonus.getBoostDuration(cp) == -1) {
            return new ItemBuilder(bonus.getMaterial()).setDisplayName("&6" + bonus.getName())
                                                       .addLore("&eGet a permanent &b" + bonus.getMultiplierIncrease() + "x " + bonus.getName() + "&e.")
                                                       .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                                                       .setGlowing(bonus.getMultiplierIncrease() < 1)
                                                       .create();
        } else {
            if (bonus.getBuildings() != null && bonus.getMultiplierIncrease() < 1) {
                return new ItemBuilder(bonus.getMaterial()).setDisplayName("&6" + bonus.getName())
                                                           .addLore("&eGet a &b" + bonus.getMultiplierIncrease() + "x " + bonus.getBuildings()
                                                                                                                               .getName() + " &efor &b"
                                                                            + bonus.getBoostDuration(cp) + "&bs&e.")
                                                           .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                                                           .setGlowing(bonus.getMultiplierIncrease() < 1)
                                                           .create();
            } else {
                return new ItemBuilder(bonus.getMaterial()).setDisplayName("&6" + bonus.getName())
                                                           .addLore("&eGet a &b" + bonus.getMultiplierIncrease() + "x " + bonus.getName() + " &efor &b"
                                                                            + bonus.getBoostDuration(cp) + "&bs&e.")
                                                           .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                                                           .setGlowing(bonus.getMultiplierIncrease() < 1)
                                                           .create();
            }
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNewRandomSlotPosition(Inventory inv) {
        int goodSlot = -1;
        if (inv == null) {
            return goodSlot;
        }
        ArrayList<Integer> possibleSpots = new ArrayList<>();

        for (int i = 0; i < inv.getSize(); i++) {
            if (itemIsPane(inv.getItem(i))) {
                possibleSpots.add(i);
            }
        }
        goodSlot = possibleSpots.get(r.nextInt(possibleSpots.size()));
        return goodSlot;
    }

    private ItemStack getItemAtSlot(Inventory inv, int slot) {
        return inv.getItem(slot);
    }

    private boolean itemIsPane(ItemStack i) {
        return (i != null) && i.getType()
                               .toString()
                               .toLowerCase()
                               .contains("pane");
    }

}
