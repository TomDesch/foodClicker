package io.github.stealingdapenta.foodclicker.commands;

import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PRESTIGEBRAG;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatsCommand implements CommandExecutor {

    private static final InventoryManager im = InventoryManager.getInstance();
    private final AntiSpamManager asm = AntiSpamManager.getInstance();


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        Player p = (Player) sender;
        ClickingPlayer cp;

        if (asm.playerIsSpamming(p)) {
            asm.sendSpamWarning(p);
            return true;
        }

        asm.addPossibleSpammer(p);

        if (im.isPlayerInMap(p)) {
            cp = im.getClickingPlayerMap()
                   .get(p);
        } else {
            cp = new ClickingPlayer(p);
        }

        p.sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &ePlease wait up to &a5s &ewhile we load your data."));

        Bukkit.getScheduler()
              .runTaskLater(FoodClicker.getInstance(), () -> {
                  boolean isPrestigeHead = false;

                  if (args.length >= 1) {
                      isPrestigeHead = Boolean.parseBoolean(args[0]);
                  }

                  if (isPrestigeHead) {
                      if (PRESTIGEBRAG.getAmountOwned(cp) < 1) {
                          cp.doMessage("You have not unlocked the Prestige Brag upgrade yet.");
                      }
                  }
                  HoverEvent tooltip = null;

                  if (tooltip == null) {
                      p.sendMessage("&6[&bFoodClicker&6] &eI'm sorry but this feature is not supported on this version.");
                      System.out.println("FoodClicker: this feature is not supported on this version.");
                      return;
                  }

                  TextComponent textComponent = new TextComponent(
                          ItemBuilder.colorStatic("&6[&bFoodClicker&6] &7Hover to see &b" + p.getDisplayName() + "&7's statistics!"));
                  textComponent.setHoverEvent(tooltip);

                  Bukkit.getServer()
                        .spigot()
                        .broadcast(textComponent);

                  cp.getRepeatingClickerTask()
                    .cancel();
              }, 50L);

        return true;
    }
}