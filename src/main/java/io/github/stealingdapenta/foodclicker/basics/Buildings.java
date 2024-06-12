package io.github.stealingdapenta.foodclicker.basics;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.DISCOUNT;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.FAIRTRADE;

public enum Buildings {

    MOM("Mom", "Home cooked meals are the best after all!", 100, 0.25, Material.LILAC, 10),
    CHEF("Chef", "The finest chef in town.", 1000, 1, Material.FURNACE, 11),
    CAFETERIA("Cafeteria", "A food service location in which there is little or no waiting staff table service.", 10_000D, 8, Material.HONEY_BOTTLE, 12),
    DELIVERY("Delivery", "Transporting foods from your kitchen to the customers.", 59_000D, 47D, Material.RAIL, 19),
    DRIVETHROUGH("Drive Through", "A take-out service that allows customers to purchase food without leaving their cars.", 880_088D, 264.5D, Material.POWERED_RAIL, 20),
    FOODTRUCK("Food truck", "A large trailer equipped to cook, prepare, serve, and sell food.", 15_000_000D, 577D, Material.FURNACE_MINECART, 21),
    POPUP("Pop Up", "Opening short-term sales spaces that last for days to weeks before closing down.", 280_000_000D, 1_300D, Material.BEACON, 28),
    RESTAURANT("Restaurant", "A quality business that prepares and serves food and drinks to customers.", 1_000_000_000D, 9_876D, Material.ENCHANTING_TABLE, 29),
    HOTEL("Hotel", "An establishment that provides paid lodging on a short-term basis.", 37_000_000_000D, 105_500D, Material.RED_BED, 30),
    CHAIN("Chain", "A set of related restaurants in many different locations that are under shared corporate ownership.", 737_000_000_000D, 773_524D, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 37),
    COMPANY("Company", "A legal entity representing an association of people.", 9_000_000_000_000D, 1_235_092D, Material.BLUE_GLAZED_TERRACOTTA, 38),
    HOUSEHOLDNAME("Household Name", "A brand name that is well known to the majority of people.", 150_000_000_000_000D, 123_321_123D, Material.DIAMOND_BLOCK, 39);

    //Bank (Miners), Lab (professors), University (Doctors), Nether Portal (Gold farm idk), End portal (end farm idk), +7;


    private final String name;
    private final String lore;
    private final double baseCost;
    private final double baseProduction;
    private final Material material;
    private final int guiSlot;
    private final InventoryManager im = InventoryManager.getInstance();

    Buildings(String name, String lore, double baseCost, double baseProduction, Material material, int guiSlot) {
        this.name = name;
        this.lore = lore;
        this.baseCost = baseCost;
        this.baseProduction = baseProduction;
        this.material = material;
        this.guiSlot = guiSlot;
    }

    public int getGuiSlot() {
        return guiSlot;
    }

    public String getName() {
        return name;
    }

    public String getLore() {
        return lore;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public double getBaseProduction() {
        return baseProduction;
    }

    public ItemStack createItem(ClickingPlayer cp) {
        String lore2 = cp.getSettings().getSecondaryLoreColor(); // green
        String lore3 = cp.getSettings().getTextLoreColor(); // white or gray

        return new ItemBuilder(getMaterial()).setDisplayName("&3" + getName())
                                             .addLore(im.makeLore30CharsPerLine(getLore(), lore3))
                                             .addLore("&6Price: &c£" + im.truncateNumber(calculateCost(cp), cp))
                                             .addLore("&6Currently producing: " + lore2 + "£" + im.truncateNumber(calculateIncome(cp), cp) + lore3 + "/s.")
                                             .addLore("&6Amount owned: " + lore3 + cp.getData().getSpecificBuildingAmount(this)).create();
    }

    public Material getMaterial() {
        return material;
    }

    public double calculateIncome(ClickingPlayer cp) {
        double income =
                ((getBaseProduction() + cp.getData().getSpecificBuildingsBaseIncreaser(this)) * cp.getData().getSpecificBuildingsBaseMultiplier(this) * cp.getData().getSpecificBuildingAmount(this));
        return income * (1 + FAIRTRADE.getCurrentBonus(cp));
    }

    public double calculateCost(ClickingPlayer cp) {
        double cost = getBaseCost() * cp.getData().getSpecificBuildingsCostMultiplier(this) * (Math.pow(1.15, cp.getData().getSpecificBuildingAmount(this)));
        return cost - (cost * DISCOUNT.getCurrentBonus(cp) / 100) + (cost * FAIRTRADE.getCurrentBonus(cp));
    }
}
