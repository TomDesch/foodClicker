package io.github.stealingdapenta.foodclicker.prestige;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Getter
public enum PrestigeEnum {
    AFKBUTONLINE("AFK playing", Material.EMERALD, 7, 1000, 0.01D, "%", "Have partial income while being online but with FoodClicker closed."),

    AFKBUTONLINEDURATION("AFK playing longer", Material.EMERALD, 7, 1440, 60D, "s", "Extend the duration of the 'AFK playing' income."),

    AFKANDOFFLINE("AFK offline", Material.EMERALD, 7, 1000, 0.01D, "%", "Have partial income while being offline."),

    AFKANDOFFLINEDURATION("AFK offline longer", Material.EMERALD, 7, 1440, 60D, "s", "Extend the duration of the 'AFK offline' income."),

    PERMANENTINCOMEMULTIPLIER("Permanent Multiplier", Material.GOLD_INGOT, 3.77, 25, 2.5D, "%", "Get a permanent multiplier on your income per second."),

    MULTIPLIERPERUNLOCKEDACHIEVEMENT("Achieved", Material.REDSTONE_LAMP, 53.72, 5, 0.5D, "%", "Get a permanent multiplier for every unlocked achievement."),

    EVENTCHANCELIMIT("Pushing limits", Material.GOLDEN_CARROT, 77, 40, 0.5D, "%",
                     "Permanently increase the Event Chance limit."), // base limit = 30%, 40*0.5 + baselimit = 50% total limit
    STARTWITHEVENCHANCE("Cheating the system", Material.GOLDEN_CARROT, 66, 60, 0.5D, "%",
                        "Start each legacy with an increased base event chance."), // max baselimit 30%
    EVENTTIME("Relaxed", Material.CARROT, 1.55, 100, 2, "s",
              "Permanently increase the timer several seconds when events spawn."), // increase howlong an event stays
    EVENTDURATION("Chilling", Material.CARROT, 7, 100, 7, "%", "Permanently increase the duration of active events."), // increase event durations

    DISCOUNT("Members card", Material.GOLD_INGOT, 5, 15, 4.44, "%", "Permanently reduce the cost of buildings."), // buildings are cheaper
    CHEAP("Members account", Material.GOLD_INGOT, 5, 15, 4.44, "%", "Permanently reduce the cost of upgrades."), // upgrades are cheaper

    FAIRTRADE("FairTrade", Material.EGG, 7, 50, 1D, "%", "Give in to ethics and stop child labour and other malpractices in your companies!",
              "Every building will be more expensive, but will produce directly proportionally more."), // buildings more expensive, but produce more

    STONKS("Stonks ltd", Material.GOLD_BLOCK, 50, 20, 2, "%", "Make the stocks event appear up to a higher limit.",
           "On default, stock events do not appear if your current bank value is over 65% of the total earnings of the current legacy.",
           "This upgrade adds a flat amount to that limit."),

    PERMANENTCLICKMULTIPLIER("Click and Morty", Material.GOLDEN_APPLE, 2, 1000, 9.87, "%", "Permanently boost your earnings per click."),

    PRESTIGESTATS("Prestige Head", Material.SKELETON_SKULL, 5, 1, 0, "", "Unlock the Prestige Statistics!", "Click your statistics to switch between modes."),

    PRESTIGEBRAG("Prestige Brag", Material.WITHER_SKELETON_SKULL, 15, 1, 0, "", "Unlock the Prestige /fcstats option!",
                 "Use the boolean parameter to choose which statistics to showcase."),

    LOCALLEADERBOARD("Leaderboard", Material.BARRIER, 20, 1, 0, "", "Unlock the leaderboard command.", "Use /fctop to see the top 10 richest players."),

    STARTWITHMOMS("Headstart", Material.FLOWER_POT, 2, 200, 1, "x", "Start each legacy with an extra mom."),
    STARTWITHCHEFS("Headsmart", Material.FLOWER_POT, 3, 100, 1, "x", "Start each legacy with an extra chef."),

    ;

