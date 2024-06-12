package io.github.stealingdapenta.foodclicker.upgrades;

import io.github.stealingdapenta.foodclicker.basics.Buildings;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayerData;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import org.bukkit.Material;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

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
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.EVENTDURATION;

public enum UpgradesBasedOnEvents {

    MOMBONUS_1(MOM, 20, 120),
    MOMBONUS_2(MOM, 30, 30),

    CHEFBONUS_1(CHEF, 9, 120),
    CHEFBONUS_2(CHEF, 18, 30),

    CAFETERIABONUS_1(CAFETERIA, 8, 120),
    CAFETERIABONUS_2(CAFETERIA, 16, 30),

    DELIVERYBONUS_1(DELIVERY, 7, 120),
    DELIVERYBONUS_2(DELIVERY, 14, 30),

    DRIVETHROUGHBONUS_1(DRIVETHROUGH, 6, 120),
    DRIVETHROUGHBONUS_2(DRIVETHROUGH, 12, 30),

    FOODTRUCKBONUS_1(FOODTRUCK, 5, 120),
    FOODTRUCKBONUS_2(FOODTRUCK, 10, 30),

    POPUPBONUS_1(POPUP, 4, 120),
    POPUPBONUS_2(POPUP, 8, 30),

    RESTAURANTBONUS_1(RESTAURANT, 3, 120),
    RESTAURANTBONUS_2(RESTAURANT, 6, 30),

    HOTELBONUS_1(HOTEL, 2, 120),
    HOTELBONUS_2(HOTEL, 4, 30),

    CHAINBONUS_1(CHAIN, 1.5, 120),
    CHAINBONUS_2(CHAIN, 3, 30),

    COMPANYBONUS_1(COMPANY, 1.25, 120),
    COMPANYBONUS_2(COMPANY, 1.5, 30),

    HOUSEHOLDNAMEBONUS_1(HOUSEHOLDNAME, 1.10, 120),
    HOUSEHOLDNAMEBONUS_2(HOUSEHOLDNAME, 1.2, 30),

    CLICKBONUS_1("Click bonus", Material.APPLE, "Click Multiplier", 7, 77),
    CLICKBONUS_2("Click bonus", Material.GOLDEN_APPLE, "Click Multiplier", 77, 27),
    CLICKBONUS_3("Click bonus", Material.ENCHANTED_GOLDEN_APPLE, "Click Multiplier", 777, 7),

    EVENTBONUS_0("Event Boost", Material.CARROT, "eventChance", 1.05),
    EVENTBONUS_0B("Event Boost", Material.CARROT, "eventChance", 1.07),
    EVENTBONUS_0C("Event Boost", Material.CARROT, "eventChance", 1.09),
    EVENTBONUS_1("Event Boost", Material.CARROT, "eventChance", 1.12),
    EVENTBONUS_2("Event Boost", Material.GOLDEN_CARROT, "eventChance", 1.25),

    COOKIES_1("Free Money", Material.GOLD_NUGGET, "money", 1.11),
    COOKIES_2("Free Money", Material.GOLD_INGOT, "money", 1.25),
    COOKIES_3("Free Money", Material.GOLD_BLOCK, "money", 1.40),

    ROTTEN_1("Rotten Kitchen", Material.ROTTEN_FLESH, RESTAURANT, 0.75, 15),
    ROTTEN_2("Rotten Fridge", Material.ROTTEN_FLESH, FOODTRUCK, 0.75, 19),
    ROTTEN_3("Rotten Attitude", Material.ROTTEN_FLESH, CHEF, 0.75, 17),

    THIEF_1("Thief", Material.SKELETON_SKULL, "money", 0.82),
    THIEF_2("Thief", Material.SKELETON_SKULL, "money", 0.666),

    PARALYSE("Cold Fingers", Material.APPLE, "Click Multiplier", 0.1, 14),
    ;

    private final double multiplierIncrease;
    private final String affectingKey;
    private final int boostDuration;
    private final Buildings buildings;
    private final String name;
    private final Material material;
    private final Random r = new Random();

    UpgradesBasedOnEvents(String name, Material material, String affectingKey, double multiplierIncrease) {
        this.multiplierIncrease = multiplierIncrease;
        this.affectingKey = affectingKey;
        this.buildings = null;
        this.name = name;
        this.material = material;
        this.boostDuration = 0;
    }

    UpgradesBasedOnEvents(String name, Material material, String affectingKey, double multiplierIncrease, int boostDuration) {
        this.multiplierIncrease = multiplierIncrease;
        this.affectingKey = affectingKey;
        this.boostDuration = boostDuration + r.nextInt(15);
        this.buildings = null;
        this.name = name;
        this.material = material;
    }

    UpgradesBasedOnEvents(Buildings buildings, double multiplierIncrease, int boostDuration) {
        this.multiplierIncrease = multiplierIncrease;
        this.affectingKey = buildings.getName() + "BaseMultiplier";
        this.boostDuration = boostDuration + r.nextInt(15);
        this.buildings = buildings;
        this.name = buildings.getName() + " bonus";
        this.material = buildings.getMaterial();
    }

