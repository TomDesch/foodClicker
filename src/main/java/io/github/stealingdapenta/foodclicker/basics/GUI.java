package io.github.stealingdapenta.foodclicker.basics;

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
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.AFKANDOFFLINE;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.AFKANDOFFLINEDURATION;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.AFKBUTONLINE;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.AFKBUTONLINEDURATION;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.CHEAP;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.DISCOUNT;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.EVENTCHANCELIMIT;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.EVENTDURATION;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.EVENTTIME;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.FAIRTRADE;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.LOCALLEADERBOARD;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.MULTIPLIERPERUNLOCKEDACHIEVEMENT;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PERMANENTCLICKMULTIPLIER;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PERMANENTINCOMEMULTIPLIER;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PRESTIGEBRAG;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PRESTIGESTATS;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHCHEFS;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHEVENCHANCE;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHMOMS;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STONKS;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.ACHIEVEMENTS_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.GAME_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.PREFERENCES_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.PRESTIGE_SHOP;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.SHOP_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.UPGRADES_GUI_TITLE_ADDENDUM;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayerData;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayerSettings;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.util.ArrayList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GUI {

    private final InventoryManager im = InventoryManager.getInstance();
    @Getter
    private final ClickingPlayer cp;
    @Getter
    private int gameState;
    private String GUItitle;
    private Inventory inventory;
    @Getter
    private int achievementsPageNumber;

    public GUI(ClickingPlayer cp) {
        this.cp = cp;
        this.inventory = createGameGUI();
        this.gameState = 0;
        this.achievementsPageNumber = 1;
    }

    public void setAchievementsPageNumber(int achievementsPageNumber) {
        this.achievementsPageNumber = achievementsPageNumber;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
        Inventory gameWindow = null;

        switch (gameState) {
            case 0: // gameGUI
                gameWindow = createGameGUI();
                break;
            case 1: // shop gui
                gameWindow = createShopGUI();
                break;
            case 2: // achievements gui
                gameWindow = createAchievementsGUI(getAchievementsPageNumber());
                break;
            case 3: // upgrades GUI
                gameWindow = createUpgradesGUI();
                getCp().updateUpgradesWindow(gameWindow);
                break;
            case 4: // settings gui / preferences
                gameWindow = createPreferencesGUI();
                break;
            case 5: // prestige shop
                gameWindow = createPrestigeShopGUI();
                break;
            default: // wtf ur not supposed to come here
                break;
        }
        this.inventory = gameWindow;
        im.openGUI(gameWindow, getCp().getPlayer());
    }

    public Inventory getGameWindow() {
        return this.inventory;
    }

    private String getGUItitle() {
        return this.GUItitle;
    }

    private void setGUItitle(String addendum) {
        this.GUItitle = getCp().getPlayer()
                               .getDisplayName() + addendum;
    }

    private Inventory createGameGUI() {
        Material pane1 = cp.getSettings()
                           .getPrimaryPaneColor();
        Material pane2 = cp.getSettings()
                           .getSecondaryPaneColor();
        Material border = cp.getSettings()
                            .getBorderPaneColor();
        String lore1 = cp.getSettings()
                         .getPrimaryLoreColor(); // blue

        setGUItitle(GAME_GUI_TITLE_ADDENDUM);
        Inventory GUI = Bukkit.createInventory(cp.getPlayer(), 54, getGUItitle());

        im.setInventoryBorder(GUI, border);

        //clickable
        GUI.setItem(20, new ItemBuilder(Material.APPLE)
                .setDisplayName("&6Cookie")
                .addLore(lore1 + "Click on me!")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .create());

        //Profile button
        GUI.setItem(16, im.getPlayerSkullItem(cp.getPlayer()));

        //Settings button
        GUI.setItem(25, new ItemBuilder(Material.GLOBE_BANNER_PATTERN)
                .setDisplayName("&3Preferences")
                .addLore("&e► Click to see your settings.")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .addItemFlags(ItemFlag.HIDE_DESTROYS)
                .create());

        //shop button
        GUI.setItem(34, new ItemBuilder(Material.ENDER_CHEST).setDisplayName("&3Shop")
                                                             .addLore("&e► Click to open the shop.")
                                                             .create());

        //Achievements button
        GUI.setItem(43, new ItemBuilder(Material.TOTEM_OF_UNDYING).setDisplayName("&3Achievements")
                                                                  .addLore("&e► Click to see your achievements.")
                                                                  .create());
        im.setInventoryPattern(GUI, true, pane1, pane2);
        return GUI;
    }

    public Inventory createShopGUI() {
        Material pane1 = cp.getSettings()
                           .getPrimaryPaneColor();
        Material pane2 = cp.getSettings()
                           .getSecondaryPaneColor();
        Material border = cp.getSettings()
                            .getBorderPaneColor();

        setGUItitle(SHOP_GUI_TITLE_ADDENDUM);
        Inventory GUI = Bukkit.createInventory(cp.getPlayer(), 54, getGUItitle());

        im.setInventoryBorder(GUI, border);

        //Mom
        GUI.setItem(MOM.getGuiSlot(), MOM.createItem(cp));
        //Chef
        GUI.setItem(CHEF.getGuiSlot(), CHEF.createItem(cp));
        //Cafeteria
        GUI.setItem(CAFETERIA.getGuiSlot(), CAFETERIA.createItem(cp));

        //Delivery
        GUI.setItem(DELIVERY.getGuiSlot(), DELIVERY.createItem(cp));
        //Drivetru
        GUI.setItem(DRIVETHROUGH.getGuiSlot(), DRIVETHROUGH.createItem(cp));
        //foodtruck
        GUI.setItem(FOODTRUCK.getGuiSlot(), FOODTRUCK.createItem(cp));

        //popup
        GUI.setItem(POPUP.getGuiSlot(), POPUP.createItem(cp));
        //resto
        GUI.setItem(RESTAURANT.getGuiSlot(), RESTAURANT.createItem(cp));
        //hotel
        GUI.setItem(HOTEL.getGuiSlot(), HOTEL.createItem(cp));

        //chain
        GUI.setItem(CHAIN.getGuiSlot(), CHAIN.createItem(cp));
        //company
        GUI.setItem(COMPANY.getGuiSlot(), COMPANY.createItem(cp));
        //household name
        GUI.setItem(HOUSEHOLDNAME.getGuiSlot(), HOUSEHOLDNAME.createItem(cp));

        //Profile button
        GUI.setItem(16, im.getPlayerSkullItem(cp.getPlayer()));

        // UpgradesBasedOnStats button
        GUI.setItem(25, new ItemBuilder(Material.BOOKSHELF).setDisplayName("&3Upgrades Shop")
                                                           .addLore("&e► Click to open your upgrades shop.")
                                                           .create());

        //shop button buy mode
        GUI.setItem(34, new ItemBuilder(Material.ENDER_EYE).setDisplayName("&6Buy mode")
                                                           .addLore("&e► Click to change buy mode.")
                                                           .addLore(cp.isBuymodeAll() ? "&a○ Currently active: buy max" : "&a○ Currently active: buy x1")
                                                           .setGlowing(true)
                                                           .create());

        //Return button
        GUI.setItem(43, new ItemBuilder(Material.SPECTRAL_ARROW).setDisplayName("&3Back")
                                                                .addLore("&e► Click to go back.")
                                                                .create());

        im.setInventoryPattern(GUI, false, pane1, pane2);
        return GUI;
    }

    private Inventory createAchievementsGUI(int page) {
        Material pane1 = getCp().getSettings()
                                .getPrimaryPaneColor();
        Material pane2 = getCp().getSettings()
                                .getSecondaryPaneColor();
        Material border = getCp().getSettings()
                                 .getBorderPaneColor();

        ItemStack notAchieved = new ItemBuilder(pane2).setDisplayName("&cNot Achieved.")
                                                      .addLore("&eYou have not unlocked this achievement.")
                                                      .create();

        setGUItitle(ACHIEVEMENTS_GUI_TITLE_ADDENDUM);
        Inventory GUI = Bukkit.createInventory(getCp().getPlayer(), 54, getGUItitle());

        im.setInventoryBorder(GUI, border);

        //Profile button
        GUI.setItem(16, im.getPlayerSkullItem(getCp().getPlayer()));

        //Back to Main Menu button
        GUI.setItem(25, new ItemBuilder(Material.BARRIER).setDisplayName("&3Menu")
                                                         .addLore("&e► Click to return to the main menu.")
                                                         .create());

        GUI.setItem(34, new ItemBuilder(Material.SPECTRAL_ARROW).setDisplayName("&3Next page")
                                                                .addLore("&eCurrently on page: &b" + page)
                                                                .addLore("&e► Click to view the next page.")
                                                                .create());

        GUI.setItem(43, new ItemBuilder(Material.SPECTRAL_ARROW).setDisplayName("&3Previous page")
                                                                .addLore("&e► Click to view the previous page.")
                                                                .create());

        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (AchievementsEnum achievement : AchievementsEnum.values()) {
            if (achievement.unlocked(getCp())) {
                itemStacks.add(achievement.createItem(getCp()));
            } else {
                itemStacks.add(notAchieved);
            }
        }

        switch (page) {
            case 1:
                getCp().doSoundEffect(10);
                fillAchievementsGui(GUI, itemStacks, 0, 20);
                break;
            case 2:
                getCp().doSoundEffect(10);
                fillAchievementsGui(GUI, itemStacks, 20, 40);
                break;
            case 3:
                getCp().doSoundEffect(10);
                fillAchievementsGui(GUI, itemStacks, 40, 60);
                break;
            case 4:
                getCp().doSoundEffect(10);
                fillAchievementsGui(GUI, itemStacks, 60, 80);
                break;
            case 5:
                getCp().doSoundEffect(10);
                fillAchievementsGui(GUI, itemStacks, 80, 100);
                break;
            default:
                break;
        }

        im.setInventoryPattern(GUI, false, pane1, pane2);
        return GUI;
    }

    private Inventory createUpgradesGUI() {
        Material border = getCp().getSettings()
                                 .getBorderPaneColor();
        String lore1 = getCp().getSettings()
                              .getPrimaryLoreColor(); // blue
        String lore2 = getCp().getSettings()
                              .getSecondaryLoreColor(); // green
        String lore3 = getCp().getSettings()
                              .getTextLoreColor(); // white or gray

        setGUItitle(UPGRADES_GUI_TITLE_ADDENDUM);
        Inventory GUI = Bukkit.createInventory(getCp().getPlayer(), 54, getGUItitle());

        im.setInventoryBorder(GUI, border);

        //Profile button
        GUI.setItem(16, im.getPlayerSkullItem(getCp().getPlayer()));

        //Back to Main Menu button
        GUI.setItem(25, new ItemBuilder(Material.BARRIER).setDisplayName("&3Menu")
                                                         .addLore("&e► Click to return to the main menu.")
                                                         .create());

        //Prestige button
        ClickingPlayerData cpd = getCp().getData();
        String earnings =
                lore3 + "Prestige to earn " + lore1 + cpd.calculateAvailablePrestigeLevels() + " Coins " + lore3 + "and " + lore1 + "levels" + lore3 + "!";

        GUI.setItem(34, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&3Prestige Shop")
                                                             .addLore(lore2 + "&e► Click to open!")
                                                             .create());

        //previous page button
        GUI.setItem(43, new ItemBuilder(Material.TIPPED_ARROW)
                .setDisplayName("&3Back")
                .addLore("&e► Click to return to the Shop.")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .create());

        return GUI;
    }

    private Inventory createPreferencesGUI() {
        {
            Material pane1 = getCp().getSettings()
                                    .getPrimaryPaneColor();
            Material pane2 = getCp().getSettings()
                                    .getSecondaryPaneColor();
            Material border = getCp().getSettings()
                                     .getBorderPaneColor();
            String lore1 = getCp().getSettings()
                                  .getPrimaryLoreColor(); // blue
            String lore2 = getCp().getSettings()
                                  .getSecondaryLoreColor(); // green
            String lore3 = getCp().getSettings()
                                  .getTextLoreColor(); // white or gray

            setGUItitle(PREFERENCES_GUI_TITLE_ADDENDUM);
            Inventory GUI = Bukkit.createInventory(getCp().getPlayer(), 54, getGUItitle());

            im.setInventoryBorder(GUI, border);

            //Profile button
            GUI.setItem(16, im.getPlayerSkullItem(getCp().getPlayer()));

            //Back to Main Menu button
            GUI.setItem(25, new ItemBuilder(Material.BARRIER).setDisplayName("&3Menu")
                                                             .addLore("&e► Click to return to the main menu.")
                                                             .create());

            Material enabled = Material.LIME_DYE;
            Material disabled = Material.GRAY_DYE;

            String enabledS = lore2 + "☼ Currently enabled";
            String disabledS = lore3 + "☼ Currently disabled";

            ClickingPlayerSettings cps = getCp().getSettings();

            //Settings
            GUI.setItem(10, new ItemBuilder(cps.isInventoryDarkMode() ? enabled : disabled)
                    .setDisplayName(lore1 + "Interface dark mode")
                    .addLore(cps.isInventoryDarkMode() ? enabledS : disabledS)
                    .addLore(lore3 + "► Click to toggle setting.")
                    .create());
            GUI.setItem(11, new ItemBuilder(cps.isLoreDarkMode() ? enabled : disabled)
                    .setDisplayName(lore1 + "Text dark mode")
                    .addLore(cps.isLoreDarkMode() ? enabledS : disabledS)
                    .addLore(lore3 + "► Click to toggle setting.")
                    .create());
            GUI.setItem(12, new ItemBuilder(cps.isDoChatMessages() ? enabled : disabled)
                    .setDisplayName(lore1 + "Chat messages")
                    .addLore(cps.isDoChatMessages() ? enabledS : disabledS)
                    .addLore(lore3 + "► Click to toggle setting.")
                    .create());
            GUI.setItem(13, new ItemBuilder(cps.isDoSoundEffects() ? enabled : disabled)
                    .setDisplayName(lore1 + "Sound effects")
                    .addLore(cps.isDoSoundEffects() ? enabledS : disabledS)
                    .addLore(lore3 + "► Click to toggle setting.")
                    .create());
            GUI.setItem(14, new ItemBuilder(cps.isDoFirework() ? enabled : disabled)
                    .setDisplayName(lore1 + "Firework effects")
                    .addLore(cps.isDoFirework() ? enabledS : disabledS)
                    .addLore(lore3 + "► Click to toggle setting.")
                    .create());
            GUI.setItem(19, new ItemBuilder(cps.isDoBigNumbers() ? enabled : disabled)
                    .setDisplayName(lore1 + "Big Numbers")
                    .addLore(cps.isDoBigNumbers() ? enabledS : disabledS)
                    .addLore(lore3 + "► Click to toggle setting.")
                    .create());

            im.setInventoryPattern(GUI, false, pane1, pane2);

            return GUI;
        }
    }

    private Inventory createPrestigeShopGUI() {
        Material border = cp.getSettings()
                            .getBorderPaneColor();
        String lore1 = getCp().getSettings()
                              .getPrimaryLoreColor(); // blue
        String lore2 = getCp().getSettings()
                              .getSecondaryLoreColor(); // green
        String lore3 = getCp().getSettings()
                              .getTextLoreColor(); // white or gray

        setGUItitle(PRESTIGE_SHOP);
        Inventory GUI = Bukkit.createInventory(cp.getPlayer(), 54, getGUItitle());

        im.setInventoryBorder(GUI, border);

        GUI.setItem(10, AFKBUTONLINE.createItem(cp));
        GUI.setItem(11, AFKBUTONLINEDURATION.createItem(cp));
        GUI.setItem(12, AFKANDOFFLINE.createItem(cp));
        GUI.setItem(13, AFKANDOFFLINEDURATION.createItem(cp));
        GUI.setItem(14, PERMANENTINCOMEMULTIPLIER.createItem(cp));

        GUI.setItem(19, MULTIPLIERPERUNLOCKEDACHIEVEMENT.createItem(cp));
        GUI.setItem(20, EVENTCHANCELIMIT.createItem(cp));
        GUI.setItem(21, STARTWITHEVENCHANCE.createItem(cp));
        GUI.setItem(22, EVENTTIME.createItem(cp));
        GUI.setItem(23, EVENTDURATION.createItem(cp));

        GUI.setItem(28, DISCOUNT.createItem(cp));
        GUI.setItem(29, CHEAP.createItem(cp));
        GUI.setItem(30, FAIRTRADE.createItem(cp));
        GUI.setItem(31, STONKS.createItem(cp));
        GUI.setItem(32, PERMANENTCLICKMULTIPLIER.createItem(cp));

        GUI.setItem(37, PRESTIGESTATS.createItem(cp));
        GUI.setItem(38, PRESTIGEBRAG.createItem(cp));
        GUI.setItem(39, LOCALLEADERBOARD.createItem(cp));
        GUI.setItem(40, STARTWITHMOMS.createItem(cp));
        GUI.setItem(41, STARTWITHCHEFS.createItem(cp));

        //Profile button
        GUI.setItem(16, im.getPlayerSkullItem(getCp().getPlayer()));

        //Back to Main Menu button
        GUI.setItem(25, new ItemBuilder(Material.BARRIER).setDisplayName("&3Menu")
                                                         .addLore("&e► Click to return to the main menu.")
                                                         .create());

        //Prestige button
        ClickingPlayerData cpd = getCp().getData();
        String earnings =
                lore3 + "Prestige to earn " + lore1 + cpd.calculateAvailablePrestigeLevels() + " Coins " + lore3 + "and " + lore1 + "levels" + lore3 + "!";

        GUI.setItem(34, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&3Prestige")
                                                             .addLore(im.makeLore30CharsPerLine(
                                                                     "Prestige resets about everything, but you earn Coins in return!", lore3))
                                                             .addLore(cpd.canPrestige() ? earnings : lore3 + "You can not prestige yet.")
                                                             .addLore(cpd.canPrestige() ? lore2 + "&e► Click to prestige!" : "")
                                                             .create());

        //previous page button
        GUI.setItem(43, new ItemBuilder(Material.TIPPED_ARROW)
                .setDisplayName("&3Back")
                .addLore("&e► Click to return to the Upgrades Shop.")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .create());

        return GUI;
    }

    public void openNextAchievementsPage() {
        if (getAchievementsPageNumber() == 5) {
            getCp().doSoundEffect(0);
            getCp().doMessage("This is the final page.");
        } else {
            setAchievementsPageNumber(getAchievementsPageNumber() + 1);
            setGameState(2);
        }
    }

    public void openPreviousAchievementsPage() {
        if (getAchievementsPageNumber() == 1) {
            getCp().doSoundEffect(0);
            getCp().doMessage("This is the first page.");
        } else {
            setAchievementsPageNumber(getAchievementsPageNumber() - 1);
            setGameState(2);
        }
    }

    private void fillAchievementsGui(Inventory gui, ArrayList<ItemStack> items, int bound1, int bound2) {
        for (int i = bound1; i < bound2; i++) {
            if (i == items.size() || gui.firstEmpty() == -1) {
                return;
            }
            gui.setItem(gui.firstEmpty(), items.get(i));
        }
    }
}
