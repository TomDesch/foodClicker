package io.github.stealingdapenta.foodclicker.clickingplayers;

import io.github.stealingdapenta.foodclicker.basics.AchievementsEnum;
import io.github.stealingdapenta.foodclicker.basics.Buildings;
import io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnBuildings;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnStats;
import io.github.stealingdapenta.foodclicker.utils.FileManager;
import io.github.stealingdapenta.foodclicker.utils.RootCalculus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHCHEFS;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHEVENCHANCE;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHMOMS;

public class ClickingPlayerData {

    private final FileManager fm = FileManager.getInstance();
    private final Map<Buildings, Integer> buildingsOwned;
    private final Map<Buildings, Double> buildingsBaseMultipliers;
    private final Map<Buildings, Double> buildingsBaseCostMultipliers;
    private final Map<Buildings, Double> buildingsBaseCostIncreaser;
    private final Map<String, Boolean> upgradesUnlocked;
    private final Map<String, Integer> generalIntegerStats;
    private final Map<String, Double> generalDoubleStats;
    private final Map<String, BigDecimal> generalBigDecimals;
    private final Map<String, Boolean> achievements;
    private final Map<PrestigeEnum, Integer> prestigeShopBonusses;
    private final ClickingPlayer cp;
    private boolean prestigeHead;

    public ClickingPlayerData(ClickingPlayer cp) {
        this.cp = cp;

        this.buildingsOwned = new HashMap<>();
        this.buildingsBaseMultipliers = new HashMap<>();
        this.buildingsBaseCostMultipliers = new HashMap<>();
        this.buildingsBaseCostIncreaser = new HashMap<>();
        this.upgradesUnlocked = new HashMap<>();
        this.generalIntegerStats = new HashMap<>();
        this.generalDoubleStats = new HashMap<>();
        this.generalBigDecimals = new HashMap<>();
        this.prestigeShopBonusses = new HashMap<>();
        this.achievements = new HashMap<>();
        this.prestigeHead = false;
    }

    public Map<PrestigeEnum, Integer> getPrestigeShopBonusses() {
        return prestigeShopBonusses;
    }

    public long loadGameCloseDate() {
        return fm.getIntByKey(getCp(), "gameCloseDate");
    }

    public void resetGameCloseTime() {
        saveGameCloseTime(0);
    }

    public void saveGameCloseTime(long duration) {
        fm.setLongByKey(getCp(), "gameCloseTime", duration);
    }

    public long loadGameCloseTime() {
        return fm.getLongByKey(getCp(), "gameCloseTime");
    }

    public void saveGameCloseDate(long duration) {
        fm.setLongByKey(getCp(), "gameCloseDate", duration);
    }

    public long loadLoggedOutTime() {
        return fm.getLongByKey(getCp(), "loggedOutTime");
    }

    public void saveLoggedOutTime(long duration) {
        fm.setLongByKey(getCp(), "loggedOutTime", duration);
    }

    public void resetLoggedOutTime() {
        saveLoggedOutTime(0);
    }

    public boolean isPrestigeHead() {
        return prestigeHead;
    }

    public void doPrestigeHead() {
        this.prestigeHead = !isPrestigeHead();
    }

    public ClickingPlayer getCp() {
        return cp;
    }

    public Map<Buildings, Integer> getBuildingsOwned() {
        return buildingsOwned;
    }

    public Map<Buildings, Double> getBuildingsBaseMultipliers() {
        return buildingsBaseMultipliers;
    }

    public Map<Buildings, Double> getBuildingsBaseCostMultipliers() {
        return buildingsBaseCostMultipliers;
    }

    public Map<String, Boolean> getUpgradesUnlocked() {
        return upgradesUnlocked;
    }

    public Map<String, Integer> getGeneralIntegerStats() {
        return generalIntegerStats;
    }

    public Map<String, BigDecimal> getGeneralBigDecimals() {
        return generalBigDecimals;
    }

    public Map<String, Double> getGeneralDoubleStats() {
        return generalDoubleStats;
    }

