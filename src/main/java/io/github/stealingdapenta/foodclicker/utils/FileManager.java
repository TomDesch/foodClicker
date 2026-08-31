package io.github.stealingdapenta.foodclicker.utils;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.function.Supplier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class FileManager {

    private static FileManager fileManager;
    private final Supplier<File> dataFolderSupplier;

    private FileManager() {
        this(() -> FoodClicker.getInstance().getDataFolder());
    }

    FileManager(Supplier<File> dataFolderSupplier) {
        this.dataFolderSupplier = dataFolderSupplier;
    }

    public static FileManager getInstance() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager;
    }

    public double getDoubleByKey(ClickingPlayer cp, String key) {
        if (getConfig(cp).getDouble(key) == 0) {
            //if it doesnt exist, it will also return 0
            //therefor setting it to 0 will create the key if it doesnt exist
            setDoubleByKey(cp, key, 0D);
        }
        return getConfig(cp).getDouble(key);
    }

    public BigDecimal getBigDecimalByKey(ClickingPlayer cp, String key) {
        return getBigDecimalByKey(getConfig(cp), key);
    }

    public BigDecimal getBigDecimalByKey(YamlConfiguration config, String key) {
        String bigString = config.getString(key, "0");
        try {
            return new BigDecimal(bigString);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid decimal value for player data key '" + key + "'", e);
        }
    }

    public void setBigDecimalByKey(ClickingPlayer cp, String key, BigDecimal score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score.toPlainString());
        saveConfig(c, getPlayerFile(cp));
    }

    public int getIntByKey(ClickingPlayer cp, String key) {
        return getIntByKey(cp.getPlayer(), key);
    }

    public int getIntByKey(Player cp, String key) {
        if (getConfig(cp).getInt(key) == 0) {
            setIntByKey(cp, key, 0);
        }
        return getConfig(cp).getInt(key);
    }

    public long getLongByKey(ClickingPlayer cp, String key) {
        return getLongByKey(cp.getPlayer(), key);
    }

    public long getLongByKey(Player cp, String key) {
        if (getConfig(cp).getLong(key) == 0) {
            setLongByKey(cp, key, 0);
        }
        return getConfig(cp).getLong(key);
    }

    public boolean getBooleanByKey(ClickingPlayer cp, String key) {
        return getConfig(cp).getBoolean(key);
    }

    public void setDoubleByKey(ClickingPlayer cp, String key, double score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score);
        saveConfig(c, getPlayerFile(cp));
    }

    public void setLongByKey(ClickingPlayer cp, String key, long score) {
        setLongByKey(cp.getPlayer(), key, score);
    }

    public void setLongByKey(Player cp, String key, long score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score);
        saveConfig(c, getPlayerFile(cp));
    }

    public void setIntByKey(ClickingPlayer cp, String key, int score) {
        setIntByKey(cp.getPlayer(), key, score);
    }

    public void setIntByKey(Player cp, String key, int score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score);
        saveConfig(c, getPlayerFile(cp));
    }

    public void setBooleanByKey(ClickingPlayer cp, String key, boolean score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score);
        saveConfig(c, getPlayerFile(cp));
    }

    public YamlConfiguration getConfig(ClickingPlayer cp) {
        return getConfig(cp.getPlayer());
    }

    public YamlConfiguration getConfig(Player cp) {
        return YamlConfiguration.loadConfiguration(getPlayerFile(cp));
    }

    public void saveConfig(ClickingPlayer cp, YamlConfiguration config) {
        saveConfig(config, getPlayerFile(cp));
    }

    public void createFile(Player cp) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("Player Name", cp.getName());
        saveConfig(config, playerFilePath(cp));
    }

    public File getPlayerFile(ClickingPlayer cp) {
        return getPlayerFile(cp.getPlayer());
    }

    public File getPlayerFile(Player cp) {
        File file = playerFilePath(cp);

        if (!file.exists()) {
            createFile(cp);
        }

        return file;
    }

    public File getUserFiles() {
        File userFiles = new File(dataFolderSupplier.get(), "foodclicker");
        if (!userFiles.isDirectory() && !userFiles.mkdirs()) {
            throw new IllegalStateException("Unable to create player data directory: " + userFiles);
        }
        return userFiles;
    }

    public void savePlayerFile(ClickingPlayer cp) {
        savePlayerFile(cp.getPlayer());
    }

    public void savePlayerFile(Player cp) {
        File file = playerFilePath(cp);

        if (!file.exists()) {
            createFile(cp);
            return;
        }
        saveConfig(YamlConfiguration.loadConfiguration(file), file);
    }

    private File playerFilePath(Player player) {
        return new File(getUserFiles(), player.getUniqueId() + ".yml");
    }

    private void saveConfig(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to save player data file: " + file, e);
        }
    }
}
