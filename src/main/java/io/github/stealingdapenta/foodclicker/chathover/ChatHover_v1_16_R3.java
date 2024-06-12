package io.github.stealingdapenta.foodclicker.chathover;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ChatHover_v1_16_R3 implements ChatHover {
    private static ChatHover_v1_16_R3 chatHover_v1_16_r3;

    private ChatHover_v1_16_R3() {
    }

    public static ChatHover_v1_16_R3 getInstance() {
        if (chatHover_v1_16_r3 == null) {
            chatHover_v1_16_r3 = new ChatHover_v1_16_R3();
        }
        return chatHover_v1_16_r3;
    }


    @Override
    public HoverEvent getItemTooltip(ItemStack item) {

        ItemStack stack = CraftItemStack.asCraftCopy(item);

        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        net.minecraft.server.v1_16_R3.NBTTagCompound tagCompound = new net.minecraft.server.v1_16_R3.NBTTagCompound();
        nmsStack.save(tagCompound);

        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, TextComponent.fromLegacyText(tagCompound.toString()));
    }
}
