package io.github.stealingdapenta.foodclicker.utils;

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

import io.github.stealingdapenta.foodclicker.basics.AchievementsEnum;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayerSettings;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnBuildings;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnStats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;

public class Listeners implements Listener {

    public static final String GAME_GUI_TITLE_ADDENDUM = "'s Factory";
    public static final String SHOP_GUI_TITLE_ADDENDUM = "'s Shop";
    public static final String PREFERENCES_GUI_TITLE_ADDENDUM = "'s Preferences";
    public static final String ACHIEVEMENTS_GUI_TITLE_ADDENDUM = "'s Achievements";
    public static final String UPGRADES_GUI_TITLE_ADDENDUM = "'s Upgrades Shop";
    public static final String PRESTIGE_SHOP = "'s Prestige Shop";

    private final InventoryManager im = InventoryManager.getInstance();
    private final FileManager fm = FileManager.getInstance();

    private static String possibleTitle(Player p, String addendum) {
        return p.getDisplayName() + addendum;
    }

    private void possiblyUnlockClickTypeAchievements(ClickType ct, ClickingPlayer cp) {
        if (ct.equals(ClickType.RIGHT)) {
            cp.unlockAchievement(AchievementsEnum.RIGHT);
        }
        if (ct.equals(ClickType.MIDDLE)) {
            cp.unlockAchievement(AchievementsEnum.WHEELIE);
        }
        if (ct.equals(ClickType.DROP)) {
            cp.unlockAchievement(AchievementsEnum.DROP);
        }
        if (ct.equals(ClickType.NUMBER_KEY)) {
            cp.unlockAchievement(AchievementsEnum.NUMERAL);
        }

    }


    public long loadGameCloseDate(Player p) {
        return fm.getLongByKey(p, "gameCloseDate");
    }

    public void saveGameCloseDate(Player p, long time) {
        fm.setLongByKey(p, "gameCloseDate", time);
    }

    public long loadGameCloseTime(Player p) {
        return fm.getLongByKey(p, "gameCloseTime");
    }

    public void saveGameCloseTime(Player p, long duration) {
        fm.setLongByKey(p, "gameCloseTime", duration);
    }


    public long loadLogOutDate(Player p) {
        return fm.getLongByKey(p, "loggedOutDate");
    }

    public void saveLogOutDate(Player p, long time) {
        fm.setLongByKey(p, "loggedOutDate", time);
    }

    public long loadLoggedOutTime(Player p) {
        return fm.getLongByKey(p, "loggedOutTime");
    }

