package io.github.stealingdapenta.foodclicker.chathover;

import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.inventory.ItemStack;

public interface ChatHover {

    HoverEvent getItemTooltip(ItemStack item);

}
