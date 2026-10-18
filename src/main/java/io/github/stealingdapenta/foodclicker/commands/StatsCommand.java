package io.github.stealingdapenta.foodclicker.commands;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.basics.AchievementsEnum;
import io.github.stealingdapenta.foodclicker.basics.Buildings;
import io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnBuildings;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnStats;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private final AntiSpamManager asm = AntiSpamManager.getInstance();

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
        boolean prestige = args.length > 0 && Boolean.parseBoolean(args[0]);
        PlayerSnapshots.read(FoodClicker.getInstance(), player, config -> {
            if (prestige && config.getInt(PrestigeEnum.PRESTIGEBRAG.getKey()) < 1) {
                player.sendMessage("FoodClicker: You have not unlocked the Prestige Brag upgrade yet.");
                return;
            }
            TextComponent component = new TextComponent(ItemBuilder.colorStatic(
                    "&6[&bFoodClicker&6] &7Hover to see &b" + player.getDisplayName() + "&7's statistics!"));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(joinLore(statistics(config, prestige)))));
            Bukkit.getServer().spigot().broadcast(component);
        });
        return true;
    }

    static List<String> statistics(YamlConfiguration config, boolean prestige) {
        List<String> lines = new ArrayList<>();
        lines.add("Total playtime: " + config.getInt("playtime") + " seconds");
        lines.add("All time earnings: " + config.getString("alltimeearnings", "0"));
        lines.add("Prestige level: " + config.getInt("Prestige level"));
        lines.add("Prestige coins: " + config.getInt("prestige coins"));
        if (prestige) {
            for (PrestigeEnum bonus : PrestigeEnum.values()) {
                lines.add(bonus.getName() + ": " + config.getInt(bonus.getKey()) + "/" + bonus.getMaxAmount());
            }
        } else {
            lines.add("This legacy playtime: " + config.getInt("Prestige Playtime") + " seconds");
            lines.add("This legacy earnings: " + config.getString("legacy earnings", "0"));
            lines.add("Bank: " + config.getString("money", "0"));
            lines.add("Total clicks: " + config.getInt("cookie"));
            lines.add("Earnings by clicking: " + config.getDouble("earnedByClicking"));
            double clickBonus = PrestigeEnum.PERMANENTCLICKMULTIPLIER.getBonus()
                    * config.getInt(PrestigeEnum.PERMANENTCLICKMULTIPLIER.getKey());
            lines.add("Income per click: " + config.getDouble("Click Multiplier", 1) * (1 + clickBonus / 100));
            lines.add("Event chance: " + config.getDouble("eventChance", 0.17) + "%");
            lines.add("Bonuses collected: " + config.getInt("clickablesClicked"));
            int buildings = 0;
            double income = 0;
            for (Buildings building : Buildings.values()) {
                buildings += config.getInt(building.getName());
                income += (building.getBaseProduction() + config.getDouble(building.getName() + "BaseIncreaser"))
                        * config.getDouble(building.getName() + "BaseMultiplier", 1) * config.getInt(building.getName());
            }
            int achievements = 0;
            for (AchievementsEnum achievement : AchievementsEnum.values()) {
                if (config.getBoolean(achievement.getAchievement())) {
                    achievements++;
                }
            }
            lines.add("Buildings owned: " + buildings);
            lines.add("Achievements unlocked: " + achievements);
            int upgrades = 0;
            for (UpgradesBasedOnStats upgrade : UpgradesBasedOnStats.values()) {
                if (config.getBoolean(upgrade.getUnlockedKey())) {
                    upgrades++;
                }
            }
            for (UpgradesBasedOnBuildings upgrade : UpgradesBasedOnBuildings.values()) {
                if (config.getBoolean(upgrade.getUnlockedKey())) {
                    upgrades++;
                }
            }
            lines.add("Upgrades purchased: " + upgrades);
            double fairTrade = PrestigeEnum.FAIRTRADE.getBonus() * config.getInt(PrestigeEnum.FAIRTRADE.getKey());
            double permanent = PrestigeEnum.PERMANENTINCOMEMULTIPLIER.getBonus()
                    * config.getInt(PrestigeEnum.PERMANENTINCOMEMULTIPLIER.getKey());
            double achievementBonus = PrestigeEnum.MULTIPLIERPERUNLOCKEDACHIEVEMENT.getBonus()
                    * config.getInt(PrestigeEnum.MULTIPLIERPERUNLOCKEDACHIEVEMENT.getKey()) * achievements;
            lines.add("Passive income: " + income * (1 + fairTrade / 100) * (1 + permanent / 100) * (1 + achievementBonus / 100) + "/s");
        }
        return List.copyOf(lines);
    }

    static String joinLore(List<String> lore) {
        return String.join("\n", lore);
    }
}
