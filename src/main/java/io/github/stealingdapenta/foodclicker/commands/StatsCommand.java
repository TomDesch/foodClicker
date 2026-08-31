package io.github.stealingdapenta.foodclicker.commands;

import static io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum.PRESTIGEBRAG;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import java.util.List;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatsCommand implements CommandExecutor {

    private static final InventoryManager im = InventoryManager.getInstance();
    private final AntiSpamManager asm = AntiSpamManager.getInstance();


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        Player p = (Player) sender;
        ClickingPlayer cp;
        boolean temporarySession;

        if (asm.playerIsSpamming(p)) {
            asm.sendSpamWarning(p);
            return true;
        }

        asm.addPossibleSpammer(p);

        if (im.isPlayerInMap(p)) {
            cp = im.getClickingPlayerMap()
                   .get(p);
            temporarySession = false;
        } else {
            cp = new ClickingPlayer(p);
            temporarySession = true;
        }

        p.sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &ePlease wait while we load your data."));

        cp.runWhenReady(() -> {
                  boolean isPrestigeHead = false;

                  if (args.length >= 1) {
                      isPrestigeHead = Boolean.parseBoolean(args[0]);
                  }

                  if (isPrestigeHead) {
                      if (PRESTIGEBRAG.getAmountOwned(cp) < 1) {
                          cp.doMessage("You have not unlocked the Prestige Brag upgrade yet.");
                          cancelTemporarySession(cp, temporarySession);
                          return;
                      }
                  }

                  ItemStack statistics = cp.getPlayerStatsHead(isPrestigeHead);
                  ItemMeta meta = statistics.getItemMeta();
                  if (meta == null || meta.getLore() == null) {
                      p.sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &eUnable to display your statistics."));
                      cancelTemporarySession(cp, temporarySession);
                      return;
                  }
                  HoverEvent tooltip = new HoverEvent(
                          HoverEvent.Action.SHOW_TEXT,
                          new Text(joinLore(meta.getLore()))
                  );

                  TextComponent textComponent = new TextComponent(
                          ItemBuilder.colorStatic("&6[&bFoodClicker&6] &7Hover to see &b" + p.getDisplayName() + "&7's statistics!"));
                  textComponent.setHoverEvent(tooltip);

                  Bukkit.getServer()
                        .spigot()
                        .broadcast(textComponent);

                  cancelTemporarySession(cp, temporarySession);
        });

        return true;
    }

    static String joinLore(List<String> lore) {
        return String.join("\n", lore);
    }

    private void cancelTemporarySession(ClickingPlayer cp, boolean temporarySession) {
        if (temporarySession) {
            cp.getRepeatingClickerTask().cancel();
        }
    }
}