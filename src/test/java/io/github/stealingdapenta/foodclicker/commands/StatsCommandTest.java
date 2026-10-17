package io.github.stealingdapenta.foodclicker.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import io.github.stealingdapenta.foodclicker.prestige.PrestigeEnum;
import io.github.stealingdapenta.foodclicker.basics.AchievementsEnum;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class StatsCommandTest {

    @Test
    void formatsEveryStatisticsLineForHoverText() {
        assertEquals("First line\nSecond line", StatsCommand.joinLore(List.of("First line", "Second line")));
    }

    @Test
    void snapshotStatisticsIncludeProgressAndNormalizedIncomeWithoutChangingSavedAfkTime() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("money", "123.45");
        config.set("cookie", 99);
        config.set("Mom", 4);
        config.set("loggedOutTime", 100);
        config.set("gameCloseTime", 50);
        config.set(PrestigeEnum.PERMANENTINCOMEMULTIPLIER.getKey(), 1);
        config.set(PrestigeEnum.MULTIPLIERPERUNLOCKEDACHIEVEMENT.getKey(), 1);
        config.set(AchievementsEnum.RIGHT.getAchievement(), true);
        config.set(AchievementsEnum.WHEELIE.getAchievement(), true);
        String before = config.saveToString();

        List<String> lines = StatsCommand.statistics(config, false);

        assertTrue(lines.contains("Bank: 123.45"));
        assertTrue(lines.contains("Total clicks: 99"));
        assertTrue(lines.contains("Buildings owned: 4"));
        assertTrue(lines.contains("Achievements unlocked: 2"));
        String income = lines.stream().filter(line -> line.startsWith("Passive income: ")).findFirst().orElseThrow();
        assertEquals(1.025 * 1.01, Double.parseDouble(income.substring("Passive income: ".length(), income.length() - 2)), 0.000001);
        assertEquals(before, config.saveToString());
        assertTrue(StatsCommand.statistics(config, true).contains("Permanent Multiplier: 1/25"));
    }
}
