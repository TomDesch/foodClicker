package io.github.stealingdapenta.foodclicker.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class StatsCommandTest {

    @Test
    void formatsEveryStatisticsLineForHoverText() {
        assertEquals("First line\nSecond line", StatsCommand.joinLore(List.of("First line", "Second line")));
    }
}
