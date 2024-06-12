package io.github.stealingdapenta.foodclicker.commands;

import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiSpamManager {
    private static final Map<UUID, Long> spammersMap = new HashMap<UUID, Long>();
    private static AntiSpamManager asm;

    private AntiSpamManager() {
    }

    public static AntiSpamManager getInstance() {
        if (asm == null) {
            asm = new AntiSpamManager();
        }
        return asm;
    }

    public Map<UUID, Long> getSpammersMap() {
        return spammersMap;
    }

    public void addPossibleSpammer(Player p) { //5s cooldown on the commands
        getSpammersMap().put(p.getUniqueId(), System.currentTimeMillis() + 5000);
    }

    public boolean playerIsSpamming(Player p) {
        return getSpammersMap().containsKey(p.getUniqueId()) && getSpammersMap().get(p.getUniqueId()) >= System.currentTimeMillis();
    }

    public void removeSpammer(Player p) {
        getSpammersMap().remove(p.getUniqueId());
    }

    public void sendSpamWarning(Player p) {
        p.sendMessage(ItemBuilder.colorStatic("&6[&bFoodClicker&6] &ePlease don't spam the commands."));
    }


}