    UpgradesBasedOnEvents(String name, Material material, Buildings buildings, double multiplierIncrease, int boostDuration) {
        this.multiplierIncrease = multiplierIncrease;
        this.affectingKey = buildings.getName() + "BaseMultiplier";
        this.boostDuration = boostDuration + r.nextInt(15);
        this.buildings = buildings;
        this.name = name;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public Buildings getBuildings() {
        return buildings;
    }

    public int getBoostDuration(ClickingPlayer cp) {
        return boostDuration * (1 + (int) EVENTDURATION.getCurrentBonus(cp));
    }

    public String getAffectingKey() {
        return affectingKey;
    }

    public double getMultiplierIncrease() {
        return multiplierIncrease;
    }

    public void doEffects(ClickingPlayer cp) {
        ClickingPlayerData cpd = cp.getData();
        InventoryManager im = InventoryManager.getInstance();

        if (getMultiplierIncrease() > 1) {
            cp.doSoundEffect(3);
        } else {
            cp.doSoundEffect(-2);
        }
        Buildings b = getBuildings();
        if (b != null) {
            cpd.getBuildingsBaseMultipliers().put(b, cpd.getSpecificBuildingsBaseMultiplier(b) * getMultiplierIncrease());

            if (getMultiplierIncrease() > 1) {
                cp.doMessage("Your &b" + b.getName() + "s &eare boosted by &b" + getMultiplierIncrease() + "x &efor &b" + getBoostDuration(cp) + " &eseconds!");
            } else {
                cp.doMessage("&cA disgrace! Your &b" + b.getName() + "s &care slowed down by &b" + im.makeNumbersPretty((1 - getMultiplierIncrease())) + "% &cfor &b" + getBoostDuration(cp) + " " +
                                     "&cseconds!");
            }
        } else {
            if (cpd.getGeneralDoubleStats().containsKey(getAffectingKey())) {
                cpd.getGeneralDoubleStats().put(getAffectingKey(), cpd.getGeneralDoubleStats().get(getAffectingKey()) * getMultiplierIncrease());
            } else if (cpd.getGeneralBigDecimals().containsKey(getAffectingKey())) {
                BigDecimal newValue = cpd.getGeneralBigDecimals().get(getAffectingKey()).multiply(BigDecimal.valueOf(getMultiplierIncrease()));
                if (getAffectingKey().equals("money") && getMultiplierIncrease() > 1) {
                    BigDecimal increment = newValue.subtract(cpd.getGeneralBigDecimals().get(getAffectingKey()));
                    cp.earn(increment);
                } else {
                    cpd.getGeneralBigDecimals().put(getAffectingKey(), newValue);
                }
            } else {
                System.out.println("FoodClicker: Error in Upgrades Based On Events!");
            }

            if (getBoostDuration(cp) == 0) {
                if (getAffectingKey().equals("money")) {
                    if (getMultiplierIncrease() > 1) {
                        cp.doMessage("Stocks went up! Your current &bbankvalue &egot multiplied by &b" + getMultiplierIncrease() + "x&e!");
                    } else {
                        cp.doMessage("&cA burglary! Your &bbankvalue &cdeclined by &b" + im.makeNumbersPretty((1 - getMultiplierIncrease())) + "%&c!");
                    }
                } else {
                    cp.doMessage("Your &b" + getAffectingKey() + " &eis permanently boosted by &b" + getMultiplierIncrease() + "x&e!");
                }
            } else {
                if (getMultiplierIncrease() > 1) {
                    cp.doMessage("Your &b" + getAffectingKey() + " &eis boosted by &b" + getMultiplierIncrease() + "x &efor &b" + getBoostDuration(cp) + " &eseconds!");
                } else {
                    cp.doMessage("&cA disgrace! Your &b" + getAffectingKey() + " &cis slowed down by &b" + im.makeNumbersPretty((1 - getMultiplierIncrease())) + "% &cfor &b" + getBoostDuration(cp) + " &cseconds!");
                }
            }
        }
        cp.setIncomePerSecond(cp.calculateTotalIncomePerSecond());
    }

    public void undoEffects(ClickingPlayer cp) {
        Buildings b = getBuildings();
        if (b != null) {
            cp.getData().getBuildingsBaseMultipliers().put(b, cp.getData().getSpecificBuildingsBaseMultiplier(b) / getMultiplierIncrease());
        } else {
            if (cp.getData().getGeneralDoubleStats().containsKey(getAffectingKey())) {
                cp.getData().getGeneralDoubleStats().put(getAffectingKey(), cp.getData().getGeneralDoubleStats().get(getAffectingKey()) / getMultiplierIncrease());
            } else if (cp.getData().getGeneralBigDecimals().containsKey(getAffectingKey())) {
                BigDecimal newValue = cp.getData().getGeneralBigDecimals().get(getAffectingKey())
                                        .divide(BigDecimal.valueOf(getMultiplierIncrease()), new MathContext(5));
                cp.getData().getGeneralBigDecimals().put(getAffectingKey(), newValue);
            } else {
                System.out.println("FoodClicker: Error in Upgrades Based On Events!");
            }
        }
        cp.setIncomePerSecond(cp.calculateTotalIncomePerSecond());
    }
}
