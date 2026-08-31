package io.github.stealingdapenta.foodclicker.clickingplayers;

import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHCHEFS;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHEVENCHANCE;
import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.STARTWITHMOMS;

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
import org.bukkit.configuration.file.YamlConfiguration;

public class ClickingPlayerData {

    private final FileManager fm;
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
        this(cp, FileManager.getInstance());
    }

    ClickingPlayerData(ClickingPlayer cp, FileManager fm) {
        this.cp = cp;
        this.fm = fm;

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

    public Map<Buildings, Integer> getBuildingsOwned() {
        return buildingsOwned;
    }

    public Map<Buildings, Double> getBuildingsBaseMultipliers() {
        return buildingsBaseMultipliers;
    }

    public Map<Buildings, Double> getBuildingsBaseCostMultipliers() {
        return buildingsBaseCostMultipliers;
    }

    public Map<Buildings, Double> getBuildingsBaseCostIncreaser() {
        return buildingsBaseCostIncreaser;
    }

    public Map<String, Boolean> getUpgradesUnlocked() {
        return upgradesUnlocked;
    }

    public Map<String, Integer> getGeneralIntegerStats() {
        return generalIntegerStats;
    }

    public Map<String, Double> getGeneralDoubleStats() {
        return generalDoubleStats;
    }

    public Map<String, BigDecimal> getGeneralBigDecimals() {
        return generalBigDecimals;
    }

    public Map<String, Boolean> getAchievements() {
        return achievements;
    }

    public Map<PrestigeEnum, Integer> getPrestigeShopBonusses() {
        return prestigeShopBonusses;
    }

    public ClickingPlayer getCp() {
        return cp;
    }

    public boolean isPrestigeHead() {
        return prestigeHead;
    }

    public long loadGameCloseDate() {
        return fm.getLongByKey(getCp(), "gameCloseDate");
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

    public void doPrestigeHead() {
        this.prestigeHead = !isPrestigeHead();
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

        BigDecimal bankValue = getGeneralBigDecimals().get("alltimeearnings");

        if (bankValue.compareTo(BigDecimal.valueOf(100)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN1);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN2);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN3);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(100_000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN4);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(100_000_000)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN5);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(100_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN6);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(100_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN7);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(100_000_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN8);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(100_000_000_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN9);
        }
        if (bankValue.compareTo(BigDecimal.valueOf(100_000_000_000_000_000_000_000D)) >= 0) {
            getCp().unlockAchievement(AchievementsEnum.MONEYMAN10);
        }
    }

    public void removeMoney(BigDecimal amount) {
        getGeneralBigDecimals().put("money", getGeneralBigDecimals().get("money").subtract(amount));
    }

    public void loadEverythingFromFile() {
        YamlConfiguration config = fm.getConfig(cp);
        loadSettings(config);
        loadBuildingsOwned(config);
        loadBuildingsMultipliers(config);
        loadBuildingsCostMultipliers(config);
        loadBuildingsBaseCostIncreaser(config);
        loadUpgradesUnlocked(config);
        loadGeneralIntegerStats(config);
        loadGeneralDoubleStats(config);
        loadGeneralBigDecimalStats(config);
        loadAchievements(config);
        loadPrestigeBonuses(config);
    }

    public void saveEverythingToFile() {
        YamlConfiguration config = fm.getConfig(cp);
        saveSettings(config);
        saveBuildingsOwned(config);
        saveBuildingsMultipliers(config);
        saveBuildingsCostMultipliers(config);
        saveBuildingsBaseCostIncreaser(config);
        saveUpgradesUnlocked(config);
        saveGeneralIntegerStats(config);
        saveGeneralDoubleStats(config);
        saveGeneralBigDecimalStats(config);
        saveAchievements(config);
        savePrestigeBonuses(config);
        fm.saveConfig(cp, config);
    }

    private void loadPrestigeBonuses(YamlConfiguration config) {
        for (PrestigeEnum prestigeEnum : PrestigeEnum.values()) {
            getPrestigeShopBonusses().put(prestigeEnum, config.getInt(prestigeEnum.getKey()));
        }
    }

    private void savePrestigeBonuses(YamlConfiguration config) {
        for (Map.Entry<PrestigeEnum, Integer> entry : getPrestigeShopBonusses().entrySet()) {
            config.set(entry.getKey().getKey(), entry.getValue());
        }
    }

    private void loadAchievements(YamlConfiguration config) {
        for (AchievementsEnum achievementsEnum : AchievementsEnum.values()) {
            getAchievements().put(achievementsEnum.getAchievement(), config.getBoolean(achievementsEnum.getAchievement()));
        }
    }

    private void saveAchievements(YamlConfiguration config) {
        for (Map.Entry<String, Boolean> entry : getAchievements().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }

    private void loadSettings(YamlConfiguration config) {
        ClickingPlayerSettings settings = getCp().getSettings();
        settings.setLoreDarkMode(config.getBoolean("loreDarkMode"));
        settings.setInventoryDarkMode(config.getBoolean("inventoryDarkMode"));
        settings.setDoChatMessages(config.getBoolean("doChats"));
        settings.setDoFirework(config.getBoolean("doFireworks"));
        settings.setDoSoundEffects(config.getBoolean("doSounds"));
        settings.setDoBigNumbers(config.getBoolean("doBigNumbers"));
    }

    private void saveSettings(YamlConfiguration config) {
        ClickingPlayerSettings settings = getCp().getSettings();
        config.set("loreDarkMode", settings.isLoreDarkMode());
        config.set("inventoryDarkMode", settings.isInventoryDarkMode());
        config.set("doChats", settings.isDoChatMessages());
        config.set("doFireworks", settings.isDoFirework());
        config.set("doSounds", settings.isDoSoundEffects());
        config.set("doBigNumbers", settings.isDoBigNumbers());
    }

    public void loadUpgradesUnlocked() {
        loadUpgradesUnlocked(fm.getConfig(cp));
    }

    private void loadUpgradesUnlocked(YamlConfiguration config) {
        //todo expand with based on achievements
        for (UpgradesBasedOnStats upgradesBasedOnStats : UpgradesBasedOnStats.values()) {
            getUpgradesUnlocked().put(upgradesBasedOnStats.getUnlockedKey(), config.getBoolean(upgradesBasedOnStats.getUnlockedKey()));
        }
        for (UpgradesBasedOnBuildings upgradesBasedOnBuildings : UpgradesBasedOnBuildings.values()) {
            getUpgradesUnlocked().put(upgradesBasedOnBuildings.getUnlockedKey(), config.getBoolean(upgradesBasedOnBuildings.getUnlockedKey()));
        }
    }

    public void saveUpgradesUnlocked() {
        YamlConfiguration config = fm.getConfig(cp);
        saveUpgradesUnlocked(config);
        fm.saveConfig(cp, config);
    }

    private void saveUpgradesUnlocked(YamlConfiguration config) {
        for (Map.Entry<String, Boolean> entry : getUpgradesUnlocked().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }

    public void loadGeneralDoubleStats() {
        loadGeneralDoubleStats(fm.getConfig(cp));
    }

    private void loadGeneralDoubleStats(YamlConfiguration config) {
        getGeneralDoubleStats().put("Click Multiplier", Math.max(config.getDouble("Click Multiplier"), 1));
        getGeneralDoubleStats().put("earnedByClicking", config.getDouble("earnedByClicking"));
        getGeneralDoubleStats().put("eventChance", Math.max(config.getDouble("eventChance"), 0.17));
    }

    public void saveGeneralDoubleStats() {
        YamlConfiguration config = fm.getConfig(cp);
        saveGeneralDoubleStats(config);
        fm.saveConfig(cp, config);
    }

    private void saveGeneralDoubleStats(YamlConfiguration config) {
        for (Map.Entry<String, Double> entry : getGeneralDoubleStats().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }

    public void loadGeneralBigDecimalStats() {
        loadGeneralBigDecimalStats(fm.getConfig(cp));
    }

    private void loadGeneralBigDecimalStats(YamlConfiguration config) {
        getGeneralBigDecimals().put("money", fm.getBigDecimalByKey(config, "money"));
        getGeneralBigDecimals().put("legacy earnings", fm.getBigDecimalByKey(config, "legacy earnings"));
        getGeneralBigDecimals().put("alltimeearnings", fm.getBigDecimalByKey(config, "alltimeearnings"));
    }

    public void saveGeneralBigDecimalStats() {
        YamlConfiguration config = fm.getConfig(cp);
        saveGeneralBigDecimalStats(config);
        fm.saveConfig(cp, config);
    }

    private void saveGeneralBigDecimalStats(YamlConfiguration config) {
        for (Map.Entry<String, BigDecimal> entry : getGeneralBigDecimals().entrySet()) {
            config.set(entry.getKey(), entry.getValue().toPlainString());
        }
    }

    public void loadGeneralIntegerStats() {
        loadGeneralIntegerStats(fm.getConfig(cp));
    }

    private void loadGeneralIntegerStats(YamlConfiguration config) {
        getGeneralIntegerStats().put("cookie", config.getInt("cookie"));
        getGeneralIntegerStats().put("playtime", config.getInt("playtime"));

        getGeneralIntegerStats().put("Prestige Playtime", config.getInt("Prestige Playtime"));
        getGeneralIntegerStats().put("clickablesClicked", config.getInt("clickablesClicked"));

        getGeneralIntegerStats().put("Prestige level", config.getInt("Prestige level"));
        getGeneralIntegerStats().put("prestige coins", config.getInt("prestige coins"));
    }

    public void saveGeneralIntegerStats() {
        YamlConfiguration config = fm.getConfig(cp);
        saveGeneralIntegerStats(config);
        fm.saveConfig(cp, config);
    }

    private void saveGeneralIntegerStats(YamlConfiguration config) {
        for (Map.Entry<String, Integer> entry : getGeneralIntegerStats().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }

    private void loadBuildingsBaseCostIncreaser(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            getBuildingsBaseCostIncreaser().put(b, config.getDouble(b.getName() + "BaseIncreaser"));
        }
    }

    private void saveBuildingsBaseCostIncreaser(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            config.set(b.getName() + "BaseIncreaser", getSpecificBuildingsBaseIncreaser(b));
        }
    }

    private void loadBuildingsCostMultipliers(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            getBuildingsBaseCostMultipliers().put(b, Math.max(config.getDouble(b.getName() + "PriceMultiplier"), 1));
        }
    }

    private void saveBuildingsCostMultipliers(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            config.set(b.getName() + "PriceMultiplier", getSpecificBuildingsCostMultiplier(b));
        }
    }

    private void loadBuildingsMultipliers(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            //if it's a new file, it'll return 0, so then mathMax makes it 1 by default
            getBuildingsBaseMultipliers().put(b, Math.max(config.getDouble(b.getName() + "BaseMultiplier"), 1));
        }
    }

    private void saveBuildingsMultipliers(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            config.set(b.getName() + "BaseMultiplier", getSpecificBuildingsBaseMultiplier(b));
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

    private void loadBuildingsOwned(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            getBuildingsOwned().put(b, config.getInt(b.getName()));
        }
    }

    private void saveBuildingsOwned(YamlConfiguration config) {
        for (Buildings b : Buildings.values()) {
            config.set(b.getName(), getSpecificBuildingAmount(b));
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
