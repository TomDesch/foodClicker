package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.stealingdapenta.foodclicker.basics.AchievementsEnum;
import io.github.stealingdapenta.foodclicker.basics.Buildings;
import io.github.stealingdapenta.foodclicker.basics.Clickable;
import io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum;
import io.github.stealingdapenta.foodclicker.upgrades.UpgradesBasedOnEvents;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import io.github.stealingdapenta.foodclicker.utils.SessionFixture;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;

class EconomyRegressionTest {

    @TempDir
    Path directory;

    @Test
    void afkIncomePaysOnceAndSeparatesActiveClosedAndOfflineTimeAcrossReconnect() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("Mom", 4);
            config.set("money", "20");
            config.set("loggedOutDate", 900);
            config.set(PrestigeEnum.AFKBUTONLINE.getKey(), 1000);
            config.set(PrestigeEnum.AFKBUTONLINEDURATION.getKey(), 1);
            config.set(PrestigeEnum.AFKANDOFFLINE.getKey(), 1000);
            config.set(PrestigeEnum.AFKANDOFFLINEDURATION.getKey(), 2);
            fixture.files.save(fixture.player.getUniqueId(), config.saveToString());
            AtomicLong clock = new AtomicLong(1000);
            ClickingPlayer cp = new ClickingPlayer(fixture.player, clock::get);
            InventoryManager.getInstance().addClickingPlayerToMap(cp);
            fixture.finishIo();
            clock.set(1050);
            cp.openGame();
            assertEquals(0, new BigDecimal("35").compareTo(cp.getData().getGeneralBigDecimals().get("money")));
            clock.set(1200);
            cp.closeGame();
            fixture.finishIo();
            clock.set(1220);
            cp.openGame();
            assertEquals(0, new BigDecimal("37").compareTo(cp.getData().getGeneralBigDecimals().get("money")));
            assertEquals(0, cp.getData().getActivityTime().takeOfflineSeconds());
        }
    }

    @Test
    void permanentIncomeAndPerAchievementBonusesUsePercentAndUnlockedCount() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.getData().getBuildingsOwned().put(Buildings.MOM, 4);
            cp.getData().getPrestigeShopBonusses().put(PrestigeEnum.PERMANENTINCOMEMULTIPLIER, 1);
            assertEquals(1.025, cp.calculateTotalIncomePerSecond(), 0.000001);
            cp.getData().getPrestigeShopBonusses().put(PrestigeEnum.MULTIPLIERPERUNLOCKEDACHIEVEMENT, 1);
            cp.unlockAchievement(AchievementsEnum.RIGHT);
            cp.unlockAchievement(AchievementsEnum.WHEELIE);
            assertEquals(1.025 * 1.01, cp.getIncomePerSecond(), 0.000001);
            cp.unlockAchievement(AchievementsEnum.WHEELIE);
            assertEquals(1.025 * 1.01, cp.getIncomePerSecond(), 0.000001);
        }
    }

    @Test
    void fairTradeOnePercentAddsOnePercentToPriceAndProductionAndDiscountStaysPercent() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.getData().getPrestigeShopBonusses().put(PrestigeEnum.FAIRTRADE, 1);
            assertEquals(101, Buildings.MOM.calculateCost(cp), 0.000001);
            cp.getData().getBuildingsOwned().put(Buildings.MOM, 4);
            assertEquals(1.01, Buildings.MOM.calculateIncome(cp), 0.000001);
            cp.getData().getBuildingsOwned().put(Buildings.MOM, 0);
            cp.getData().getPrestigeShopBonusses().put(PrestigeEnum.DISCOUNT, 1);
            assertEquals(96.56, Buildings.MOM.calculateCost(cp), 0.000001);
        }
    }

    @Test
    void eventDurationSevenPercentExtendsDurationBySevenPercentNotEightTimes() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            int baseline = UpgradesBasedOnEvents.MOMBONUS_1.getBoostDuration(cp);
            cp.getData().getPrestigeShopBonusses().put(PrestigeEnum.EVENTDURATION, 1);
            assertEquals(Math.round(baseline * 1.07), UpgradesBasedOnEvents.MOMBONUS_1.getBoostDuration(cp));
        }
    }

    @Test
    void moneyGainsTheftAndPermanentEventChanceAreNeverScheduledForUndo() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.openGame();
            Inventory window = mock(Inventory.class);
            cp.getData().getGeneralBigDecimals().put("money", new BigDecimal("100"));
            List<UpgradesBasedOnEvents> events = List.of(
                    UpgradesBasedOnEvents.COOKIES_1, UpgradesBasedOnEvents.THIEF_1, UpgradesBasedOnEvents.EVENTBONUS_1);
            for (UpgradesBasedOnEvents bonus : events) {
                Clickable event = mock(Clickable.class);
                when(event.getBonus()).thenReturn(bonus);
                when(event.getInv()).thenReturn(window);
                cp.getActiveEvents().add(event);
                cp.eventGotClicked(event);
            }
            assertEquals(0, new BigDecimal("91.0200").compareTo(cp.getData().getGeneralBigDecimals().get("money")));
            assertEquals(0.17 * 1.12, cp.getData().getGeneralDoubleStats().get("eventChance"), 0.000001);
            assertTrue(cp.getActiveBoosts().isEmpty());
            verify(fixture.scheduler, never()).runTaskLater(any(), any(Runnable.class), anyLong());
            cp.closeGame();
            fixture.finishIo();
            assertEquals(0, new BigDecimal("91.0200").compareTo(fixture.files.getBigDecimalByKey(fixture.files.read(cp.getPlayerId()), "money")));
            assertEquals(0.17 * 1.12, fixture.files.read(cp.getPlayerId()).getDouble("eventChance"), 0.000001);
        }
    }

    @Test
    void tooltipsDifferentiateInstantPermanentAndTimedEffects() {
        try (SessionFixture fixture = new SessionFixture(directory);
             MockedConstruction<ItemBuilder> builders = mockConstruction(ItemBuilder.class, withSettings().defaultAnswer(RETURNS_SELF))) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            Inventory window = mock(Inventory.class);
            ItemStack pane = mock(ItemStack.class);
            when(pane.getType()).thenReturn(Material.GRAY_STAINED_GLASS_PANE);
            when(window.getSize()).thenReturn(1);
            when(window.getItem(0)).thenReturn(pane);
            new Clickable(10, window, UpgradesBasedOnEvents.COOKIES_1, cp);
            new Clickable(10, window, UpgradesBasedOnEvents.EVENTBONUS_1, cp);
            new Clickable(10, window, UpgradesBasedOnEvents.MOMBONUS_1, cp);
            verify(builders.constructed().get(0)).addLore(contains("Instantly"));
            verify(builders.constructed().get(1)).addLore(contains("permanent"));
            verify(builders.constructed().get(2)).addLore(contains("for"));
        }
    }

    @Test
    void timedBuildingSlowdownExpiresOnceAndRecalculatesProduction() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            ClickingPlayer cp = fixture.connect();
            fixture.finishIo();
            cp.openGame();
            cp.getData().getBuildingsOwned().put(Buildings.CHEF, 1);
            ArrayList<Runnable> expiries = new ArrayList<>();
            BukkitTask expiry = mock(BukkitTask.class);
            when(fixture.scheduler.runTaskLater(any(), any(Runnable.class), anyLong())).thenAnswer(call -> {
                expiries.add(call.getArgument(1));
                return expiry;
            });
            Clickable event = mock(Clickable.class);
            when(event.getBonus()).thenReturn(UpgradesBasedOnEvents.ROTTEN_3);
            Inventory window = mock(Inventory.class);
            when(event.getInv()).thenReturn(window);
            cp.getActiveEvents().add(event);
            cp.eventGotClicked(event);
            assertEquals(0.75, cp.getIncomePerSecond(), 0.000001);
            expiries.getFirst().run();
            expiries.getFirst().run();
            assertEquals(1, cp.getIncomePerSecond(), 0.000001);
            assertTrue(cp.getActiveBoosts().isEmpty());
            verify(expiry).cancel();
        }
    }
}
