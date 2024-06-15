package io.github.stealingdapenta.foodclicker.utils;

import static io.github.stealingdapenta.foodclicker.utils.ItemBuilder.colorStatic;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.ACHIEVEMENTS_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.GAME_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.PREFERENCES_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.PRESTIGE_SHOP;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.SHOP_GUI_TITLE_ADDENDUM;
import static io.github.stealingdapenta.foodclicker.utils.Listeners.UPGRADES_GUI_TITLE_ADDENDUM;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@Getter
public class InventoryManager {

    private static InventoryManager inventoryManager;
    private final Map<Player, ClickingPlayer> clickingPlayerMap = new HashMap<>();

    private InventoryManager() {
    }

    public static InventoryManager getInstance() {
        if (inventoryManager == null) {
            inventoryManager = new InventoryManager();
        }
        return inventoryManager;
    }

    public void setInventoryBorder(Inventory inv, Material color) {
        int size = inv.getSize();
        ItemStack border = new ItemBuilder(color).setDisplayName(" ")
                                                 .create();

        for (int i = 0; i < size; i++) {
            if ((i < 9) || (((i) % 9) == 0) || (((i + 1) % 9) == 0) || (i > (size - 9))) {
                if (inv.getItem(i) == null) {
                    inv.setItem(i, border);
                }
            }
        }

        //border left side from the buttons
        inv.setItem(15, border);
        inv.setItem(24, border);
        inv.setItem(33, border);
        inv.setItem(42, border);
    }

