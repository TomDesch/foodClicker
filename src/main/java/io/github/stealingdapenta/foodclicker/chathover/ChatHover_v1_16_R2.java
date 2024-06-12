package io.github.stealingdapenta.foodclicker.chathover;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ChatHover_v1_16_R2 implements ChatHover {
    private static ChatHover_v1_16_R2 chatHover_v1_16_r2;

    private ChatHover_v1_16_R2() {
    }

    public static ChatHover_v1_16_R2 getInstance() {
        if (chatHover_v1_16_r2 == null) {
            chatHover_v1_16_r2 = new ChatHover_v1_16_R2();
        }
        return chatHover_v1_16_r2;
    }


    @Override
    public HoverEvent getItemTooltip(ItemStack item) {

        ItemStack stack = CraftItemStack.asCraftCopy(item);

        net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        net.minecraft.server.v1_16_R2.NBTTagCompound tagCompound = new net.minecraft.server.v1_16_R2.NBTTagCompound();
        nmsStack.save(tagCompound);

        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, TextComponent.fromLegacyText(tagCompound.toString()));
    }
}
