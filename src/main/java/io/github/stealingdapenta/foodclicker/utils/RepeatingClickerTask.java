package io.github.stealingdapenta.foodclicker.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RepeatingClickerTask implements Runnable {

    private int taskID;

    public RepeatingClickerTask(JavaPlugin plugin, long delay, long period) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
