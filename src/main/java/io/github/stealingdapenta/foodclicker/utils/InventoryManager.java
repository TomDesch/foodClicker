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
    private static final BigDecimal MILLION = BigDecimal.valueOf(1000000D);
    private static final BigDecimal BILLION = BigDecimal.valueOf(1000000000D);
    private static final BigDecimal TRILLION = BigDecimal.valueOf(1000000000000D);
    private static final BigDecimal QUADRILLION = BigDecimal.valueOf(1000000000000000D);
    private static final BigDecimal QUINTILLION = BigDecimal.valueOf(1000000000000000000D);
    private static final BigDecimal HEXTILLION = BigDecimal.valueOf(1000000000000000000000D);
    private static final BigDecimal SEPTILLION = BigDecimal.valueOf(1000000000000000000000000D);
    private static final BigDecimal OCTILLION = BigDecimal.valueOf(1000000000000000000000000000D);
    private static final BigDecimal NONILLION = BigDecimal.valueOf(1000000000000000000000000000000D);
    private static final BigDecimal DECILLION = BigDecimal.valueOf(1000000000000000000000000000000000D);
    private static final BigDecimal UNDECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000D);
    private static final BigDecimal DUODECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000D);
    private static final BigDecimal TREDECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000D);
    private static final BigDecimal QUATTUORDECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000000D);
    private static final BigDecimal QUINDECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000D);
    private static final BigDecimal HEXDECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000D);
    private static final BigDecimal SEPTENDECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000D);
    private static final BigDecimal OCTODECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000000D);
    private static final BigDecimal NOVEMDECILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000000000D);
    private static final BigDecimal VIGINTILLION = BigDecimal.valueOf(1000000000000000000000000000000000000000000000000000000000000000D);
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
        if (isBetween(number, MILLION, BILLION)) {
            return (makeNumbersPretty(calculateFraction(number, MILLION)) + " Million");
        } else if (isBetween(number, BILLION, TRILLION)) {
            return (makeNumbersPretty(calculateFraction(number, BILLION)) + " Billion");
        } else if (isBetween(number, TRILLION, QUADRILLION)) {
            return (makeNumbersPretty(calculateFraction(number, TRILLION)) + " Trillion");
        } else if (isBetween(number, QUADRILLION, QUINTILLION)) {
            return (makeNumbersPretty(calculateFraction(number, QUADRILLION)) + " Quadrillion");
        } else if (isBetween(number, QUINTILLION, HEXTILLION)) {
            return (makeNumbersPretty(calculateFraction(number, QUINTILLION)) + " Quintillion");
        } else if (isBetween(number, HEXTILLION, SEPTILLION)) {
            return (makeNumbersPretty(calculateFraction(number, HEXTILLION)) + " Hextillion");
        } else if (isBetween(number, SEPTILLION, OCTILLION)) {
            return (makeNumbersPretty(calculateFraction(number, SEPTILLION)) + " Septillion");
        } else if (isBetween(number, OCTILLION, NONILLION)) {
            return (makeNumbersPretty(calculateFraction(number, OCTILLION)) + " Octillion");
        } else if (isBetween(number, NONILLION, DECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, NONILLION)) + " Nonillion");
        } else if (isBetween(number, DECILLION, UNDECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, DECILLION)) + " Decillion");
        } else if (isBetween(number, UNDECILLION, DUODECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, UNDECILLION)) + " Undecillion");
        } else if (isBetween(number, DUODECILLION, TREDECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, DUODECILLION)) + " Duodecillion");
        } else if (isBetween(number, TREDECILLION, QUATTUORDECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, TREDECILLION)) + " Tredecillion");
        } else if (isBetween(number, QUATTUORDECILLION, QUINDECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, QUATTUORDECILLION)) + " Quattuordecillion");
        } else if (isBetween(number, QUINDECILLION, HEXDECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, QUINDECILLION)) + " Quindecillion");
        } else if (isBetween(number, HEXDECILLION, SEPTENDECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, HEXDECILLION)) + " Hexdecillion");
        } else if (isBetween(number, SEPTENDECILLION, OCTODECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, SEPTENDECILLION)) + " Septendecillion");
        } else if (isBetween(number, OCTODECILLION, NOVEMDECILLION)) {
            return (makeNumbersPretty(calculateFraction(number, OCTODECILLION)) + " Octodecillion");
        } else if (isBetween(number, NOVEMDECILLION, VIGINTILLION)) {
            return (makeNumbersPretty(calculateFraction(number, NOVEMDECILLION)) + " Novemdecillion");
        } else if (number.compareTo(VIGINTILLION) >= 0) {
            return (makeNumbersPretty(calculateFraction(number, VIGINTILLION)) + " Vigintillion");
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
