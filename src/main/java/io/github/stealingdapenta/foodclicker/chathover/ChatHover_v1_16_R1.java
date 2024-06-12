package io.github.stealingdapenta.foodclicker.chathover;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ChatHover_v1_16_R1 implements ChatHover {
    private static ChatHover_v1_16_R1 chatHover_v1_16_r1;

    private ChatHover_v1_16_R1() {
    }

    public static ChatHover_v1_16_R1 getInstance() {
        if (chatHover_v1_16_r1 == null) {
            chatHover_v1_16_r1 = new ChatHover_v1_16_R1();
        }
        return chatHover_v1_16_r1;
    }


    @Override
    public HoverEvent getItemTooltip(ItemStack item) {

        ItemStack stack = CraftItemStack.asCraftCopy(item);

        net.minecraft.server.v1_16_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        net.minecraft.server.v1_16_R1.NBTTagCompound tagCompound = new net.minecraft.server.v1_16_R1.NBTTagCompound();
        nmsStack.save(tagCompound);

        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, TextComponent.fromLegacyText(tagCompound.toString()));

    }
}
