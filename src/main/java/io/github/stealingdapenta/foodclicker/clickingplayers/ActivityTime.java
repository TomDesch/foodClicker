package io.github.stealingdapenta.foodclicker.clickingplayers;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Server-thread-owned accounting that keeps active play, online idle time and offline time separate.
 * All timestamps and durations are in seconds; timestamps use the Unix epoch.
 */
final class ActivityTime {

    private long closedAt;
    private long loggedOutAt;
    private long closedSeconds;
    private long offlineSeconds;
    private boolean online;
    private boolean playing;

    /** Restores persisted accounting without marking the player online or playing. */
    ActivityTime(YamlConfiguration config) {
        closedAt = config.getLong("gameCloseDate");
        loggedOutAt = config.getLong("loggedOutDate");
        closedSeconds = Math.max(0, config.getLong("gameCloseTime"));
        offlineSeconds = Math.max(0, config.getLong("loggedOutTime"));
    }

    /** Accumulates offline time and begins online idle time; repeated joins are ignored. */
    void join(long now) {
        if (online) {
            return;
        }
        offlineSeconds += elapsed(loggedOutAt > 0 ? loggedOutAt : closedAt, now);
        loggedOutAt = 0;
        closedAt = now;
        online = true;
    }

    /** Accumulates the current online idle interval and starts active play, if online and idle. */
    void open(long now) {
        if (online && !playing) {
            closedSeconds += elapsed(closedAt, now);
            closedAt = 0;
            playing = true;
        }
    }

    /** Ends active play and starts an online idle interval; repeated closes preserve its start. */
    void close(long now) {
        if (playing) {
            playing = false;
            closedAt = now;
        }
    }

    /**
     * Accumulates remaining online idle time and records the start of an offline interval.
     *
     * @return true for the first disconnect after joining, false if already offline
     */
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

    /** Returns and clears accumulated online idle seconds, excluding any interval still in progress. */
    long takeClosedSeconds() {
        long seconds = closedSeconds;
        closedSeconds = 0;
        return seconds;
    }

    /** Returns and clears accumulated offline seconds, excluding any interval still in progress. */
    long takeOfflineSeconds() {
        long seconds = offlineSeconds;
        offlineSeconds = 0;
        return seconds;
    }

    /** Copies accounting into the supplied snapshot without saving it or advancing ongoing intervals. */
    void write(YamlConfiguration config) {
        config.set("gameCloseDate", closedAt);
        config.set("loggedOutDate", loggedOutAt);
        config.set("gameCloseTime", closedSeconds);
        config.set("loggedOutTime", offlineSeconds);
    }

    /** Treats missing timestamps and non-forward clock movement as zero elapsed time. */
    private static long elapsed(long start, long now) {
        return start <= 0 || now <= start ? 0 : now - start;
    }
}
