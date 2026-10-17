package io.github.stealingdapenta.foodclicker.commands;

import static io.github.stealingdapenta.foodclicker.utils.ItemBuilder.colorStatic;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum;
import io.github.stealingdapenta.foodclicker.utils.FileManager;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {

    private final AntiSpamManager asm = AntiSpamManager.getInstance();

    record Entry(UUID id, String name, BigDecimal earnings) {
    }

    static Map<String, BigDecimal> sortByValue(Map<String, BigDecimal> values) {
        return values.entrySet().stream()
                     .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                               (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    static List<Entry> readLeaderboard(FileManager files, Map<UUID, String> currentNames, Consumer<String> invalid) {
        File[] playerFiles = files.getUserFiles().listFiles((directory, name) -> name.endsWith(".yml"));
        if (playerFiles == null) {
            throw new IllegalStateException("Unable to list player files");
        }
        List<Entry> entries = new ArrayList<>();
        for (File file : playerFiles) {
            try {
                UUID id = UUID.fromString(file.getName().substring(0, file.getName().length() - 4));
                YamlConfiguration config = files.readFile(file);
                if (config.contains("alltimeearnings")) {
                    entries.add(new Entry(id, currentNames.getOrDefault(id, config.getString("Player Name", id.toString())),
                                          files.getBigDecimalByKey(config, "alltimeearnings")));
                }
            } catch (RuntimeException failure) {
                invalid.accept(file.getName());
            }
        }
        return entries.stream().sorted(Comparator.comparing(Entry::earnings).reversed().thenComparing(Entry::id)).toList();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("FoodClicker: This command is only available to players.");
            return true;
        }
        if (asm.playerIsSpamming(player)) {
            asm.sendSpamWarning(player);
            return true;
        }
        asm.addPossibleSpammer(player);
        FoodClicker plugin = FoodClicker.getInstance();
        PlayerSnapshots.read(plugin, player, config -> {
            if (config.getInt(PrestigeEnum.LOCALLEADERBOARD.getKey()) < 1) {
                player.sendMessage("FoodClicker: You have not unlocked this prestige command yet.");
                return;
            }
            Map<UUID, String> names = new HashMap<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                names.put(online.getUniqueId(), online.getName());
            }
            InventoryManager inventories = InventoryManager.getInstance();
            UUID requester = player.getUniqueId();
            ClickingPlayer session = inventories.getClickingPlayerMap().get(requester);
            FileManager files = FileManager.getInstance();
            plugin.getPersistence().submit(() -> readLeaderboard(files, names,
                    name -> plugin.getLogger().warning("Skipping invalid leaderboard record " + name)))
                  .whenComplete((entries, failure) -> plugin.onServerThread(() -> {
                      if (!player.isOnline() || inventories.getClickingPlayerMap().get(requester) != session) {
                          return;
                      }
                      if (failure != null) {
                          plugin.getLogger().warning("Unable to read leaderboard: " + failure.getMessage());
                          player.sendMessage("FoodClicker: Unable to read leaderboard data.");
                          return;
                      }
                      player.sendMessage(colorStatic("&6[&bFoodClicker&6] &eTop 10 all time earnings:"));
                      for (int index = 0; index < Math.min(10, entries.size()); index++) {
                          Entry entry = entries.get(index);
                          player.sendMessage(colorStatic("&6" + (index + 1) + ". &e" + entry.name() + " &6- &b"
                                  + inventories.truncateNumber(entry.earnings())));
                      }
                  }));
        });
        return true;
    }
}