    public Map<Buildings, Double> getBuildingsBaseCostIncreaser() {
        return buildingsBaseCostIncreaser;
    }

    public int calculateTotalBuildingsOwned() {
        return getBuildingsOwned().values().stream().mapToInt(i -> i).sum();
    }

    public int calculateTotalUpgradesOwned() {
        int amountUnlocked = 0;
        for (Boolean b : getUpgradesUnlocked().values()) {
            if (b) {
                amountUnlocked++;
            }
        }
        return amountUnlocked;
    }

    public int calculateTotalAchievementsUnlocked() {
        return (int) getAchievements().values().stream().filter(b -> b).count();
    }

    public void addOneBuilding(Buildings b) {
        getBuildingsOwned().put(b, (getSpecificBuildingAmount(b) + 1));
    }

    public void addOneSecond() {
        getGeneralIntegerStats().put("playtime", getGeneralIntegerStats().get("playtime") + 1);
        getGeneralIntegerStats().put("Prestige Playtime", getGeneralIntegerStats().get("Prestige Playtime") + 1);

        possiblyUnlockAfkAchievement(getGeneralIntegerStats().get("playtime"));
    }

    private void possiblyUnlockAfkAchievement(int playtime) {
        if (playtime >= 60) {
            getCp().unlockAchievement(AchievementsEnum.AFK1);
        }
        if (playtime >= 60 * 10) {
            getCp().unlockAchievement(AchievementsEnum.AFK2);
        }
        if (playtime >= 60 * 30) {
            getCp().unlockAchievement(AchievementsEnum.AFK3);
        }
        if (playtime >= 60 * 60) {
            getCp().unlockAchievement(AchievementsEnum.AFK4);
        }
        if (playtime >= 60 * 60 * 2) {
            getCp().unlockAchievement(AchievementsEnum.AFK5);
        }
        if (playtime >= 60 * 60 * 5) {
            getCp().unlockAchievement(AchievementsEnum.AFK6);
        }
        if (playtime >= 60 * 60 * 10) {
            getCp().unlockAchievement(AchievementsEnum.AFK7);
        }
        if (playtime >= 60 * 60 * 24) {
            getCp().unlockAchievement(AchievementsEnum.AFK8);
        }
        if (playtime >= 60 * 60 * 48) {
            getCp().unlockAchievement(AchievementsEnum.AFK9);
        }
        if (playtime >= 60 * 60 * 24 * 7) {
            getCp().unlockAchievement(AchievementsEnum.AFK10);
        }
    }

    public void addOneClick() {
        getGeneralIntegerStats().put("cookie", getGeneralIntegerStats().get("cookie") + 1);
    }

    public void addOneClickableClicked() {
        getGeneralIntegerStats().put("clickablesClicked", getGeneralIntegerStats().get("clickablesClicked") + 1);
    }

    public void addMoney(BigDecimal amount) {
        getGeneralBigDecimals().put("money", getGeneralBigDecimals().get("money").add(amount));
        getGeneralBigDecimals().put("legacy earnings", getGeneralBigDecimals().get("legacy earnings").add(amount));

        getGeneralBigDecimals().put("alltimeearnings", getGeneralBigDecimals().get("alltimeearnings").add(amount));
        possiblyUnlockMoneyManAchievement();
    }

