package io.github.stealingdapenta.foodclicker.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    private final ItemStack stack;
    private final List<String> loreList = new ArrayList<>();
    private ItemMeta meta;

    public ItemBuilder(ItemStack stack) {
        this.stack = stack.clone();
        this.meta = stack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material, 1));
    }

    public static String colorStatic(String str) {
        return str == null ? null : ChatColor.translateAlternateColorCodes('&', str);
    }

    public String color(String str) {
        return str == null ? null : ChatColor.translateAlternateColorCodes('&', str);
    }

    public ItemBuilder setDisplayName(String name) {
        meta = meta == null ? stack.getItemMeta() : meta;
        assert meta != null;
        meta.setDisplayName(color(name));
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        meta = meta == null ? stack.getItemMeta() : meta;
        List<String> temp = Arrays.asList(lore);

        temp.forEach(s -> loreList.add(color(s)));
        meta.setLore(loreList);

        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        meta = meta == null ? stack.getItemMeta() : meta;
        lore.forEach(s -> loreList.add(color(s)));
        meta.setLore(loreList);

        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        meta = meta == null ? stack.getItemMeta() : meta;
        assert meta != null;
        meta.addItemFlags(itemFlags);

        return this;
    }

    public ItemBuilder setGlowing(boolean glow) {
        if (!glow) {
            return this;
        }
        meta = meta == null ? stack.getItemMeta() : meta;
        assert meta != null;
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemStack create() {
        meta = meta == null ? stack.getItemMeta() : meta;
        stack.setItemMeta(meta);
        return stack;
    }
}
