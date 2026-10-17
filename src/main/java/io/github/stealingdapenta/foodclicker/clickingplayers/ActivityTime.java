package io.github.stealingdapenta.foodclicker.clickingplayers;

import org.bukkit.configuration.file.YamlConfiguration;

/** Server-thread owned accounting: active play is never also counted as idle time. */
final class ActivityTime {

    private long closedAt;
    private long loggedOutAt;
    private long closedSeconds;
    private long offlineSeconds;
    private boolean online;
    private boolean playing;

    ActivityTime(YamlConfiguration config) {
        closedAt = config.getLong("gameCloseDate");
        loggedOutAt = config.getLong("loggedOutDate");
        closedSeconds = Math.max(0, config.getLong("gameCloseTime"));
        offlineSeconds = Math.max(0, config.getLong("loggedOutTime"));
    }

    void join(long now) {
        if (online) {
            return;
        }
        offlineSeconds += elapsed(loggedOutAt > 0 ? loggedOutAt : closedAt, now);
        loggedOutAt = 0;
        closedAt = now;
        online = true;
    }

    void open(long now) {
        if (online && !playing) {
            closedSeconds += elapsed(closedAt, now);
            closedAt = 0;
            playing = true;
        }
    }

    void close(long now) {
        if (playing) {
            playing = false;
            closedAt = now;
        }
    }

    boolean disconnect(long now) {
        if (!online) {
            return false;
        }
        close(now);
        closedSeconds += elapsed(closedAt, now);
        closedAt = 0;
        loggedOutAt = now;
        online = false;
        return true;
    }

    long takeClosedSeconds() {
        long seconds = closedSeconds;
        closedSeconds = 0;
        return seconds;
    }

    long takeOfflineSeconds() {
        long seconds = offlineSeconds;
        offlineSeconds = 0;
        return seconds;
    }

    void write(YamlConfiguration config) {
        config.set("gameCloseDate", closedAt);
        config.set("loggedOutDate", loggedOutAt);
        config.set("gameCloseTime", closedSeconds);
        config.set("loggedOutTime", offlineSeconds);
    }

    private static long elapsed(long start, long now) {
        return start <= 0 || now <= start ? 0 : now - start;
    }
}
