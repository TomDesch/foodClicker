package io.github.stealingdapenta.foodclicker.clickingplayers;

import io.github.stealingdapenta.foodclicker.basics.AchievementsEnum;
import org.bukkit.Material;

public class ClickingPlayerSettings {
    private final ClickingPlayer cp;
    private boolean inventoryDarkMode;
    private boolean loreDarkMode;
    private boolean doChatMessages;
    private boolean doSoundEffects;
    private boolean doFirework;
    private boolean doBigNumbers;

    public ClickingPlayerSettings(ClickingPlayer cp) {
        this.cp = cp;
    }

    public boolean isDoBigNumbers() {
        return doBigNumbers;
    }

    public void setDoBigNumbers(boolean doBigNumbers) {
        this.doBigNumbers = doBigNumbers;
    }

    public ClickingPlayer getCp() {
        return cp;
    }

    public boolean isInventoryDarkMode() {
        return inventoryDarkMode;
    }

    public void setInventoryDarkMode(boolean inventoryDarkMode) {
        this.inventoryDarkMode = inventoryDarkMode;
    }

    public boolean isLoreDarkMode() {
        return loreDarkMode;
    }

    public void setLoreDarkMode(boolean loreDarkMode) {
        this.loreDarkMode = loreDarkMode;
    }

    public boolean isDoChatMessages() {
        return doChatMessages;
    }

    public void setDoChatMessages(boolean doChatMessages) {
        this.doChatMessages = doChatMessages;
    }

    public boolean isDoSoundEffects() {
        return doSoundEffects;
    }

    public void setDoSoundEffects(boolean doSoundEffects) {
        this.doSoundEffects = doSoundEffects;
    }

    public boolean isDoFirework() {
        return doFirework;
    }

    public void setDoFirework(boolean doFirework) {
        this.doFirework = doFirework;
    }

    public void flickInventoryDarkMode() {
        setInventoryDarkMode(!isInventoryDarkMode());
        possiblyGetDarkSide();
    }

    public void flickLoreDarkMode() {
        setLoreDarkMode(!isLoreDarkMode());
        possiblyGetDarkSide();
    }

    public void flickDoChatMessages() {
        setDoChatMessages(!isDoChatMessages());
    }

    public void flickDoSoundEffects() {
        setDoSoundEffects(!isDoSoundEffects());
    }

    public void flickDoFirework() {
        setDoFirework(!isDoFirework());
    }

    public void flickDoBigNumbers() {
        setDoBigNumbers(!isDoBigNumbers());
    }

    public Material getPrimaryPaneColor() {
        if (isInventoryDarkMode()) {
            return Material.GRAY_STAINED_GLASS_PANE;
        } else {
            return Material.PURPLE_STAINED_GLASS_PANE;
        }
    }

    public Material getSecondaryPaneColor() {
        if (isInventoryDarkMode()) {
            return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
        } else {
            return Material.MAGENTA_STAINED_GLASS_PANE;
        }
    }

    public Material getBorderPaneColor() {
        if (isInventoryDarkMode()) {
            return Material.BLACK_STAINED_GLASS_PANE;
        } else {
            return Material.CYAN_STAINED_GLASS_PANE;
        }
    }

    public String getPrimaryLoreColor() {
        if (isLoreDarkMode()) {
            return "&1";
        } else {
            return "&b";
        }
    }

    public String getSecondaryLoreColor() {
        if (isLoreDarkMode()) {
            return "&2";
        } else {
            return "&a";
        }
    }

    public String getTextLoreColor() {
        if (isLoreDarkMode()) {
            return "&7";
        } else {
            return "&f";
        }
    }

    private void possiblyGetDarkSide() {
        if (isInventoryDarkMode() && isLoreDarkMode()) {
            getCp().unlockAchievement(AchievementsEnum.DARK);
        }
    }


}
