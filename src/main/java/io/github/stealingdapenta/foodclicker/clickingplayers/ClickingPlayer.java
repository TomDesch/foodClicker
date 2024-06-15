package io.github.stealingdapenta.foodclicker.clickingplayers;

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
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.EVENTCHANCELIMIT;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.EVENTTIME;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.MULTIPLIERPERUNLOCKEDACHIEVEMENT;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PERMANENTCLICKMULTIPLIER;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PERMANENTINCOMEMULTIPLIER;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STONKS;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.basics.AchievementsEnum;
import io.github.stealingdapenta.foodclicker.basics.Buildings;
import io.github.stealingdapenta.foodclicker.basics.Clickable;
import io.github.stealingdapenta.foodclicker.basics.GUI;
import io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnBuildings;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnEvents;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnStats;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import io.github.stealingdapenta.foodclicker.utils.RepeatingClickerTask;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class ClickingPlayer {
    private static final InventoryManager im = InventoryManager.getInstance();
    private static final Random r = new Random();
    @Getter
    private final Player player;
    @Getter
    private final ArrayList<Clickable> activeEvents;
    @Getter
    private final ArrayList<Clickable> activeBoosts;
    private final ClickingPlayerData cpd;
    private final ClickingPlayerSettings cps;
    @Getter
    private GUI gui;
    @Getter
    private RepeatingClickerTask repeatingClickerTask;
    @Getter
    private double incomePerSecond;
    @Getter
    private int closedDetector;
    @Getter
    private boolean buymodeAll;

    public ClickingPlayer(Player player) {
        this.player = player;
        this.buymodeAll = false;
        this.activeEvents = new ArrayList<>();
        this.activeBoosts = new ArrayList<>();
        this.cpd = new ClickingPlayerData(this);
        this.cps = new ClickingPlayerSettings(this);

        doAllCalculationsAsync();
    }

    public ClickingPlayerSettings getSettings() {
        return cps;
    }

    public ClickingPlayerData getData() {
        return this.cpd;
    }

    private void doAllCalculationsAsync() {
        ClickingPlayer cp = this;

        Bukkit.getScheduler().runTaskAsynchronously(FoodClicker.getInstance(), () -> {
            cp.getData().loadEverythingFromFile();
            cp.incomePerSecond = calculateTotalIncomePerSecond();
            cp.gui = new GUI(this);
            cp.repeatingClickerTask = createTask();

            earnAfkIncomes();

        });
    }

    public boolean noActiveEvents() {
        return this.getActiveEvents()
                   .isEmpty();
    }

    public void addActiveEvent(Clickable c) {
        this.getActiveEvents().add(c);
    }

    private void doSecondFromDurationFromAllActiveEvents() {
        if (getActiveEvents() == null) return;
        if (getActiveEvents().isEmpty()) {
            return;
        }

        for (Clickable c : getActiveEvents()) {
            c.setDuration(c.getDuration() - 1);
        }
        removeEventsExceedingDuration();
    }

    public void doClick() {
        doSoundEffect(-1);

        double amount = 1D * getData().getGeneralDoubleStats().get("Click Multiplier") * (1 + PERMANENTCLICKMULTIPLIER.getCurrentBonus(this) / 100);
        getData().addOneClick();
        earn(amount);
        getData().getGeneralDoubleStats().put("earnedByClicking", getData().getGeneralDoubleStats().get("earnedByClicking") + amount);

        possiblyUnlockAutoClickerAchievement(getData().getGeneralIntegerStats().get("cookie"));
    }

    private void possiblyUnlockAutoClickerAchievement(int timesClicked) {
        if (timesClicked >= 1) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER1);
        }
        if (timesClicked >= 50) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER2);
        }
        if (timesClicked >= 100) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER3);
        }
        if (timesClicked >= 500) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER4);
        }
        if (timesClicked >= 1000) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER5);
        }
        if (timesClicked >= 5000) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER6);
        }
        if (timesClicked >= 10000) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER7);
        }
        if (timesClicked >= 25000) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER8);
        }
        if (timesClicked >= 50000) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER9);
        }
        if (timesClicked >= 100000) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER10);
        }
        if (timesClicked >= 1000000) {
            unlockAchievement(AchievementsEnum.AUTOCLICKER11);
        }
    }

    private void setEventItem(Clickable c) {
        c.getInv().setItem(c.getSlotPosition(), c.getItemStack());
    }

    private void resetPaneItem(Clickable c) {
        c.getInv().setItem(c.getSlotPosition(), c.getReplacedItem());
    }

    public boolean clickedSlotIsEventClickable(int clickedSlot) {
        if (noActiveEvents()) return false;
        return (getActiveEvents().stream().anyMatch(s -> s.getSlotPosition() == clickedSlot));
    }

    public Clickable getEventBasedOnSlot(int clickedSlot) {
        return getActiveEvents().stream().filter(s -> s.getSlotPosition() == clickedSlot).findFirst().orElse(null);
    }

    public void eventGotClicked(Clickable c) {
        getData().addOneClickableClicked();
        getActiveEvents().remove(c);
        resetPaneItem(c);
        c.getBonus().doEffects(this);

        if (c.getBonus().getBoostDuration(this) != -1) {
            // perma upgrades don't need to be terminated
            getActiveBoosts().add(c);
            autoTerminateUpgrades(c);
        }
        if (getGui().getGameState() == 1 && c.getBonus().getBuildings() != null) {
            updateBuildingInGUI(getGui().getGameWindow(), c.getBonus().getBuildings()); // refresh if in shop menu
        }

        unlockBonusCollectorAchievement(getData().getGeneralIntegerStats().get("clickablesClicked"));
    }

    private void unlockBonusCollectorAchievement(int eventsClicked) {
        if (eventsClicked >= 10) {
            unlockAchievement(AchievementsEnum.BONUSCOLLECTOR1);
        }
        if (eventsClicked >= 100) {
            unlockAchievement(AchievementsEnum.BONUSCOLLECTOR2);

        }
        if (eventsClicked >= 500) {
            unlockAchievement(AchievementsEnum.BONUSCOLLECTOR3);

        }
        if (eventsClicked >= 1000) {
            unlockAchievement(AchievementsEnum.BONUSCOLLECTOR4);

        }
        if (eventsClicked >= 5000) {
            unlockAchievement(AchievementsEnum.BONUSCOLLECTOR5);

        }
        if (eventsClicked >= 10000) {
            unlockAchievement(AchievementsEnum.BONUSCOLLECTOR6);

        }
    }

    public boolean areBoostsActive() {
        return (getActiveBoosts() != null && !getActiveBoosts().isEmpty());
    }

    public void instantlyTerminateUpgrade(Clickable c) {
        c.getBonus().undoEffects(this);
    }

    public void possiblyTerminate() {
        if (areBoostsActive()) {
            for (Clickable c : getActiveBoosts()) {
                instantlyTerminateUpgrade(c);
                if (getGui().getGameState() == 1 && c.getBonus().getBuildings() != null) {
                    updateBuildingInGUI(getGui().getGameWindow(), c.getBonus().getBuildings()); // refresh if in shop menu
                }
            }
        }
    }

    public void autoTerminateUpgrades(Clickable c) {
        ClickingPlayer cp = this;

        Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> {
            c.getBonus().undoEffects(cp);
            getActiveBoosts().remove(c);
            if (cp.getGui().getGameState() == 1 && c.getBonus().getBuildings() != null) {
                cp.updateBuildingInGUI(cp.getGui().getGameWindow(), c.getBonus().getBuildings()); // refresh if in shop menu
            }
        }, (c.getBonus().getBoostDuration(this) * 20L));
        // turns into seconds bcs bukkit asks for ticks
    }

    public void removeEventsExceedingDuration() {
        for (Iterator<Clickable> iterator = getActiveEvents().iterator(); iterator.hasNext(); ) {
            Clickable c = iterator.next();
            if (c.getDuration() < 1) {
                resetPaneItem(c);
                iterator.remove();
            }
        }
    }

    public void possiblyDoEvent() {
        if (r.nextInt(100) < getData().getGeneralDoubleStats().get("eventChance")) {
            doNewEvent();
        }
    }

    public void doNewEvent() {
        Inventory window = this.getGui().getGameWindow();
        final List<UpgradesBasedOnEvents> eventBonuses = Collections.unmodifiableList(Arrays.asList(UpgradesBasedOnEvents.values()));

        Bukkit.getScheduler().runTaskAsynchronously(FoodClicker.getInstance(), () -> {
            UpgradesBasedOnEvents chosen = null;
            while (chosen == null) {
                int eventChooser = r.nextInt(eventBonuses.size());
                UpgradesBasedOnEvents possiblyChosen = eventBonuses.get(eventChooser);

                if (possiblyChosen.getAffectingKey().equals("eventChance")) {

                    double eventLimit = 30 + EVENTCHANCELIMIT.getCurrentBonus(this);

                    if (getData().getGeneralDoubleStats().get("eventChance") > eventLimit) {
                        getData().getGeneralDoubleStats().put("eventChance", eventLimit);
                    }
                    if (getData().getGeneralDoubleStats().get("eventChance") == eventLimit) {
                        continue;
                    }
                }

                //nerf stock events if current bank is over 65% of current legacy earnings
                if (possiblyChosen.getAffectingKey().equals("money") && possiblyChosen.getMultiplierIncrease() > 1D) {
                    if ((0.65D + STONKS.getCurrentBonus(this) / 100) * cpd.getGeneralBigDecimals().get("legacy earnings").doubleValue() < cpd.getGeneralBigDecimals().get("money").doubleValue()) {
                        continue;
                    }
                }

                if (possiblyChosen.getBuildings() == null || (getData().getSpecificBuildingAmount(possiblyChosen.getBuildings()) > 0)) {
                    chosen = possiblyChosen;
                }
            }
            Clickable event = new Clickable(8 + r.nextInt(12) + (int) EVENTTIME.getCurrentBonus(this), window, chosen, this);
            doSoundEffect(20);
            setEventItem(event);
            addActiveEvent(event);

        });
    }

    private void possiblyGetLeprechaun() {
        if (r.nextInt(1000000) == 1) {
            unlockAchievement(AchievementsEnum.LEPRECHAUN);
        }
    }

    private RepeatingClickerTask createTask() {
        ClickingPlayer cp = this;

        return new RepeatingClickerTask(FoodClicker.getInstance(), 30L, 20L) {
            @Override
            public void run() {
                InventoryView view = player.getOpenInventory();
                Inventory gameWindow = view.getTopInventory();

                if (im.inventoryIsFoodClicker(view.getTitle(), player)) {
                    if (cp.getClosedDetector() > 0) cp.resetClosedDetector();
                    cp.getData().addOneSecond();
                    cp.doIncomePerSecond();
                    cp.updatePlayerHead(gameWindow);
                    cp.doSecondFromDurationFromAllActiveEvents();
                    cp.possiblyGetLeprechaun();

                    if (cp.getGui().getGameState() == 0) {
                        cp.possiblyDoEvent();
                    }
                } else {
                    cp.addClosedDetection();
                    if (cp.getClosedDetector() >= 3) {
                        System.out.println("FoodClicker: " + player.getName() + " closed FoodClicker. Task ending & saving file.");

                        cp.getData().saveGameCloseDate(System.currentTimeMillis() / 1000);


                        cp.possiblyTerminate();

                        Bukkit.getScheduler().runTaskAsynchronously(FoodClicker.getInstance(), () -> {
                            cp.getData().saveEverythingToFile();
                            im.removePlayerFromMap(player);
                        });

                        System.out.println("FoodClicker: " + player.getName() + " 's file saved.");
                        this.cancel();
                    }
                }
            }
        };
    }

    public void setBuymodeAll(boolean buymodeAll) {
        this.buymodeAll = buymodeAll;
    }

    public void setIncomePerSecond(double incomePerSecond) {
        this.incomePerSecond = incomePerSecond;
    }

    public void earn(double amount) {
        getData().addMoney(new BigDecimal(amount));
    }

    public void earn(BigDecimal amount) {
        getData().addMoney(amount);
    }

    public void pay(double amount) {
        getData().removeMoney(new BigDecimal(amount));
    }

    public void pay(BigDecimal amount) {
        getData().removeMoney(amount);
    }

    public boolean canAfford(double amount) {
        return getData().getGeneralBigDecimals().get("money").min(BigDecimal.valueOf(amount)).equals(BigDecimal.valueOf(amount));
    }

    public boolean canAfford(BigDecimal amount) {
        return getData().getGeneralBigDecimals().get("money").min(amount).equals(amount);
    }

    public void updateBuymodeButton(Inventory inv) {
        inv.setItem(34, new ItemBuilder(Material.ENDER_EYE).setDisplayName("&6Buy mode")
                                                           .addLore("&e► Click to change buy mode.")
                                                           .addLore(isBuymodeAll() ? "&a○ Currently active: buy max" : "&a○ Currently active: buy x1")
                                                           .setGlowing(true).create());
    }

    private void updatePlayerHead(Inventory inv) {
        inv.setItem(16, getPlayerStatsHead(getData().isPrestigeHead()));
    }

    public ItemStack getPlayerStatsHead(boolean isPrestigeHead) {
        String lore1 = getSettings().getPrimaryLoreColor(); // blue
        String lore2 = getSettings().getSecondaryLoreColor(); // green
        String lore3 = getSettings().getTextLoreColor(); // white or gray

        ClickingPlayerData cpd = getData();
        if (!isPrestigeHead) {
            return new ItemBuilder(im.getPlayerSkullItem(player))
                    .addLore(lore1 + "This legacy playtime: " + lore3 + im.makeSecondsATimestamp(cpd.getGeneralIntegerStats().get("Prestige Playtime")))
                    .addLore(lore1 + "Total clicks: " + lore3 + im.makeNumbersPretty(cpd.getGeneralIntegerStats().get("cookie")))
                    .addLore(lore1 + "This legacy earnings: " + lore2 + "£" + im.truncateNumber(cpd.getGeneralBigDecimals().get("legacy earnings"), this))
                    .addLore(lore1 + "Earnings by clicking: " + lore2 + "£" + im.truncateNumber(cpd.getGeneralDoubleStats().get("earnedByClicking"), this))
                    .addLore(lore1 + "Bank: " + lore2 + "£" + im.truncateNumber(cpd.getGeneralBigDecimals().get("money"), this))
                    .addLore(lore1 + "Income per click: " + lore2 + "£" + im.truncateNumber(getData().getGeneralDoubleStats().get("Click Multiplier") * (1 + PERMANENTCLICKMULTIPLIER.getCurrentBonus(this) / 100), this))
                    .addLore(lore1 + "Passive income: " + lore2 + "£" + im.truncateNumber(getIncomePerSecond(), this) + lore3 + "/s")
                    .addLore(lore1 + "Buildings owned: " + lore3 + im.makeNumbersPretty(cpd.calculateTotalBuildingsOwned()))
                    .addLore(lore1 + "Upgrades purchased: " + lore3 + im.makeNumbersPretty(cpd.calculateTotalUpgradesOwned()))
                    .addLore(lore1 + "Achievements unlocked: " + lore3 + im.makeNumbersPretty(cpd.calculateTotalAchievementsUnlocked()))
                    .addLore(lore1 + "Event chance: " + lore3 + im.makeNumbersPretty(cpd.getGeneralDoubleStats().get("eventChance")) + lore3 + "%")
                    .addLore(lore1 + "Bonuses collected: " + lore3 + im.makeNumbersPretty(cpd.getGeneralIntegerStats().get("clickablesClicked")))
                    .addLore(cpd.getCurrentPrestigeLevels() > 0 ? lore1 + "Prestige level: " + lore3 + im.makeNumbersPretty(cpd.getCurrentPrestigeLevels()) : "")
                    .addLore(cpd.getCurrentPrestigeLevels() > 0 ? lore1 + "Prestige Coins: " + lore3 + im.makeNumbersPretty(cpd.getCurrentPrestigeCoins()) : "")
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES).create();
        } else {
            List<String> prestigeLore = new ArrayList<>();

            for (PrestigeEnum prestigeEnum : PrestigeEnum.values()) {
                if (prestigeEnum.getAmountOwned(this) == prestigeEnum.getMaxAmount()) {
                    prestigeLore.add((lore2 + "• " + lore1 + prestigeEnum.getName() + " owned: " + lore2 + im.makeNumbersPretty(prestigeEnum.getAmountOwned(this)) + "/" + prestigeEnum.getMaxAmount() + lore3 + "."));
                } else {
                    prestigeLore.add((lore2 + "• " + lore1 + prestigeEnum.getName() + " owned: " + lore3 + im.makeNumbersPretty(prestigeEnum.getAmountOwned(this)) + lore3 + "/" + prestigeEnum.getMaxAmount() + "."));
                }
            }

            return new ItemBuilder(im.getPlayerSkullItem(player))
                    .addLore(lore1 + "Total playtime: " + lore3 + im.makeSecondsATimestamp(cpd.getGeneralIntegerStats().get("playtime")))
                    .addLore(lore1 + "All time earnings: " + lore2 + "£" + im.truncateNumber(cpd.getGeneralBigDecimals().get("alltimeearnings"), this))
                    .addLore(lore1 + "Prestige level: " + lore3 + im.makeNumbersPretty(cpd.getCurrentPrestigeLevels()))
                    .addLore(lore1 + "Prestige Coins: " + lore3 + im.makeNumbersPretty(cpd.getCurrentPrestigeCoins()))
                    .addLore(prestigeLore)
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES).create();
        }
    }

    public void unlockAchievement(AchievementsEnum achievementsEnum) {
        achievementsEnum.unlock(this);
    }

    public void updateBuildingInGUI(Inventory inv, Buildings b) {
        inv.setItem(b.getGuiSlot(), b.createItem(this));
    }

    public void doIncomePerSecond() {
        BigDecimal income = BigDecimal.valueOf(getIncomePerSecond());
        getData().addMoney(income);

        possiblyUnlockIdleAchievement(income);
    }

    private void possiblyUnlockIdleAchievement(BigDecimal income) {
        if (income.compareTo(BigDecimal.valueOf(1)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE1);
        }
        if (income.compareTo(BigDecimal.valueOf(10)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE2);
        }
        if (income.compareTo(BigDecimal.valueOf(100)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE3);
        }
        if (income.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE4);
        }
        if (income.compareTo(BigDecimal.valueOf(100000)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE5);
        }
        if (income.compareTo(BigDecimal.valueOf(100_000_000)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE6);
        }
        if (income.compareTo(BigDecimal.valueOf(100_000_000_000D)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE7);
        }
        if (income.compareTo(BigDecimal.valueOf(100_000_000_000_000D)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE8);
        }
        if (income.compareTo(BigDecimal.valueOf(100_000_000_000_000_000D)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE9);
        }
        if (income.compareTo(BigDecimal.valueOf(100_000_000_000_000_000_000D)) >= 0) {
            unlockAchievement(AchievementsEnum.IDLE10);
        }
    }

    public double calculateTotalIncomePerSecond() {
        return (MOM.calculateIncome(this)
                + CHEF.calculateIncome(this)
                + CAFETERIA.calculateIncome(this)
                + DELIVERY.calculateIncome(this)
                + DRIVETHROUGH.calculateIncome(this)
                + FOODTRUCK.calculateIncome(this)
                + POPUP.calculateIncome(this)
                + RESTAURANT.calculateIncome(this)
                + HOTEL.calculateIncome(this)
                + CHAIN.calculateIncome(this)
                + COMPANY.calculateIncome(this)
                + HOUSEHOLDNAME.calculateIncome(this))
                * (1 + MULTIPLIERPERUNLOCKEDACHIEVEMENT.getCurrentBonus(this))
                * (1 + PERMANENTINCOMEMULTIPLIER.getCurrentBonus(this));
    }

    public void flickBuyMode() {
        setBuymodeAll(!isBuymodeAll());
    }

    public void setClosedDetector(int closedDetector) {
        this.closedDetector = closedDetector;
    }

    public void resetClosedDetector() {
        setClosedDetector(0);
    }

    public void addClosedDetection() {
        setClosedDetector(getClosedDetector() + 1);
    }

    private double calculateAfkButOnline() {
        ClickingPlayerData cpd = getData();
        long gameCloseTime = cpd.loadGameCloseTime();
        long afkTime = Math.min((long) AFKBUTONLINEDURATION.getCurrentBonus(this), gameCloseTime);
        cpd.resetGameCloseTime();

        return calculateTotalIncomePerSecond() * AFKBUTONLINE.getCurrentBonus(this) / 100 * afkTime;
    }

    private double calculateAfkAndOffline() {
        ClickingPlayerData cpd = getData();
        long offlineTime = cpd.loadLoggedOutTime();
        long offlineDuration = Math.min((long) AFKANDOFFLINEDURATION.getCurrentBonus(this), offlineTime);
        cpd.resetLoggedOutTime();

        return calculateTotalIncomePerSecond() * AFKANDOFFLINE.getCurrentBonus(this) / 100 * offlineDuration;
    }

    private void earnAfkButOnline() {
        double amount = calculateAfkButOnline();
        if (amount > 0) {
            earn(amount);
            doMessage("You earned &a£" + im.truncateNumber(amount, this) + " &ewhile being out of FoodClicker, but online on the server.");
        }

    }

    private void earnAfkAndOffline() {
        double amount = calculateAfkAndOffline();
        if (amount > 0) {
            earn(amount);
            doMessage("You earned &a£" + im.truncateNumber(amount, this) + " &ewhile being offline.");
        }
    }

    public void earnAfkIncomes() {
        earnAfkButOnline();
        earnAfkAndOffline();
    }

    public ArrayList<ItemStack> getBuyableUpgrades() {
        //todo expand with based on events and achievements
        ArrayList<ItemStack> items = new ArrayList<>();
        for (UpgradesBasedOnStats u : UpgradesBasedOnStats.values()) {
            if (u.requirementMetAndNotUnlocked(this)) {
                items.add(u.createItem(this));
            }
        }
        for (UpgradesBasedOnBuildings ub : UpgradesBasedOnBuildings.values()) {
            if (ub.requirementMetAndNotUnlocked(this)) {
                items.add(ub.createItem(this));
            }
        }
        return items;
    }

    public void updateUpgradesWindow(Inventory inv) {
        if (inv.firstEmpty() == -1) return;

        ArrayList<ItemStack> buyableUpgrades = getBuyableUpgrades();
        for (int i = 0; i < (Math.min(buyableUpgrades.size(), 20)); i++) { // max 20 open spots in the inv hence <20 if a lot of upgrades unlocked
            if (inv.contains(buyableUpgrades.get(i))) {
                continue; // safety check that you dont put in the same item after a spot came open after buying an upgrade
            }
            inv.setItem(inv.firstEmpty(), buyableUpgrades.get(i));
        }
        im.setInventoryPattern(inv, false, getSettings().getPrimaryPaneColor(), getSettings().getSecondaryPaneColor());
    }

    public boolean itemIsBuyableUpgrade(ItemStack item) {
        return getBuyableUpgrades().contains(item);
    }

    public Object getUpgradeRelatedToItem(ItemStack item) {
        if (!itemIsBuyableUpgrade(item)) return null;
        for (UpgradesBasedOnStats u : UpgradesBasedOnStats.values()) {
            if (u.requirementMetAndNotUnlocked(this)) {
                if (u.createItem(this).equals(item)) {
                    return u;
                }
            }
        }
        for (UpgradesBasedOnBuildings u : UpgradesBasedOnBuildings.values()) {
            if (u.requirementMetAndNotUnlocked(this)) {
                if (u.createItem(this).equals(item)) {
                    return u;
                }
            }
        }

        return null;
    }

    private void possiblyUnlockBuildingAchievement(Buildings b) {
        int amount = getData().getSpecificBuildingAmount(b);
        List<AchievementsEnum> achievementsEnums =
                Arrays.asList(Arrays.stream(AchievementsEnum.values()).filter(a -> a.getBuilding() != null && a.getBuilding() == b).toArray(AchievementsEnum[]::new));
        if (!achievementsEnums.isEmpty() && amount >= 100 && amount < 500) {
            unlockAchievement(achievementsEnums.get(0));
        } else if (achievementsEnums.size() >= 2 && amount >= 500 && amount < 1000) {
            unlockAchievement(achievementsEnums.get(1));
        } else if (achievementsEnums.size() >= 3 && amount >= 1000) {
            unlockAchievement(achievementsEnums.get(2));
        }
    }

    public void doSuccessfulPurchase(Buildings b) {

        doFireWorks(1, 0);
        doSoundEffect(1);

        double cost = b.calculateCost(this);

        pay(cost);
        getData().addOneBuilding(b);
        setIncomePerSecond(calculateTotalIncomePerSecond());
        updateBuildingInGUI(getGui().getGameWindow(), b);
        possiblyUnlockBuildingAchievement(b);
        doMessage("You bought a &b" + b.getName() + "&e for only &a£" + im.truncateNumber(cost, this) + "&e.");
    }

    public void possiblyBuy(Buildings b) {
        if (canAfford(b.calculateCost(this))) {
            if (isBuymodeAll()) {
                doMaxPurchases(b);
            } else {
                doSuccessfulPurchase(b);
            }
        } else {
            doSoundEffect(0);
            doMessage("You can't afford this building.");
        }
    }

    public void doMaxPurchases(Buildings b) {
        ClickingPlayer cp = this;
        new RepeatingClickerTask(FoodClicker.getInstance(), 1L, 1L) {
            @Override
            public void run() {
                if (canAfford(b.calculateCost(cp))) {
                    doSuccessfulPurchase(b);
                } else {
                    cancel();
                }
            }
        };
    }

    public void doMessage(String message) {
        if (getSettings().isDoChatMessages()) {
            getPlayer().sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &e" + message));
        }
    }

    public void doFireWorks(int amount, int size) {
        if (getSettings().isDoFirework()) {
            Location loc = getPlayer().getLocation();
            if (loc.getWorld() == null) return;
            Random r = new Random();
            Firework fw = (Firework) loc.getWorld()
                                        .spawnEntity(loc, EntityType.FIREWORK_ROCKET);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(size);
            fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).flicker(true).build());
            fw.remove();

            for (int i = 0; i < amount; i++) {
                Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> {
                    fwm.clearEffects();
                    fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).flicker(true).trail(true).build());

                    Firework fw2 = (Firework) loc.getWorld()
                                                 .spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                    fw2.setFireworkMeta(fwm);
                }, (1L + i * 4L));
            }
        }
    }

    public void doSoundEffect(int size) {
        if (getSettings().isDoSoundEffects()) {
            switch (size) {
                case -2: // bad event
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_CAT_HISS, 1, r.nextFloat() + 1);
                    break;
                case -1: // metal clicking sound
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, Math.max(r.nextFloat() + 1, 1.3F));
                    break;
                case 0: // no.
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, Math.max(0.5F + r.nextFloat(), 0.5F));
                    break;
                case 1:
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.35F);
                    break;
                case 2:
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
                    Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.75F), 2L);
                    Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F), 4L);
                    break;
                case 3:
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
                    Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.75F), 2L);
                    Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F), 4L);
                    Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5F), 6L);
                    Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2F), 8L);
                    break;
                case 4: // achievement unlocked
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1F);
                    break;
                case 10: // random button clicks
                    getPlayer().playSound(getPlayer().getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, Math.max(r.nextFloat(), 0.5F));
                    break;
                case 20: // Event spawned
                    getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_CAT_PURR, 1, 1);
                    break;
                default:
                    break;
            }
        }
    }

}
