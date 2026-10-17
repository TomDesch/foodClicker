package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.stealingdapenta.foodclicker.utils.FileManager;
import java.math.BigDecimal;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class ClickingPlayerDataTest {

    @Test
    void snapshotsPreserveTypedStatisticsSettingsAndUnknownDataWithoutDiskIo() throws Exception {
        ClickingPlayer cp = mock(ClickingPlayer.class);
        FileManager files = mock(FileManager.class);
        ClickingPlayerSettings settings = new ClickingPlayerSettings(cp);
        when(cp.getSettings()).thenReturn(settings);
        when(files.getBigDecimalByKey(any(YamlConfiguration.class), anyString())).thenReturn(BigDecimal.ZERO);
        ClickingPlayerData data = new ClickingPlayerData(cp, files);
        YamlConfiguration original = new YamlConfiguration();
        original.set("doBigNumbers", true);
        original.set("extension", "keep");
        original.set("loggedOutDate", 3_000_000_000L);
        data.loadSnapshot(original);
        data.getGeneralIntegerStats().put("cookie", 42);
        data.getGeneralBigDecimals().put("money", new BigDecimal("12345678901234567890.12"));
        clearInvocations(files);

        YamlConfiguration saved = new YamlConfiguration();
        saved.loadFromString(data.snapshot("NewName"));

        assertEquals(42, saved.get("cookie"));
        assertEquals("12345678901234567890.12", saved.getString("money"));
        assertEquals("NewName", saved.getString("Player Name"));
        assertEquals("keep", saved.getString("extension"));
        assertEquals(3_000_000_000L, saved.getLong("loggedOutDate"));
        assertTrue(saved.getBoolean("doBigNumbers"));
        verifyNoInteractions(files);
    }

    @Test
    void unloadedDataCannotOverwriteAPlayerFile() {
        ClickingPlayerData data = new ClickingPlayerData(mock(ClickingPlayer.class), mock(FileManager.class));
        assertThrows(IllegalStateException.class, () -> data.snapshot("Test"));
    }
}
