package io.github.stealingdapenta.foodclicker.commands;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.FileManager;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

final class PlayerSnapshots {

    private PlayerSnapshots() {
    }

    static void read(FoodClicker plugin, Player player, Consumer<YamlConfiguration> action) {
        InventoryManager inventories = InventoryManager.getInstance();
        UUID id = player.getUniqueId();
        ClickingPlayer session = inventories.getClickingPlayerMap().get(id);
        if (session != null && session.getPlayer() != player) {
            player.sendMessage("FoodClicker: Your previous session is awaiting recovery. Use /foodclicker to retry.");
            return;
        }
        if (session != null && session.isReady()) {
            try {
                YamlConfiguration snapshot = new YamlConfiguration();
                snapshot.loadFromString(session.getData().snapshot(player.getName()));
                action.accept(snapshot);
            } catch (Exception failure) {
                report(plugin, player, failure);
            }
            return;
        }
        FileManager files = FileManager.getInstance();
        plugin.getPersistence().submit(() -> files.read(id)).whenComplete((config, failure) ->
                plugin.onServerThread(() -> {
                    if (!player.isOnline() || inventories.getClickingPlayerMap().get(id) != session) {
                        return;
                    }
                    if (failure != null) {
                        report(plugin, player, failure);
                    } else {
                        action.accept(config);
                    }
                }));
    }

    private static void report(FoodClicker plugin, Player player, Throwable failure) {
        plugin.getLogger().log(Level.WARNING, "Unable to read FoodClicker statistics", failure);
        player.sendMessage("FoodClicker: Unable to read your data. It has not been changed.");
    }
}
