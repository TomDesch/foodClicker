package io.github.stealingdapenta.foodclicker.upgrades;

import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.CHEAP;
import static org.bukkit.Material.APPLE;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum UpgradesBasedOnStats {

    CLICK_0("Clickyfier", APPLE, 50D, 1.10, "cookie", 10, "Click Multiplier", "• Clicking is 10% more effective."),
    CLICK_1("Clickyfier", APPLE, 100D, 1.10, "cookie", 100, "Click Multiplier", "&2Congratulations! You clicked &b100 &2times!",
            "• Clicking is 10% more effective."),
    CLICK_2("Clickyfier", APPLE, 250D, 1.10, "cookie", 200, "Click Multiplier", "• Clicking is 10% more effective."),
    CLICK_3("Clickyfier", APPLE, 500D, 1.15, "cookie", 250, "Click Multiplier", "• Clicking is 15% more effective."),
    CLICK_4("Clickyfier", APPLE, 1_000D, 1.20, "cookie", 500, "Click Multiplier", "• Clicking is 20% more effective."),
    CLICK_4B("Clickyfier", APPLE, 1_500D, 1.25, "cookie", 750, "Click Multiplier", "• Clicking is 25% more effective."),
    CLICK_5("Clickyfier", APPLE, 5_000D, 1.30, "cookie", 1000, "Click Multiplier", "• Clicking is 30% more effective."),
    CLICK_6("Clickyfier", APPLE, 10_000D, 1.35, "cookie", 2000, "Click Multiplier", "• Clicking is 35% more effective."),
    CLICK_7("Clickyfier", APPLE, 50_000D, 1.40, "cookie", 5000, "Click Multiplier", "• Clicking is 40% more effective."),
    CLICK_8("Clickyfier", APPLE, 100_000D, 1.45, "cookie", 7500, "Click Multiplier", "• Clicking is 45% more effective."),
    CLICK_9("Clickyfier", APPLE, 250_000D, 1.50, "cookie", 10000, "Click Multiplier", "• Clicking is 50% more effective."),
    CLICK_10("Clickyfier", APPLE, 1_000_000D, 2, "cookie", 100_000, "Click Multiplier", "• Clicking is 100% more effective."),

    CLICK_11("Mouse Breaker", APPLE, 10_000_000D, 2, "cookie", 500_000, "Click Multiplier", "• Clicking is 100% more effective.");

    @Getter
    private final String name;
    @Getter
    private final String requiredKey;
    @Getter
    private final int requiredValue;
    @Getter
    private final List<String> lore;
    private final double cost;
    @Getter
    private final double multiplierIncrease;
    @Getter
    private final String affectingKey;
    @Getter
    private final String unlockedKey;
    private final InventoryManager im = InventoryManager.getInstance();
    @Getter
    private final Material material;

    UpgradesBasedOnStats(String name, Material material, double cost, double multiplierIncrease, String requiredKey, int requiredValue, String affectingKey,
                         String... lore) {
        this.name = name;
        this.requiredKey = requiredKey;
        this.requiredValue = requiredValue;
        this.lore = Arrays.asList(lore);
        this.material = material;
        this.cost = cost;
        this.multiplierIncrease = multiplierIncrease;
        this.unlockedKey = (getRequiredKey() + getRequiredValue());
        this.affectingKey = affectingKey;
    }

    public double getCost(ClickingPlayer cp) {
        return cost - (cost * CHEAP.getCurrentBonus(cp) / 100);
    }

    private boolean requiredValueIsMet(ClickingPlayer cp) {
        if (cp.getData()
              .getGeneralIntegerStats()
              .containsKey(getRequiredKey())) {
            return cp.getData()
                     .getGeneralIntegerStats()
                     .get(getRequiredKey()) >= getRequiredValue();
        } else if (cp.getData()
                     .getGeneralDoubleStats()
                     .containsKey(getRequiredKey())) {
            return cp.getData()
                     .getGeneralDoubleStats()
                     .get(getRequiredKey()) >= getRequiredValue();
        } else {
            System.out.println("FoodClicker: Error in Upgrades Based On Stats!");
            return false;
        }
    }

    public boolean upgradeIsUnlocked(ClickingPlayer cp) {
        return cp.getData()
                 .getUpgradesUnlocked()
                 .get(getUnlockedKey());
    }

    private void setUpgradeUnlocked(ClickingPlayer cp) {
        cp.getData()
          .getUpgradesUnlocked()
          .put(getUnlockedKey(), true);
    }

    private void getEffectsFromUnlocking(ClickingPlayer cp) {
        if (cp.getData()
              .getGeneralDoubleStats()
              .containsKey(getAffectingKey())) {
            cp.getData()
              .getGeneralDoubleStats()
              .put(getAffectingKey(), cp.getData()
                                        .getGeneralDoubleStats()
                                        .get(getAffectingKey()) * getMultiplierIncrease());
        } else {
            System.out.println("FoodClicker: Error in Upgrades Based On Stats!");
        }
    }

    public void possiblyBuyUpgrade(ClickingPlayer cp, Inventory inv, int slot) {
        if (cp.canAfford(this.getCost(cp))) {
            cp.doSoundEffect(2);
            cp.doFireWorks(2, 0);
            cp.doFireWorks(2, 1);

            cp.pay(this.getCost(cp));
            setUpgradeUnlocked(cp);
            getEffectsFromUnlocking(cp);

            // remove item from shop
            inv.setItem(slot, new ItemStack(Material.AIR));
            cp.updateUpgradesWindow(inv);
            cp.setIncomePerSecond(cp.calculateTotalIncomePerSecond());
            cp.doMessage("You bought &b" + getName() + "&e for only &a£" + getCost(cp) + "&e!");
        } else {
            cp.doMessage("You can't afford this upgrade.");
            cp.doSoundEffect(0);
        }
    }

    public boolean requirementMetAndNotUnlocked(ClickingPlayer cp) {
        return (requiredValueIsMet(cp) && !upgradeIsUnlocked(cp));
    }

    public ItemStack createItem(ClickingPlayer cp) {
        String lore1 = cp.getSettings()
                         .getPrimaryLoreColor(); // blue
        String lore2 = cp.getSettings()
                         .getSecondaryLoreColor(); // green

        return new ItemBuilder(getMaterial()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                                             .setDisplayName(lore1 + getName())
                                             .addLore(getLore().stream()
                                                               .map(s -> lore2 + s)
                                                               .collect(Collectors.toList()))
                                             .addLore("&6Price: &c£" + im.truncateNumber(getCost(cp), cp))
                                             .setGlowing(true)
                                             .create();
    }
}