    public void setInventoryPattern(Inventory inv, boolean pattern, Material color1, Material color2) {
        int size = inv.getSize();
        ItemStack primary = new ItemBuilder(color1).setDisplayName(" ")
                                                   .create();
        ItemStack secondary = new ItemBuilder(color2).setDisplayName(" ")
                                                     .create();

        if (pattern) {
            for (int i = 0; i < size; i++) {
                if ((i % 2) == 0) {
                    if (inv.getItem(i) == null) {
                        inv.setItem(i, primary);
                    }
                } else {
                    if (inv.getItem(i) == null) {
                        inv.setItem(i, secondary);
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (inv.getItem(i) == null) {
                    inv.setItem(i, primary);
                }
            }
        }
    }

    public ItemStack getPlayerSkullItem(Player p) {
        ItemStack playerHead = new ItemBuilder(Material.PLAYER_HEAD).create();
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(p);
        skullMeta.setDisplayName(colorStatic("&6" + p.getName() + "'s Statistics"));
        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }

    public void closeGUI(Player p) {
        Bukkit.getScheduler()
              .runTask(FoodClicker.getInstance(), p::closeInventory);
    }

    public void openGUI(Inventory i, Player p) {
        closeGUI(p);
        Bukkit.getScheduler()
              .runTaskLater(FoodClicker.getInstance(), () -> p.openInventory(i), 2L);
    }

    public boolean isPlayerInMap(Player p) {
        return clickingPlayerMap.containsKey(p);
    }

    public void removePlayerFromMap(Player p) {
        clickingPlayerMap.remove(p);
    }

    public void addClickingPlayerToMap(ClickingPlayer cp) {
        Player p = cp.getPlayer();
        if (isPlayerInMap(p)) {
            removePlayerFromMap(p);
        }
        clickingPlayerMap.put(p, cp);
    }

    public boolean inventoryIsFoodClicker(String invTitle, Player player) {
        return invTitle.equals(player.getDisplayName() + GAME_GUI_TITLE_ADDENDUM) || invTitle.equals(
                player.getDisplayName() + ACHIEVEMENTS_GUI_TITLE_ADDENDUM) || invTitle.equals(player.getDisplayName() + PREFERENCES_GUI_TITLE_ADDENDUM)
                || invTitle.equals(player.getDisplayName() + SHOP_GUI_TITLE_ADDENDUM) || invTitle.equals(
                player.getDisplayName() + UPGRADES_GUI_TITLE_ADDENDUM) || invTitle.equals(player.getDisplayName() + PRESTIGE_SHOP);
    }

    public String makeNumbersPretty(double d) {
        return new DecimalFormat((d < 100_000) ? "###,###.##" : "###,###").format(d);
    }

    public String makeNumbersPretty(BigDecimal d) {
        return new DecimalFormat((d.doubleValue() < 100_000) ? "###,###.##" : "###,###").format(d);
    }

    public String makeSecondsATimestamp(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int remainder = totalSeconds % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;
        return (hours + "h " + minutes + "m " + seconds + "s");
    }

    public List<String> makeLore30CharsPerLine(String lore, String color) {
        String[] split = lore.split(" ");

        List<String> individualLines = new ArrayList<>();
        String currentLine = "";

        for (String word : split) {
            if ((currentLine.length() + word.length()) < 30) {
                currentLine = currentLine.concat(" ")
                                         .concat(word);
            } else {
                individualLines.add(currentLine.trim());
                currentLine = word;
            }
        }
        individualLines.add(currentLine.trim());
        individualLines = individualLines.stream()
                                         .map(s -> color + s)
                                         .collect(Collectors.toList());
        return individualLines;
    }

    public String truncateNumber(double number, ClickingPlayer cp) {
        return truncateNumber(BigDecimal.valueOf(number), cp);
    }

    public String truncateNumber(BigDecimal number) {
        BigDecimal million = BigDecimal.valueOf(1000000D);
        BigDecimal billion = BigDecimal.valueOf(1000000000D);
        BigDecimal trillion = BigDecimal.valueOf(1000000000000D);
        BigDecimal quadrillion = BigDecimal.valueOf(1000000000000000D);
        BigDecimal quintillion = BigDecimal.valueOf(1000000000000000000D);
        BigDecimal hextillion = BigDecimal.valueOf(1000000000000000000000D);
        BigDecimal septillion = BigDecimal.valueOf(1000000000000000000000000D);
        BigDecimal octillion = BigDecimal.valueOf(1000000000000000000000000000D);
        BigDecimal nonillion = BigDecimal.valueOf(1000000000000000000000000000000D);
        BigDecimal decillion = BigDecimal.valueOf(1000000000000000000000000000000000D);
        BigDecimal undecillion = BigDecimal.valueOf(1000000000000000000000000000000000000D);
        BigDecimal duodecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000D);
        BigDecimal tredecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000D);
        BigDecimal quattuordecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000000D);
        BigDecimal quindecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000D);
        BigDecimal hexdecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000D);
        BigDecimal septendecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000D);
        BigDecimal octodecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000000D);
        BigDecimal novemdecillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000000000D);
        BigDecimal vigintillion = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000000000000D);

        if (isBetween(number, million, billion)) {
            return (makeNumbersPretty(calculateFraction(number, million)) + " Million");
        } else if (isBetween(number, billion, trillion)) {
            return (makeNumbersPretty(calculateFraction(number, billion)) + " Billion");
        } else if (isBetween(number, trillion, quadrillion)) {
            return (makeNumbersPretty(calculateFraction(number, trillion)) + " Trillion");
        } else if (isBetween(number, quadrillion, quintillion)) {
            return (makeNumbersPretty(calculateFraction(number, quadrillion)) + " Quadrillion");
        } else if (isBetween(number, quintillion, hextillion)) {
            return (makeNumbersPretty(calculateFraction(number, quintillion)) + " Quintillion");
        } else if (isBetween(number, hextillion, septillion)) {
            return (makeNumbersPretty(calculateFraction(number, hextillion)) + " Hextillion");
        } else if (isBetween(number, septillion, octillion)) {
            return (makeNumbersPretty(calculateFraction(number, septillion)) + " Septillion");
        } else if (isBetween(number, octillion, nonillion)) {
            return (makeNumbersPretty(calculateFraction(number, octillion)) + " Octillion");
        } else if (isBetween(number, nonillion, decillion)) {
            return (makeNumbersPretty(calculateFraction(number, nonillion)) + " Nonillion");
        } else if (isBetween(number, decillion, undecillion)) {
            return (makeNumbersPretty(calculateFraction(number, decillion)) + " Decillion");
        } else if (isBetween(number, undecillion, duodecillion)) {
            return (makeNumbersPretty(calculateFraction(number, undecillion)) + " Undecillion");
        } else if (isBetween(number, duodecillion, tredecillion)) {
            return (makeNumbersPretty(calculateFraction(number, duodecillion)) + " Duodecillion");
        } else if (isBetween(number, tredecillion, quattuordecillion)) {
            return (makeNumbersPretty(calculateFraction(number, tredecillion)) + " Tredecillion");
        } else if (isBetween(number, quattuordecillion, quindecillion)) {
            return (makeNumbersPretty(calculateFraction(number, quattuordecillion)) + " Quattuordecillion");
        } else if (isBetween(number, quindecillion, hexdecillion)) {
            return (makeNumbersPretty(calculateFraction(number, quindecillion)) + " Quindecillion");
        } else if (isBetween(number, hexdecillion, septendecillion)) {
            return (makeNumbersPretty(calculateFraction(number, hexdecillion)) + " Hexdecillion");
        } else if (isBetween(number, septendecillion, octodecillion)) {
            return (makeNumbersPretty(calculateFraction(number, septendecillion)) + " Septendecillion");
        } else if (isBetween(number, octodecillion, novemdecillion)) {
            return (makeNumbersPretty(calculateFraction(number, octodecillion)) + " Octodecillion");
        } else if (number.compareTo(novemdecillion) > 0) {
            return (makeNumbersPretty(calculateFraction(number, novemdecillion)) + " Novemdecillion");
        } else if (number.compareTo(vigintillion) > 0) {
            return (makeNumbersPretty(calculateFraction(number, vigintillion)) + " Vigintillion");
        }

        return makeNumbersPretty(number);
    }

    public String truncateNumber(BigDecimal number, ClickingPlayer cp) {

        if (cp.getSettings()
              .isDoBigNumbers()) {
            return makeNumbersPretty(number);
        }

        return truncateNumber(number);
    }

    private boolean isBetween(BigDecimal number, BigDecimal bound1, BigDecimal bound2) {
        return (number.compareTo(bound1) >= 0 && number.compareTo(bound2) < 0);
    }

    private BigDecimal calculateFraction(BigDecimal number, BigDecimal divisor) {
        return number.divide(divisor, 5, RoundingMode.HALF_DOWN);
    }

}
