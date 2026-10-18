package io.github.stealingdapenta.foodclicker.commands;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FoodClickerCommand implements CommandExecutor {
    private final InventoryManager im = InventoryManager.getInstance();
    private final AntiSpamManager asm = AntiSpamManager.getInstance();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("FoodClicker: This command is only available to players.");
            return true;
        }

        if (asm.playerIsSpamming(p)) {
            asm.sendSpamWarning(p);
            return true;
        }

        asm.addPossibleSpammer(p);


        im.connect(p);
        ClickingPlayer cp = im.getClickingPlayerMap().get(p.getUniqueId());
        if (cp != null && cp.getPlayer() == p && !cp.isDisconnected()) {
            cp.runWhenReady(cp::openGame);
        } else {
            p.sendMessage("FoodClicker: Recovering your previous session. Use /foodclicker again when it has saved.");
        }
        return true;
    }
}