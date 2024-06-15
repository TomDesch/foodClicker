package io.github.stealingdapenta.foodclicker;

import io.github.stealingdapenta.foodclicker.commands.FireworkCommand;
import io.github.stealingdapenta.foodclicker.commands.FoodClickerCommand;
import io.github.stealingdapenta.foodclicker.commands.StatsCommand;
import io.github.stealingdapenta.foodclicker.commands.TopCommand;
import io.github.stealingdapenta.foodclicker.utils.Listeners;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class FoodClicker extends JavaPlugin {

    private static FoodClicker instance = null;

    public static Logger LOGGER;

    public static FoodClicker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        LOGGER = getInstance().getLogger();

        Objects.requireNonNull(this.getCommand("foodclicker"))
               .setExecutor(new FoodClickerCommand());
        Objects.requireNonNull(this.getCommand("fcstats"))
               .setExecutor(new StatsCommand());
        Objects.requireNonNull(this.getCommand("fcfirework"))
               .setExecutor(new FireworkCommand());
        Objects.requireNonNull(this.getCommand("fctop"))
               .setExecutor(new TopCommand());

        Bukkit.getPluginManager()
              .registerEvents(new Listeners(), this);

        LOGGER.info("FoodClicker enabled.");
        LOGGER.info(ChatColor.BLUE + "FoodClicker IDLE GAME is now enabled!");
        LOGGER.info(ChatColor.BLUE + "Have fun breaking your mouse!");
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("FoodClicker: Thank you for playing. Goodbye!");
        getLogger().info("FoodClicker: Plugin disabled.");
    }

}
