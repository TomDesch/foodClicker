package io.github.stealingdapenta.foodclicker.utils;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class FileManager {
    private static FileManager fileManager;

    private FileManager() {
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
        String bigString = getConfig(cp).getString(key, "0");
        if (bigString == null) return new BigDecimal(0);
        return new BigDecimal(bigString);
    }

    public void setBigDecimalByKey(ClickingPlayer cp, String key, BigDecimal score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score.toPlainString());
        try {
            c.save(getPlayerFile(cp));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            c.save(getPlayerFile(cp));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLongByKey(ClickingPlayer cp, String key, long score) {
        setLongByKey(cp.getPlayer(), key, score);
    }

    public void setLongByKey(Player cp, String key, long score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score);
        try {
            c.save(getPlayerFile(cp));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIntByKey(ClickingPlayer cp, String key, int score) {
        setIntByKey(cp.getPlayer(), key, score);
    }

    public void setIntByKey(Player cp, String key, int score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score);
        try {
            c.save(getPlayerFile(cp));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBooleanByKey(ClickingPlayer cp, String key, boolean score) {
        YamlConfiguration c = getConfig(cp);
        c.set(key, score);
        try {
            c.save(getPlayerFile(cp));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig(ClickingPlayer cp) {
        return getConfig(cp.getPlayer());
    }

    public YamlConfiguration getConfig(Player cp) {
        return YamlConfiguration.loadConfiguration(getPlayerFile(cp));
    }

    public void createFile(ClickingPlayer cp) {
        createFile(cp.getPlayer());
    }

    public void createFile(Player cp) {
        File file = new File(getUserFiles(), cp.getPlayer().getUniqueId().toString() + ".yml");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            file.createNewFile();

            writer = new PrintWriter(file);
            writer.println("Player Name: " + cp.getPlayer().getName());

            System.out.println("FoodClicker: " + cp.getPlayer().getName() + "'s FoodClicker file created.");
        } catch (IOException e1) {
            System.out.println(ChatColor.RED + "FoodClicker Player file creation failed!");
            e1.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public File getPlayerFile(ClickingPlayer cp) {
        return getPlayerFile(cp.getPlayer());
    }

    public File getPlayerFile(Player cp) {
        File file = new File(getUserFiles(), cp.getPlayer().getUniqueId().toString() + ".yml");

        if (!file.exists()) {
            createFile(cp);
            savePlayerFile(cp);
        }

        return file;
    }

    public File getUserFiles() {
        File userFiles = new File(FoodClicker.getInstance().getDataFolder() + File.separator + "foodclicker");
        if (!userFiles.exists()) {
            userFiles.mkdirs();
        }
        return userFiles;
    }

    public void savePlayerFile(ClickingPlayer cp) {
        savePlayerFile(cp.getPlayer());
    }

    public void savePlayerFile(Player cp) {
        String uuid = cp.getPlayer().getUniqueId().toString();
        File file = new File(getUserFiles(), uuid + ".yml");

        if (!file.exists()) {
            createFile(cp);
        }
        try {
            YamlConfiguration.loadConfiguration(file).save(file);
        } catch (IOException e) {
            System.out.println("foodclicker wtf @ saving file");
        }
    }
}
