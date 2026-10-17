package io.github.stealingdapenta.foodclicker;

import io.github.stealingdapenta.foodclicker.commands.FireworkCommand;
import io.github.stealingdapenta.foodclicker.commands.FoodClickerCommand;
import io.github.stealingdapenta.foodclicker.commands.StatsCommand;
import io.github.stealingdapenta.foodclicker.commands.TopCommand;
import io.github.stealingdapenta.foodclicker.clickingplayers.SessionShutdown;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.Listeners;
import io.github.stealingdapenta.foodclicker.utils.PlayerPersistence;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.IllegalPluginAccessException;

public class FoodClicker extends JavaPlugin {

    private static FoodClicker instance = null;

    public static Logger LOGGER;
    private PlayerPersistence persistence;
    private volatile boolean stopping;

    public PlayerPersistence getPersistence() {
        return persistence;
    }

    public void onServerThread(Runnable action) {
        if (stopping) {
            return;
        }
        try {
            Bukkit.getScheduler().runTask(this, () -> {
                if (!stopping) {
                    action.run();
                }
            });
        } catch (IllegalPluginAccessException failure) {
            if (!stopping) {
                getLogger().log(Level.SEVERE, "Unable to deliver player data result", failure);
            }
        }
    }

    public static FoodClicker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        LOGGER = getInstance().getLogger();
        persistence = new PlayerPersistence();

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
        InventoryManager inventoryManager = InventoryManager.getInstance();
        stopping = true;
        if (persistence != null) {
            persistence.beginShutdown(Duration.ofSeconds(30));
        }
        SessionShutdown.shutdownSessions(new ArrayList<>(inventoryManager.getClickingPlayerMap().values()))
                       .forEach(result -> result.whenComplete((ignored, failure) -> {
                           if (failure != null) {
                               getLogger().log(Level.SEVERE, "Unable to save a FoodClicker session during shutdown", failure);
                           }
                       }));
        if (persistence != null && !persistence.drain(Duration.ofSeconds(30))) {
            getLogger().severe("Player data queue did not finish within 30 seconds; pending writes are still running.");
        }
        inventoryManager.getClickingPlayerMap().clear();
        getLogger().info("FoodClicker: Thank you for playing. Goodbye!");
        getLogger().info("FoodClicker: Plugin disabled.");
        instance = null;
    }

}
