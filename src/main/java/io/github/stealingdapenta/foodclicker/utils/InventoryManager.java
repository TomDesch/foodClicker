package io.github.stealingdapenta.foodclicker.utils;

import static io.github.stealingdapenta.foodclicker.utils.ItemBuilder.colorStatic;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.basics.GameInventory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryManager {

    private static InventoryManager inventoryManager;
    private final Map<UUID, ClickingPlayer> clickingPlayerMap = new HashMap<>();

    private InventoryManager() {
    }

    public static InventoryManager getInstance() {
        if (inventoryManager == null) {
            inventoryManager = new InventoryManager();
        }
        return inventoryManager;
    }

    public Map<UUID, ClickingPlayer> getClickingPlayerMap() {
        return clickingPlayerMap;
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
        Bukkit.getScheduler().runTask(FoodClicker.getInstance(), () -> {
            if (p.isOnline() && ownsInventory(i, p)) {
                p.openInventory(i);
            }
        });
    }

    public boolean isPlayerInMap(Player p) {
        return clickingPlayerMap.containsKey(p.getUniqueId());
    }

    public void removePlayerFromMap(Player p) {
        clickingPlayerMap.remove(p.getUniqueId());
    }

    public void addClickingPlayerToMap(ClickingPlayer cp) {
        Player p = cp.getPlayer();
        if (clickingPlayerMap.putIfAbsent(p.getUniqueId(), cp) != null) {
            throw new IllegalStateException("Player already has a FoodClicker session");
        }
    }

    public boolean isCurrent(ClickingPlayer session) {
        return clickingPlayerMap.get(session.getPlayerId()) == session;
    }

    public void remove(ClickingPlayer session) {
        clickingPlayerMap.remove(session.getPlayerId(), session);
    }

    public void connect(Player player) {
        ClickingPlayer previous = clickingPlayerMap.get(player.getUniqueId());
        if (previous == null) {
            addClickingPlayerToMap(new ClickingPlayer(player));
        } else if (previous.getPlayer() != player || previous.isDisconnected()) {
            FoodClicker plugin = FoodClicker.getInstance();
            previous.shutdownAndSave().whenComplete((ignored, failure) ->
                    plugin.onServerThread(() -> {
                        if (failure == null) {
                            remove(previous);
                            if (player.isOnline() && !isPlayerInMap(player)) {
                                addClickingPlayerToMap(new ClickingPlayer(player));
                            }
                        } else if (failure != null && player.isOnline()) {
                            player.sendMessage("FoodClicker: Your previous session could not be saved. Use /foodclicker to retry.");
                        }
                    }));
        }
    }

    public boolean inventoryIsFoodClicker(InventoryView view, Player player) {
        return ownsInventory(view.getTopInventory(), player);
    }

    public boolean ownsInventory(Inventory inventory, Player player) {
        if (!(inventory.getHolder() instanceof GameInventory holder)) {
            return false;
        }
        ClickingPlayer session = holder.getSession();
        return session.getPlayer() == player && isCurrent(session) && session.isReady() && session.isGameOpen()
                && session.getGui() != null && session.getGui().getGameWindow() == inventory;
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
        } else if (isBetween(number, novemdecillion, vigintillion)) {
            return (makeNumbersPretty(calculateFraction(number, novemdecillion)) + " Novemdecillion");
        } else if (number.compareTo(vigintillion) >= 0) {
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