    private void possiblyUnlockMoneyManAchievement() {

        BigDecimal bankvalue = getGeneralBigDecimals().get("alltimeearnings");

        if (bankvalue.compareTo(BigDecimal.valueOf(100)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN1);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN2);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN3);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(100_000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN4);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(100_000_000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN5);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(100_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN6);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(100_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN7);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(100_000_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN8);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(100_000_000_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN9);
        }
        if (bankvalue.compareTo(BigDecimal.valueOf(100_000_000_000_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN10);
        }
    }

    public void removeMoney(BigDecimal amount) {
        getGeneralBigDecimals().put("money", getGeneralBigDecimals().get("money").subtract(amount));
    }

    public void loadEverythingFromFile() {//always do this async
        fm.getPlayerFile(cp);
        loadSettings();
        loadBuildingsOwned();
        loadBuildingsMultipliers();
        loadBuildingsCostMultipliers(); // buildings.getname + PriceMultiplier
        loadBuildingsBaseCostIncreaser(); // buildings.getname + BaseIncreaser
        loadUpgradesUnlocked();
        loadGeneralIntegerStats();
        loadGeneralDoubleStats();
        loadGeneralBigDecimalStats();
        loadAchievements();
        loadPrestigeBonusses();
    }

    public void saveEverythingToFile() { // always do this async
        fm.getPlayerFile(cp);
        saveSettings();
        saveBuildingsOwned();
        saveBuildingsMultipliers();
        saveBuildingsCostMultipliers();
        saveBuildingsBaseCostIncreaser();
        saveUpgradesUnlocked();
        saveGeneralIntegerStats();
        saveGeneralDoubleStats();
        saveGeneralBigDecimalStats();
        saveAchievements();
        savePrestigeBonusses();
        fm.savePlayerFile(cp);
    }

    private void loadPrestigeBonusses() {
        for (PrestigeEnum prestigeEnum : PrestigeEnum.values()) {
            getPrestigeShopBonusses().put(prestigeEnum, fm.getIntByKey(getCp(), prestigeEnum.getKey()));
        }
    }

    private void savePrestigeBonusses() {
        for (Map.Entry<PrestigeEnum, Integer> entry : getPrestigeShopBonusses().entrySet()) {
            fm.setIntByKey(getCp(), entry.getKey().getKey(), entry.getValue());
        }
    }

    public Map<String, Boolean> getAchievements() {
        return achievements;
    }

    private void loadAchievements() {
        for (AchievementsEnum achievementsEnum : AchievementsEnum.values()) {
            getAchievements().put(achievementsEnum.getAchievement(), fm.getBooleanByKey(getCp(), achievementsEnum.getAchievement()));
        }
    }

    private void saveAchievements() {
        for (Map.Entry<String, Boolean> entry : getAchievements().entrySet()) {
            fm.setBooleanByKey(getCp(), entry.getKey(), entry.getValue());
        }
    }

    private void loadSettings() {
        ClickingPlayerSettings settings = getCp().getSettings();
        settings.setLoreDarkMode(fm.getBooleanByKey(getCp(), "loreDarkMode"));
        settings.setInventoryDarkMode(fm.getBooleanByKey(getCp(), "inventoryDarkMode"));
        settings.setDoChatMessages(fm.getBooleanByKey(getCp(), "doChats"));
        settings.setDoFirework(fm.getBooleanByKey(getCp(), "doFireworks"));
        settings.setDoSoundEffects(fm.getBooleanByKey(getCp(), "doSounds"));
    }

    private void saveSettings() {
        ClickingPlayerSettings settings = getCp().getSettings();
        fm.setBooleanByKey(getCp(), "loreDarkMode", settings.isLoreDarkMode());
        fm.setBooleanByKey(getCp(), "inventoryDarkMode", settings.isInventoryDarkMode());
        fm.setBooleanByKey(getCp(), "doChats", settings.isDoChatMessages());
        fm.setBooleanByKey(getCp(), "doFireworks", settings.isDoFirework());
        fm.setBooleanByKey(getCp(), "doSounds", settings.isDoSoundEffects());
    }

    public void loadUpgradesUnlocked() {
        //todo expand with based on achievements
        for (UpgradesBasedOnStats upgradesBasedOnStats : UpgradesBasedOnStats.values()) {
            getUpgradesUnlocked().put(upgradesBasedOnStats.getUnlockedKey(), fm.getBooleanByKey(cp, upgradesBasedOnStats.getUnlockedKey()));
        }
        for (UpgradesBasedOnBuildings upgradesBasedOnBuildings : UpgradesBasedOnBuildings.values()) {
            getUpgradesUnlocked().put(upgradesBasedOnBuildings.getUnlockedKey(), fm.getBooleanByKey(cp, upgradesBasedOnBuildings.getUnlockedKey()));
        }
    }

    public void saveUpgradesUnlocked() {
        for (Map.Entry<String, Boolean> entry : getUpgradesUnlocked().entrySet()) {
            fm.setBooleanByKey(getCp(), entry.getKey(), entry.getValue());
        }
    }

    public void loadGeneralDoubleStats() {
        getGeneralDoubleStats().put("Click Multiplier", Math.max(fm.getDoubleByKey(getCp(), "Click Multiplier"), 1));
        getGeneralDoubleStats().put("earnedByClicking", fm.getDoubleByKey(getCp(), "earnedByClicking"));
        getGeneralDoubleStats().put("eventChance", Math.max(fm.getDoubleByKey(getCp(), "eventChance"), 0.17));
    }

    public void saveGeneralDoubleStats() {
        for (Map.Entry<String, Double> entry : getGeneralDoubleStats().entrySet()) {
            fm.setDoubleByKey(getCp(), entry.getKey(), entry.getValue());
        }
    }

    public void loadGeneralBigDecimalStats() {
        getGeneralBigDecimals().put("money", fm.getBigDecimalByKey(getCp(), "money"));
        getGeneralBigDecimals().put("legacy earnings", fm.getBigDecimalByKey(getCp(), "legacy earnings"));
        getGeneralBigDecimals().put("alltimeearnings", fm.getBigDecimalByKey(getCp(), "alltimeearnings"));
    }

    public void saveGeneralBigDecimalStats() {
        for (Map.Entry<String, BigDecimal> entry : getGeneralBigDecimals().entrySet()) {
            fm.setBigDecimalByKey(getCp(), entry.getKey(), entry.getValue());
        }
    }

    public void loadGeneralIntegerStats() {
        getGeneralIntegerStats().put("cookie", fm.getIntByKey(getCp(), "cookie"));
        getGeneralIntegerStats().put("playtime", fm.getIntByKey(getCp(), "playtime"));

        getGeneralIntegerStats().put("Prestige Playtime", fm.getIntByKey(getCp(), "Prestige Playtime"));
        getGeneralIntegerStats().put("clickablesClicked", fm.getIntByKey(getCp(), "clickablesClicked"));

        getGeneralIntegerStats().put("Prestige level", fm.getIntByKey(getCp(), "Prestige level"));
        getGeneralIntegerStats().put("prestige coins", fm.getIntByKey(getCp(), "prestige coins"));

    }

    public void saveGeneralIntegerStats() {
        for (Map.Entry<String, Integer> entry : getGeneralIntegerStats().entrySet()) {
            fm.setDoubleByKey(getCp(), entry.getKey(), entry.getValue());
        }
    }

    private void loadBuildingsBaseCostIncreaser() {
        for (Buildings b : Buildings.values()) {
            getBuildingsBaseCostIncreaser().put(b, fm.getDoubleByKey(getCp(), b.getName() + "BaseIncreaser"));
        }
    }

    private void saveBuildingsBaseCostIncreaser() {
        for (Buildings b : Buildings.values()) {
            fm.setDoubleByKey(getCp(), b.getName() + "BaseIncreaser", getSpecificBuildingsBaseIncreaser(b));
        }
    }

    private void loadBuildingsCostMultipliers() {
        for (Buildings b : Buildings.values()) {
            getBuildingsBaseCostMultipliers().put(b, Math.max(fm.getDoubleByKey(getCp(), b.getName() + "PriceMultiplier"), 1));
        }
    }

    private void saveBuildingsCostMultipliers() {
        for (Buildings b : Buildings.values()) {
            fm.setDoubleByKey(getCp(), b.getName() + "PriceMultiplier", getSpecificBuildingsCostMultiplier(b));
        }
    }

    private void loadBuildingsMultipliers() {
        for (Buildings b : Buildings.values()) {
            //if it's a new file, it'll return 0, so then mathMax makes it 1 by default
            getBuildingsBaseMultipliers().put(b, Math.max(fm.getDoubleByKey(getCp(), b.getName() + "BaseMultiplier"), 1));
        }
    }

    private void saveBuildingsMultipliers() {
        for (Buildings b : Buildings.values()) {
            fm.setDoubleByKey(getCp(), b.getName() + "BaseMultiplier", getSpecificBuildingsBaseMultiplier(b));
        }
    }

    public double getSpecificBuildingsBaseMultiplier(Buildings b) {
        return getBuildingsBaseMultipliers().get(b);
    }

    public double getSpecificBuildingsCostMultiplier(Buildings b) {
        return getBuildingsBaseCostMultipliers().get(b);
    }

    public double getSpecificBuildingsBaseIncreaser(Buildings b) {
        return getBuildingsBaseCostIncreaser().get(b);
    }

    private void loadBuildingsOwned() {
        for (Buildings b : Buildings.values()) {
            getBuildingsOwned().put(b, fm.getIntByKey(getCp(), b.getName()));
        }
    }

    private void saveBuildingsOwned() {
        for (Buildings b : Buildings.values()) {
            fm.setIntByKey(getCp(), b.getName(), getSpecificBuildingAmount(b));
        }
    }

    public int getSpecificBuildingAmount(Buildings b) {
        return getBuildingsOwned().get(b);
    }

    private int calculatePrestigeLevels() {
        BigDecimal trillions = getGeneralBigDecimals().get("alltimeearnings").divide(BigDecimal.valueOf(1_000_000_000_000_000D), 5, RoundingMode.HALF_DOWN);
        return RootCalculus.nthRoot(3, trillions).intValue();
    }

    public int calculateAvailablePrestigeLevels() {
        return calculatePrestigeLevels() - getCurrentPrestigeLevels();
    }

    public int getCurrentPrestigeLevels() {
        return getGeneralIntegerStats().get("Prestige level");
    }

    public int getCurrentPrestigeCoins() {
        return getGeneralIntegerStats().get("prestige coins");
    }

    private void setCurrentPrestigeCoins(int value) {
        getGeneralIntegerStats().put("prestige coins", value);
    }

    public void payPrestigeCoins(int value) {
        setCurrentPrestigeCoins(getCurrentPrestigeCoins() - value);
    }

    private void resetBuildings() {
        this.buildingsOwned.replaceAll((k, v) -> 0);
        getBuildingsOwned().put(Buildings.MOM, (int) STARTWITHMOMS.getCurrentBonus(getCp()));
        getBuildingsOwned().put(Buildings.CHEF, (int) STARTWITHCHEFS.getCurrentBonus(getCp()));

    }

    private void resetBuildingMultipliers() {
        this.buildingsBaseMultipliers.replaceAll((k, v) -> 1D);
    }

    private void resetStats() {
        getGeneralBigDecimals().put("money", BigDecimal.valueOf(0));
        getGeneralBigDecimals().put("legacy earnings", BigDecimal.valueOf(0));


        getGeneralDoubleStats().put("Click Multiplier", 1D);
        getGeneralDoubleStats().put("earnedByClicking", 0D);
        getGeneralDoubleStats().put("eventChance", 0.17 + STARTWITHEVENCHANCE.getCurrentBonus(getCp()));

        getGeneralIntegerStats().put("Prestige Playtime", 0);
        getGeneralIntegerStats().put("cookie", 0);
        getGeneralIntegerStats().put("clickablesClicked", 0);


    }

    private void resetUpgrades() {
        this.upgradesUnlocked.replaceAll((k, v) -> false);
    }

    public boolean canPrestige() {
        return (calculateAvailablePrestigeLevels() >= 1);
    }

    public void doPrestige() {
        int earningAmount = calculateAvailablePrestigeLevels();
        getGeneralIntegerStats().put("Prestige level", getCurrentPrestigeLevels() + earningAmount);
        getGeneralIntegerStats().put("prestige coins", getCurrentPrestigeCoins() + earningAmount);

        resetUpgrades();
        resetBuildingMultipliers();
        resetBuildings();
        resetStats();
        getCp().setIncomePerSecond(getCp().calculateTotalIncomePerSecond());
    }
}
