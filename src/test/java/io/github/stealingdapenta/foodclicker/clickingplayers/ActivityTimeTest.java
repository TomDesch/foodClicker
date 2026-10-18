package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class ActivityTimeTest {

    @Test
    void closedOnlineOfflineAndActiveIntervalsAreDisjointAndDisconnectIsIdempotent() {
        ActivityTime time = new ActivityTime(new YamlConfiguration());
        time.join(100);
        time.open(110);
        time.close(200);
        assertTrue(time.disconnect(220));
        assertFalse(time.disconnect(230));
        time.join(250);
        time.open(260);
        assertEquals(40, time.takeClosedSeconds());
        assertEquals(30, time.takeOfflineSeconds());
        assertEquals(0, time.takeClosedSeconds());
        assertEquals(0, time.takeOfflineSeconds());
    }

    @Test
    void restartAccountsOfflineFromSavedDisconnectWithoutIntegerTruncation() {
        ActivityTime first = new ActivityTime(new YamlConfiguration());
        first.join(3_000_000_000L);
        first.open(3_000_000_010L);
        first.disconnect(3_000_000_100L);
        YamlConfiguration snapshot = new YamlConfiguration();
        first.write(snapshot);
        ActivityTime restarted = new ActivityTime(snapshot);
        restarted.join(3_000_000_200L);
        restarted.open(3_000_000_220L);
        assertEquals(30, restarted.takeClosedSeconds());
        assertEquals(100, restarted.takeOfflineSeconds());
    }

    @Test
    void negativeWallClockElapsedAndMissingDatesNeverCreateIncome() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("loggedOutDate", 200);
        config.set("gameCloseTime", -100);
        config.set("loggedOutTime", -200);
        ActivityTime time = new ActivityTime(config);
        time.join(100);
        time.open(90);
        time.disconnect(80);
        assertEquals(0, time.takeClosedSeconds());
        assertEquals(0, time.takeOfflineSeconds());
    }
}
