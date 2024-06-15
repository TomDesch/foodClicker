package io.github.stealingdapenta.foodclicker.upgrades;

import static io.github.stealingdapenta.foodclicker.basics.Buildings.CAFETERIA;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.CHAIN;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.CHEF;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.COMPANY;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.DELIVERY;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.DRIVETHROUGH;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.FOODTRUCK;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.HOTEL;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.HOUSEHOLDNAME;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.MOM;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.POPUP;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.RESTAURANT;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.CHEAP;

import io.github.stealingdapenta.foodclicker.basics.Buildings;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum UpgradesBasedOnBuildings {

    MOM_0("Home", MOM, 1.07, 5),
    MOM_1("Home", MOM, 1.05, 10),
    MOM_2("Home", MOM, 1.10, 20),
    MOM_2B("Home", MOM, 1.15, 25),
    MOM_3("Home", MOM, 1.15, 50),
    MOM_4("Home", MOM, 1.20, 100),
    MOM_5("Home", MOM, 1.25, 200),
    MOM_6("Home", MOM, 1.30, 300),
    MOM_7("Home", MOM, 1.35, 400),
    MOM_8("Home", MOM, 1.40, 500),
    MOM_9("Home", MOM, 1.50, 750),
    MOM_10("Home", MOM, 2.00, 1000),

    CHEF_0("New menu", CHEF, 1.08, 5),
    CHEF_1("New menu", CHEF, 1.05, 10),
    CHEF_2("New menu", CHEF, 1.10, 20),
    CHEF_2B("New menu", CHEF, 1.15, 25),
    CHEF_3("New menu", CHEF, 1.15, 50),
    CHEF_4("New menu", CHEF, 1.20, 100),
    CHEF_5("New menu", CHEF, 1.25, 200),
    CHEF_6("New menu", CHEF, 1.30, 300),
    CHEF_7("New menu", CHEF, 1.35, 400),
    CHEF_8("New menu", CHEF, 1.40, 500),
    CHEF_9("New menu", CHEF, 1.50, 750),
    CHEF_10("New menu", CHEF, 2, 1000),

    CAFETERIA_0("Increased commission", CAFETERIA, 1.09, 5),
    CAFETERIA_1("Increased commission", CAFETERIA, 1.05, 10),
    CAFETERIA_2("Increased commission", CAFETERIA, 1.10, 20),
    CAFETERIA_3("Increased commission", CAFETERIA, 1.15, 50),
    CAFETERIA_4("Increased commission", CAFETERIA, 1.20, 100),
    CAFETERIA_5("Increased commission", CAFETERIA, 1.25, 200),
    CAFETERIA_6("Increased commission", CAFETERIA, 1.30, 300),
    CAFETERIA_7("Increased commission", CAFETERIA, 1.35, 400),
    CAFETERIA_8("Increased commission", CAFETERIA, 1.40, 500),
    CAFETERIA_9("Increased commission", CAFETERIA, 1.50, 750),
    CAFETERIA_10("Increased commission", CAFETERIA, 2, 1000),

    DELIVERY_1("Faster delivery", DELIVERY, 1.05, 10),
    DELIVERY_2("Faster delivery", DELIVERY, 1.05, 20),
    DELIVERY_3("Faster delivery", DELIVERY, 1.05, 50),
    DELIVERY_4("Faster delivery", DELIVERY, 1.05, 100),
    DELIVERY_5("Faster delivery", DELIVERY, 1.05, 200),
    DELIVERY_6("Faster delivery", DELIVERY, 1.05, 300),
    DELIVERY_7("Faster delivery", DELIVERY, 1.05, 400),
    DELIVERY_8("Faster delivery", DELIVERY, 1.05, 500),
    DELIVERY_9("Faster delivery", DELIVERY, 1.10, 750),
    DELIVERY_10("Faster delivery", DELIVERY, 1.20, 1000),

    DRIVETHROUGH_1("Extra window", DRIVETHROUGH, 1.05, 10),
    DRIVETHROUGH_2("Extra window", DRIVETHROUGH, 1.10, 20),
    DRIVETHROUGH_3("Extra window", DRIVETHROUGH, 1.15, 50),
    DRIVETHROUGH_4("Extra window", DRIVETHROUGH, 1.20, 100),
    DRIVETHROUGH_5("Extra window", DRIVETHROUGH, 1.25, 200),
    DRIVETHROUGH_6("Extra window", DRIVETHROUGH, 1.30, 300),
    DRIVETHROUGH_7("Extra window", DRIVETHROUGH, 1.40, 400),
    DRIVETHROUGH_8("Extra window", DRIVETHROUGH, 1.50, 500),
    DRIVETHROUGH_9("Extra window", DRIVETHROUGH, 1.60, 750),
    DRIVETHROUGH_10("Extra window", DRIVETHROUGH, 2, 1000),

    FOODTRUCK_1("Trailer park", FOODTRUCK, 1.05, 10),
    FOODTRUCK_2("Trailer park", FOODTRUCK, 1.15, 20),
    FOODTRUCK_3("Trailer park", FOODTRUCK, 1.25, 50),
    FOODTRUCK_4("Trailer park", FOODTRUCK, 1.35, 100),
    FOODTRUCK_5("Trailer park", FOODTRUCK, 1.45, 200),
    FOODTRUCK_6("Trailer park", FOODTRUCK, 1.55, 300),
    FOODTRUCK_7("Trailer park", FOODTRUCK, 1.65, 400),
    FOODTRUCK_8("Trailer park", FOODTRUCK, 1.75, 500),
    FOODTRUCK_9("Trailer park", FOODTRUCK, 1.85, 750),
    FOODTRUCK_10("Trailer park", FOODTRUCK, 2, 1000),

    POPUP_1("Another one", POPUP, 1.05, 10),
    POPUP_2("Another one", POPUP, 1.15, 20),
    POPUP_3("Another one", POPUP, 1.25, 50),
    POPUP_4("Another one", POPUP, 1.35, 100),
    POPUP_5("Another one", POPUP, 1.45, 200),
    POPUP_6("Another one", POPUP, 1.55, 300),
    POPUP_7("Another one", POPUP, 1.65, 400),
    POPUP_8("Another one", POPUP, 1.75, 500),
    POPUP_9("Another one", POPUP, 1.85, 750),
    POPUP_10("Another one", POPUP, 2, 1000),

    RESTAURANT_1("Michelin Star", RESTAURANT, 1.05, 10),
    RESTAURANT_2("Michelin Star", RESTAURANT, 1.15, 20),
    RESTAURANT_3("Michelin Star", RESTAURANT, 1.25, 50),
    RESTAURANT_4("Michelin Star", RESTAURANT, 1.35, 100),
    RESTAURANT_5("Michelin Star", RESTAURANT, 1.55, 200),
    RESTAURANT_6("Michelin Star", RESTAURANT, 1.65, 300),
    RESTAURANT_7("Michelin Star", RESTAURANT, 1.75, 400),
    RESTAURANT_8("Michelin Star", RESTAURANT, 1.85, 500),
    RESTAURANT_9("Michelin Star", RESTAURANT, 1.95, 750),
    RESTAURANT_10("Michelin Star", RESTAURANT, 2, 1000),

    HOTEL_1("Room service", HOTEL, 1.05, 10),
    HOTEL_2("Room service", HOTEL, 1.15, 20),
    HOTEL_3("Room service", HOTEL, 1.25, 50),
    HOTEL_4("Room service", HOTEL, 1.45, 100),
    HOTEL_5("Room service", HOTEL, 1.55, 200),
    HOTEL_6("Room service", HOTEL, 1.65, 300),
    HOTEL_7("Room service", HOTEL, 1.75, 400),
    HOTEL_8("Room service", HOTEL, 1.85, 500),
    HOTEL_9("Room service", HOTEL, 1.95, 750),
    HOTEL_10("Room service", HOTEL, 2, 1000),

    CHAIN_1("Unchained", CHAIN, 1.05, 10),
    CHAIN_2("Unchained", CHAIN, 1.15, 20),
    CHAIN_3("Unchained", CHAIN, 1.35, 50),
    CHAIN_4("Unchained", CHAIN, 1.45, 100),
    CHAIN_5("Unchained", CHAIN, 1.55, 200),
    CHAIN_6("Unchained", CHAIN, 1.65, 300),
    CHAIN_7("Unchained", CHAIN, 1.75, 400),
    CHAIN_8("Unchained", CHAIN, 1.85, 500),
    CHAIN_9("Unchained", CHAIN, 1.95, 750),
    CHAIN_10("Unchained", CHAIN, 2, 1000),

    COMPANY_1("Sponsorship", COMPANY, 1.05, 10),
    COMPANY_2("Sponsorship", COMPANY, 1.25, 20),
    COMPANY_3("Sponsorship", COMPANY, 1.35, 50),
    COMPANY_4("Sponsorship", COMPANY, 1.45, 100),
    COMPANY_5("Sponsorship", COMPANY, 1.55, 200),
    COMPANY_6("Sponsorship", COMPANY, 1.65, 300),
    COMPANY_7("Sponsorship", COMPANY, 1.75, 400),
    COMPANY_8("Sponsorship", COMPANY, 1.85, 500),
    COMPANY_9("Sponsorship", COMPANY, 1.95, 750),
    COMPANY_10("Sponsorship", COMPANY, 2, 1000),

    HOUSEHOLDNAME_1("Stock market", HOUSEHOLDNAME, 1.15, 10),
    HOUSEHOLDNAME_2("Stock market", HOUSEHOLDNAME, 1.25, 20),
    HOUSEHOLDNAME_3("Stock market", HOUSEHOLDNAME, 1.35, 50),
    HOUSEHOLDNAME_4("Stock market", HOUSEHOLDNAME, 1.45, 100),
    HOUSEHOLDNAME_5("Stock market", HOUSEHOLDNAME, 1.55, 200),
    HOUSEHOLDNAME_6("Stock market", HOUSEHOLDNAME, 1.65, 300),
    HOUSEHOLDNAME_7("Stock market", HOUSEHOLDNAME, 1.75, 400),
    HOUSEHOLDNAME_8("Stock market", HOUSEHOLDNAME, 1.85, 500),
    HOUSEHOLDNAME_9("Stock market", HOUSEHOLDNAME, 1.95, 750),
    HOUSEHOLDNAME_10("Stock market", HOUSEHOLDNAME, 2, 1000);

    @Getter
    private final String name;
    @Getter
    private final String requiredKey;
    @Getter
    private final int requiredValue;
    @Getter
    private final List<String> lore;
    private final BigDecimal cost;
    @Getter
    private final double multiplierIncrease;
    @Getter
    private final String unlockedKey;
    private final InventoryManager im = InventoryManager.getInstance();
    @Getter
    private final Buildings b;
    @Getter
    private final Material material;


    UpgradesBasedOnBuildings(String name, Buildings b, double multiplierIncrease, int requiredValue, String... lore) {
        this.name = name;
        this.requiredKey = b.getName();
        this.requiredValue = requiredValue;
        this.lore = Arrays.asList(lore);
        this.b = b;
        this.cost = BigDecimal.valueOf(b.getBaseCost() * Math.pow(1.15, (requiredValue + 3)));
        this.material = b.getMaterial();
        this.multiplierIncrease = multiplierIncrease;
        this.unlockedKey = getRequiredKey() + getRequiredValue();
    }

    public BigDecimal getCost(ClickingPlayer cp) {
        return cost.subtract(cost.multiply(BigDecimal.valueOf(CHEAP.getCurrentBonus(cp) / 100)));
    }

    private boolean requiredValueIsMet(ClickingPlayer cp) {
        return cp.getData()
                 .getSpecificBuildingAmount(getB()) >= getRequiredValue();
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
        cp.getData()
          .getBuildingsBaseMultipliers()
          .put(getB(), cp.getData()
                         .getSpecificBuildingsBaseMultiplier(getB()) * getMultiplierIncrease());
    }

    public void possiblyBuyUpgrade(ClickingPlayer cp, Inventory inv, int slot) {
        if (cp.canAfford(getCost(cp))) {
            cp.doSoundEffect(2);
            cp.doFireWorks(2, 0);
            cp.doFireWorks(2, 1);

            cp.pay(getCost(cp));
            setUpgradeUnlocked(cp);
            getEffectsFromUnlocking(cp);

            // remove item from shop
            inv.setItem(slot, new ItemStack(Material.AIR));
            cp.updateUpgradesWindow(inv);
            cp.setIncomePerSecond(cp.calculateTotalIncomePerSecond());
            cp.doMessage("You bought &b" + getName() + "&e for only &a£" + im.makeNumbersPretty(getCost(cp)) + "&e!");
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
                                             .addLore(getLore())
                                             .addLore(lore2 + "• " + b.getName() + "s are " + lore1 + im.makeNumbersPretty((getMultiplierIncrease() - 1) * 100)
                                                              + "%" + lore2 + " more effective.")
                                             .addLore("&6Price: &c£" + im.truncateNumber(getCost(cp), cp))
                                             .setGlowing(true)
                                             .create();
    }

}
