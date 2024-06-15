package io.github.stealingdapenta.foodclicker.commands;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FoodClickerCommand implements CommandExecutor {
    private final InventoryManager im = InventoryManager.getInstance();
    private final AntiSpamManager asm = AntiSpamManager.getInstance();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) return true;

        Player p = (Player) sender;

        if (asm.playerIsSpamming(p)) {
            asm.sendSpamWarning(p);
            return true;
        }

        asm.addPossibleSpammer(p);


        if (im.isPlayerInMap(p)) {
            ClickingPlayer cp = im.getClickingPlayerMap().get(p);
            cp.getGui().setGameState(0);

        } else {
            ClickingPlayer cp = new ClickingPlayer(p);
            long time = cp.getData().loadGameCloseDate() == 0 ? 0 : System.currentTimeMillis() / 1000 - cp.getData().loadGameCloseDate();
            cp.getData().saveGameCloseTime(cp.getData().loadGameCloseTime() + time);

            im.addClickingPlayerToMap(cp);
            p.sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &ePlease wait &a2s &ewhile we load your data."));
            Bukkit.getScheduler()
                  .runTaskLater(FoodClicker.getInstance(), () -> cp.getGui()
                                                                   .setGameState(0), 40L);
        }
        return true;
    }
}