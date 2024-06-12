package io.github.stealingdapenta.foodclicker.commands;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

import static io.github.stealingdapenta.foodclicker.utils.ItemBuilder.colorStatic;

public class FireworkCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foodclicker.firework")) {
            sender.sendMessage(colorStatic("&6[&bFoodClicker&6] &eYou're not permitted to use this command."));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(colorStatic("&6[&bFoodClicker&6] &ePlease provide a player, amount and a size."));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(colorStatic("&6[&bFoodClicker&6] &ePlease provide an online players name."));
            return true;
        }

        int size;
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(colorStatic("&6[&bFoodClicker&6] &ePlease provide a numeral amount."));
            return true;
        }
        try {
            size = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(colorStatic("&6[&bFoodClicker&6] &ePlease provide a numeral size."));
            return true;
        }


        doFireWorks(target, amount, size);
        sender.sendMessage(colorStatic("&6[&bFoodClicker&6] &eSpawned &b" + amount + "&e fireworks at &b" + target.getName() + " &ewith size &b" + size + "&e."));

        return true;
    }

    private void doFireWorks(Player target, int amount, int size) {
        Location loc = target.getLocation();
        if (loc.getWorld() == null) return;
        Random r = new Random();
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(size);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).flicker(true).build());
        fw.remove();

        for (int i = 0; i < amount; i++) {
            Bukkit.getScheduler().runTaskLater(FoodClicker.getInstance(), () -> {
                fwm.clearEffects();
                fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))).flicker(true).trail(true).build());

                Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
                fw2.setFireworkMeta(fwm);
            }, (1L + i * 4));
        }
    }

}