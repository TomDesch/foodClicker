package io.github.stealingdapenta.foodclicker.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TopCommandTest {

    @Test
    void sortsLeaderboardFromHighestToLowestEarnings() {
        Map<String, BigDecimal> unsorted = Map.of(
                "middle", new BigDecimal("50"),
                "highest", new BigDecimal("100"),
                "lowest", new BigDecimal("10")
        );

        Map<String, BigDecimal> sorted = TopCommand.sortByValue(unsorted);

        assertEquals(List.of("highest", "middle", "lowest"), List.copyOf(sorted.keySet()));
        assertEquals(LinkedHashMap.class, sorted.getClass());
    }
}