    public void saveLoggedOutTime(Player p, long duration) {
        fm.setLongByKey(p, "loggedOutTime", duration);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        saveLogOutDate(p, System.currentTimeMillis() / 1000);

        long time = loadGameCloseDate(p) == 0 ? 0 : System.currentTimeMillis() / 1000 - loadGameCloseDate(p);

        saveGameCloseTime(p, loadGameCloseTime(p) + time);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        saveLogOutDate(p, System.currentTimeMillis() / 1000);

        long time = loadGameCloseDate(p) == 0 ? 0 : System.currentTimeMillis() / 1000 - loadGameCloseDate(p);

        saveGameCloseTime(p, loadGameCloseTime(p) + time);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        saveGameCloseDate(p, System.currentTimeMillis() / 1000);

        long time = loadLogOutDate(p) == 0 ? 0 : System.currentTimeMillis() / 1000 - loadLogOutDate(p);

        saveLoggedOutTime(p, loadLoggedOutTime(p) + time);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        InventoryView inv = e.getView();
        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        ClickType ct = e.getClick();

        if (!im.inventoryIsFoodClicker(inv.getTitle(), p)) {
            return;
        }
        e.setCancelled(true);

        ClickingPlayer cp = im.getClickingPlayerMap()
                              .get(p);
        if (cp == null) {
            im.closeGUI(p);
            cp = new ClickingPlayer(p);
            im.addClickingPlayerToMap(cp);
            return;
        }

        possiblyUnlockClickTypeAchievements(ct, cp);
        if (e.getSlotType()
             .equals(InventoryType.SlotType.OUTSIDE)) {
            cp.unlockAchievement(AchievementsEnum.OUTSIDER);
        }

        if (e.getClickedInventory() != null) {
            if (e.getClickedInventory()
                 .equals(e.getView()
                          .getBottomInventory())) {
                cp.unlockAchievement(AchievementsEnum.INSIDER);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem()
                         .getType()
                         .equals(Material.COOKIE)) {
                        cp.unlockAchievement(AchievementsEnum.ORTEIL);
                    }
                }
            }
        }

        if (slot == 16) {
            cp.unlockAchievement(AchievementsEnum.HEADCASE);
            if (PRESTIGESTATS.getAmountOwned(cp) > 0) {
                cp.getData()
                  .doPrestigeHead();
            }
        }

        if (inv.getTitle()
               .equals(possibleTitle(p, GAME_GUI_TITLE_ADDENDUM))) {
            doMainGameInteractions(slot, cp);
        } else if (inv.getTitle()
                      .equals(possibleTitle(p, SHOP_GUI_TITLE_ADDENDUM))) {
            doStoreInteractions(slot, cp, inv);
        } else if (inv.getTitle()
                      .equals(possibleTitle(p, ACHIEVEMENTS_GUI_TITLE_ADDENDUM))) {
            doAchievementInteractions(slot, cp);
        } else if (inv.getTitle()
                      .equals(possibleTitle(p, UPGRADES_GUI_TITLE_ADDENDUM))) {
            doUpgradesInteractions(slot, cp, inv);
        } else if (inv.getTitle()
                      .equals(possibleTitle(p, PREFERENCES_GUI_TITLE_ADDENDUM))) {
            doPreferencesInteractions(slot, cp);
        } else if (inv.getTitle()
                      .equals(possibleTitle(p, PRESTIGE_SHOP))) {
            doPrestigeShopInteractions(slot, cp);
        }
    }

    private void doMainGameInteractions(int clickedSlot, ClickingPlayer cp) {
        switch (clickedSlot) {
            case 20: //the cookie
                cp.doClick();
                break;
            case 25: // settings button
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(4);
                break;
            case 34: // shop button
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(1);
                break;
            case 43: // achievements gui
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(2);
                break;
            default:
                if (cp.clickedSlotIsEventClickable(clickedSlot)) {
                    cp.eventGotClicked(cp.getEventBasedOnSlot(clickedSlot));
                }
                break;
        }
    }

    private void doStoreInteractions(int clickedSlot, ClickingPlayer cp, InventoryView inv) {
        switch (clickedSlot) {
            case 10:
                cp.possiblyBuy(MOM);
                break;
            case 11:
                cp.possiblyBuy(CHEF);
                break;
            case 12:
                cp.possiblyBuy(CAFETERIA);
                break;
            case 19:
                cp.possiblyBuy(DELIVERY);
                break;
            case 20:
                cp.possiblyBuy(DRIVETHROUGH);
                break;
            case 21:
                cp.possiblyBuy(FOODTRUCK);
                break;
            case 28:
                cp.possiblyBuy(POPUP);
                break;
            case 29:
                cp.possiblyBuy(RESTAURANT);
                break;
            case 30:
                cp.possiblyBuy(HOTEL);
                break;
            case 37:
                cp.possiblyBuy(CHAIN);
                break;
            case 38:
                cp.possiblyBuy(COMPANY);
                break;
            case 39:
                cp.possiblyBuy(HOUSEHOLDNAME);
                break;

            case 25: // Upgrades GUI button
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(3);
                break;
            case 34: //button buy mode flick
                cp.doSoundEffect(10);
                cp.flickBuyMode();
                cp.updateBuymodeButton(inv.getTopInventory());
                break;
            case 43: // main menu
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(0);
            default:
                break;
        }
    }

    private void doUpgradesInteractions(int clickedSlot, ClickingPlayer cp, InventoryView inv) {
        switch (clickedSlot) {
            case 25: // main menu button
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(0);
                break;
            case 34: // prestige shop
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(5);
                break;
            case 43: // return to shop
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(1);
                break;
            default:
                //todo extend with upgrades based on achievements
                Object u = cp.getUpgradeRelatedToItem(inv.getItem(clickedSlot));
                if (u == null) {
                    return;
                }

                if (u instanceof UpgradesBasedOnBuildings) {
                    ((UpgradesBasedOnBuildings) u).possiblyBuyUpgrade(cp, inv.getTopInventory(), clickedSlot);
                    return;
                } else if (u instanceof UpgradesBasedOnStats) {
                    ((UpgradesBasedOnStats) u).possiblyBuyUpgrade(cp, inv.getTopInventory(), clickedSlot);
                    return;
                }
                break;
        }
    }

    private void doPreferencesInteractions(int clickedSlot, ClickingPlayer cp) {
        ClickingPlayerSettings settings = cp.getSettings();
        switch (clickedSlot) {
            case 10:
                cp.doSoundEffect(10);
                settings.flickInventoryDarkMode();
                cp.getGui()
                  .setGameState(4); // refresh settings window
                break;
            case 11:
                cp.doSoundEffect(10);
                settings.flickLoreDarkMode();
                cp.getGui()
                  .setGameState(4); // refresh settings window
                break;
            case 12:
                cp.doSoundEffect(10);
                settings.flickDoChatMessages();
                cp.getGui()
                  .setGameState(4); // refresh settings window
                break;
            case 13:
                cp.doSoundEffect(10);
                settings.flickDoSoundEffects();
                cp.getGui()
                  .setGameState(4); // refresh settings window
                break;
            case 14:
                cp.doSoundEffect(10);
                settings.flickDoFirework();
                cp.getGui()
                  .setGameState(4); // refresh settings window
                break;
            case 19:
                cp.doSoundEffect(10);
                settings.flickDoBigNumbers();
                cp.getGui()
                  .setGameState(4); // refresh settings window
                break;
            case 25: // main menu button
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(0);
                break;
            default:
                break;
        }
    }

    private void doAchievementInteractions(int clickedSlot, ClickingPlayer cp) {
        switch (clickedSlot) {
            case 25: // main menu button
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(0);
                break;
            case 34: // next page
                cp.doSoundEffect(10);
                cp.getGui()
                  .openNextAchievementsPage();
                break;
            case 43: // previous page
                cp.doSoundEffect(10);
                cp.getGui()
                  .openPreviousAchievementsPage();
                break;
            default:
                if (clickedSlot == 28 && cp.getGui()
                                           .getAchievementsPageNumber() == 1) {
                    cp.unlockAchievement(AchievementsEnum.WELCOME);
                }
                break;
        }
    }

    private void doPrestigeShopInteractions(int clickedSlot, ClickingPlayer cp) {
        switch (clickedSlot) {
            case 10:
                AFKBUTONLINE.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 11:
                AFKBUTONLINEDURATION.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 12:
                AFKANDOFFLINE.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 13:
                AFKANDOFFLINEDURATION.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 14:
                PERMANENTINCOMEMULTIPLIER.possiblyBuyPrestigeUpgrade(cp);
                cp.setIncomePerSecond(cp.calculateTotalIncomePerSecond());
                break;

            case 19:
                MULTIPLIERPERUNLOCKEDACHIEVEMENT.possiblyBuyPrestigeUpgrade(cp);
                cp.setIncomePerSecond(cp.calculateTotalIncomePerSecond());
                break;
            case 20:
                EVENTCHANCELIMIT.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 21:
                STARTWITHEVENCHANCE.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 22:
                EVENTTIME.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 23:
                EVENTDURATION.possiblyBuyPrestigeUpgrade(cp);
                break;

            case 28:
                DISCOUNT.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 29:
                CHEAP.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 30:
                FAIRTRADE.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 31:
                STONKS.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 32:
                PERMANENTCLICKMULTIPLIER.possiblyBuyPrestigeUpgrade(cp);
                break;

            case 37:
                PRESTIGESTATS.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 38:
                PRESTIGEBRAG.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 39:
                LOCALLEADERBOARD.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 40:
                STARTWITHMOMS.possiblyBuyPrestigeUpgrade(cp);
                break;
            case 41:
                STARTWITHCHEFS.possiblyBuyPrestigeUpgrade(cp);
                break;

            case 25: // main menu button
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(0);
                break;
            case 34: // prestige
                if (cp.getData()
                      .canPrestige()) {
                    cp.doSoundEffect(3);
                    cp.doFireWorks(5, 1);
                    cp.doMessage("Congratulations! You've earned &b" + cp.getData()
                                                                         .calculateAvailablePrestigeLevels() + " Prestige Coins&e!");

                    cp.getData()
                      .doPrestige();
                    cp.getGui()
                      .setGameState(0);
                } else {
                    cp.doMessage("You can't afford Prestige yet.");
                    cp.doSoundEffect(0);
                }
                break;
            case 43: // return to Upgrades shop
                cp.doSoundEffect(10);
                cp.getGui()
                  .setGameState(3);
                break;
            default:
                break;
        }
    }


}
