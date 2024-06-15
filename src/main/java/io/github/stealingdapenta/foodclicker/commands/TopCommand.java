package io.github.stealingdapenta.foodclicker.commands;

import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.LOCALLEADERBOARD;
import static io.github.stealingdapenta.foodclicker.utils.ItemBuilder.colorStatic;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.FileManager;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.io.File;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TopCommand implements CommandExecutor {
    private static final InventoryManager im = InventoryManager.getInstance();
    private static final FileManager fm = FileManager.getInstance();
    private final AntiSpamManager asm = AntiSpamManager.getInstance();


    private BigDecimal getBigDecimalMoney(YamlConfiguration config) {
        String bigString = config.getString("alltimeearnings");
        if (bigString == null || bigString.equals("0")) return new BigDecimal(0);
        return new BigDecimal(bigString);
    }

    private String getPlayerName(YamlConfiguration config) {
        return config.getString("Player Name");
    }

    private boolean containsNameAndMoney(YamlConfiguration config) {
        return (config.contains("Player Name") && config.contains("alltimeearnings"));
    }

    private Map<String, BigDecimal> sortByValue(Map<String, BigDecimal> unsortMap) {
        return unsortMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                  (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) return true;
        Player p = (Player) sender;
        ClickingPlayer cp;


        if (asm.playerIsSpamming(p)) {
            asm.sendSpamWarning(p);
            return true;
        }

        asm.addPossibleSpammer(p);

        if (im.isPlayerInMap(p)) {
            cp = im.getClickingPlayerMap().get(p);
        } else {
            cp = new ClickingPlayer(p);
        }

        p.sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &ePlease wait up to &a5s &ewhile we load the data."));

        Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> {
            if (LOCALLEADERBOARD.getAmountOwned(cp) > 0) {
                Map<String, BigDecimal> allData = new HashMap<>();

                File[] files = fm.getUserFiles().listFiles();

                if (files != null) {
                    for (File file : files) {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                        if (containsNameAndMoney(config)) {
                            allData.put(getPlayerName(config), getBigDecimalMoney(config));
                        }
                    }

                    Map<String, BigDecimal> sortedData = sortByValue(allData);

                    p.sendMessage(colorStatic("&6-------------------------------"));
                    p.sendMessage(colorStatic("&6[&bFoodClicker&6] &eTop 10 richest people:"));
                    p.sendMessage(colorStatic("&7&o          (All time earnings)"));

                    int index = 1;

                    for (Map.Entry<String, BigDecimal> entry : sortedData.entrySet()) {
                        p.sendMessage(colorStatic("&6" + index + ". &e" + entry.getKey() + " &6- &b" + im.truncateNumber(entry.getValue())));
                        index++;

                        if (index > 10) {
                            break;
                        }
                    }

                    p.sendMessage(colorStatic("&6-------------------------------"));
                } else {
                    p.sendMessage(colorStatic("&6[&bFoodClicker&6] &eYou have not unlocked this prestige command yet."));
                }

                cp.getRepeatingClickerTask().cancel();
            }

        }, 50L);


        return true;
    }
}