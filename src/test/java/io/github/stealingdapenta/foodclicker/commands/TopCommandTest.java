package io.github.stealingdapenta.foodclicker.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.nio.file.Path;
import io.github.stealingdapenta.foodclicker.utils.SessionFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TopCommandTest {

    @TempDir
    Path directory;

    @Test
    void invalidDecimalIsSkippedWhileDuplicateNamesKeepDistinctUuidEntriesAndNamesRefresh() {
        try (SessionFixture fixture = new SessionFixture(directory)) {
            UUID first = UUID.randomUUID();
            UUID second = UUID.randomUUID();
            UUID invalid = UUID.randomUUID();
            fixture.files.save(first, "Player Name: Same\nalltimeearnings: '100'");
            fixture.files.save(second, "Player Name: Same\nalltimeearnings: '50'");
            fixture.files.save(invalid, "Player Name: Bad\nalltimeearnings: invalid");
            List<String> warnings = new ArrayList<>();

            List<TopCommand.Entry> entries = TopCommand.readLeaderboard(fixture.files, Map.of(first, "Renamed"), warnings::add);

            assertEquals(2, entries.size());
            assertEquals(first, entries.get(0).id());
            assertEquals("Renamed", entries.get(0).name());
            assertEquals(second, entries.get(1).id());
            assertEquals(List.of(invalid + ".yml"), warnings);
            assertEquals("invalid", fixture.files.read(invalid).getString("alltimeearnings"));
        }
    }

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
