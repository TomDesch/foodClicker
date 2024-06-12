package io.github.stealingdapenta.foodclicker;

import io.github.stealingdapenta.foodclicker.commands.FireworkCommand;
import io.github.stealingdapenta.foodclicker.commands.FoodClickerCommand;
import io.github.stealingdapenta.foodclicker.commands.StatsCommand;
import io.github.stealingdapenta.foodclicker.commands.TopCommand;
import io.github.stealingdapenta.foodclicker.utils.Listeners;
import io.github.stealingdapenta.foodclicker.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FoodClicker extends JavaPlugin {
    private static FoodClicker instance = null;
    private static String version;

    public static String getVersion() {
        return version;
    }

    public static FoodClicker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Logger logger = getInstance().getLogger();

        this.getCommand("foodclicker").setExecutor(new FoodClickerCommand());
        this.getCommand("fcstats").setExecutor(new StatsCommand());
        this.getCommand("fcfirework").setExecutor(new FireworkCommand());
        this.getCommand("fctop").setExecutor(new TopCommand());


        Bukkit.getPluginManager().registerEvents(new Listeners(), this);

        logger.info("FoodClicker enabled.");
        logger.info(ChatColor.BLUE + "FoodClicker IDLE GAME is now enabled!");
        logger.info(ChatColor.BLUE + "Have fun breaking your mouse!");

        new UpdateChecker(getInstance(), 87531).getFoodClickerVersion(version -> {
            if (getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("FoodClicker: Your FoodClicker is up to date :)");
            } else {
                logger.info("FoodClicker: There is a newer version available on Spigot! Update now to get all the latest features.");
            }
        });
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("FoodClicker: Thank you for playing. Goodbye!");
        getLogger().info("FoodClicker: Plugin disabled.");
    }

}
