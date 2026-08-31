package io.github.stealingdapenta.foodclicker.commands;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.FileManager;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class FoodClickerCommand implements CommandExecutor {
    private final InventoryManager im = InventoryManager.getInstance();
    private final FileManager fm = FileManager.getInstance();
    private final AntiSpamManager asm = AntiSpamManager.getInstance();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) return true;

        Player p = (Player) sender;

        if (asm.playerIsSpamming(p)) {
            asm.sendSpamWarning(p);
            return true;
        }

        asm.addPossibleSpammer(p);


        if (im.isPlayerInMap(p)) {
            ClickingPlayer cp = im.getClickingPlayerMap().get(p);
            cp.runWhenReady(() -> cp.getGui().setGameState(0));

        } else {
            long closeDate = fm.getLongByKey(p, "gameCloseDate");
            long elapsed = closeDate == 0 ? 0 : System.currentTimeMillis() / 1000 - closeDate;
            fm.setLongByKey(p, "gameCloseTime", fm.getLongByKey(p, "gameCloseTime") + elapsed);

            ClickingPlayer cp = new ClickingPlayer(p);
            im.addClickingPlayerToMap(cp);
            p.sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &ePlease wait while we load your data."));
            cp.runWhenReady(() -> cp.getGui().setGameState(0));
        }
        return true;
    }
}