    private final static InventoryManager im = InventoryManager.getInstance();
    private final String name;
    private final double baseCost;
    private final int maxAmount; // 1 = unique
    private final String key;
    private final double bonus; // in percent, adds the flat bonus, not recursively AKA owned amount * bonus = current bonus
    private final Material material;
    private final String bonusUnit;
    private final String[] lore;

    PrestigeEnum(String name, Material material, double baseCost, int maxAmount, double bonus, String bonusUnit, String... lore) {
        this.name = name;
        this.baseCost = baseCost;
        this.maxAmount = maxAmount;
        this.key = "Prestige shop " + name;
        this.bonus = bonus;
        this.material = material;
        this.bonusUnit = bonusUnit;
        this.lore = lore;
    }

    public int getAmountOwned(ClickingPlayer cp) {
        return cp.getData()
                 .getPrestigeShopBonusses()
                 .get(this);
    }

    public double getCurrentBonus(ClickingPlayer cp) {
        return getBonus() * getAmountOwned(cp);
    }

    public int getCost(ClickingPlayer cp) {
        return (int) (getBaseCost() * (getAmountOwned(cp) + 1));
    }

    private boolean canAfford(ClickingPlayer cp) {
        return cp.getData()
                 .getCurrentPrestigeCoins() >= getCost(cp);
    }

    private void buyUpgrade(ClickingPlayer cp) {
        cp.doSoundEffect(2);
        cp.doFireWorks(1, 0);
        cp.doFireWorks(2, 1);
        cp.doFireWorks(3, 2);

        cp.doMessage("You bought &b" + getName() + "&e for only &a" + im.truncateNumber(getCost(cp), cp) + " Prestige Coins&e!");

        cp.getData()
          .payPrestigeCoins(getCost(cp));
        cp.getData()
          .getPrestigeShopBonusses()
          .put(this, cp.getData()
                       .getPrestigeShopBonusses()
                       .get(this) + 1);
    }

    public void possiblyBuyPrestigeUpgrade(ClickingPlayer cp) {
        if (getAmountOwned(cp) < getMaxAmount()) {
            if (canAfford(cp)) {
                buyUpgrade(cp);

                cp.setIncomePerSecond(cp.calculateTotalIncomePerSecond());
                cp.getGui()
                  .setGameState(cp.getGui()
                                  .getGameState()); // refresh the window to view changes

            } else {
                cp.doMessage("You need &c" + (getCost(cp) - cp.getData()
                                                              .getCurrentPrestigeCoins()) + "&e more Prestige Coins for this upgrade.");
                cp.doSoundEffect(0);
            }
        } else {
            cp.doMessage("&aYou've already reached the maximum amount for this upgrade!");
            cp.doSoundEffect(0);
        }
    }

    public ItemStack createItem(ClickingPlayer cp) {

        String lore1 = cp.getSettings()
                         .getPrimaryLoreColor(); // blue
        String lore2 = cp.getSettings()
                         .getSecondaryLoreColor(); // green
        String lore3 = cp.getSettings()
                         .getTextLoreColor(); // white or grey

        return new ItemBuilder(getMaterial()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                                             .setDisplayName(lore1 + getName())
                                             .addLore(im.makeLore30CharsPerLine(String.join(" ", getLore()), "&e"))
                                             .addLore(getAmountOwned(cp) == getMaxAmount() ? (lore3 + "• Amount owned: " + lore2 + im.makeNumbersPretty(
                                                     getAmountOwned(cp)) + "/" + getMaxAmount() + lore3 + ".")
                                                                                           : (lore3 + "• " + "Amount owned: " + lore1 + im.makeNumbersPretty(
                                                                                                   getAmountOwned(cp)) + lore3 + "/" + getMaxAmount() + "."))
                                             .addLore(lore3 + "Your current bonus: " + lore2 + im.truncateNumber(getCurrentBonus(cp), cp) + getBonusUnit())
                                             .addLore(lore3 + "Bonus for each: " + lore2 + im.truncateNumber(getBonus(), cp) + getBonusUnit())
                                             .addLore(getAmountOwned(cp) == getMaxAmount() ? "" : "&6Price: &c" + im.truncateNumber(getCost(cp), cp)
                                                     + " Prestige Coins")
                                             .setGlowing(getAmountOwned(cp) == getMaxAmount())
                                             .create();
    }
}
