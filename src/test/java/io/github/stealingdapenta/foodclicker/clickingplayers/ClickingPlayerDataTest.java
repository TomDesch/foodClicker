package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.stealingdapenta.foodclicker.utils.FileManager;
import java.math.BigDecimal;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClickingPlayerDataTest {

    @Mock
    private ClickingPlayer clickingPlayer;

    @Mock
    private FileManager fileManager;

    private ClickingPlayerData data;

    @BeforeEach
    void setUp() {
        data = new ClickingPlayerData(clickingPlayer, fileManager);
    }

    @Test
    void loadsGameCloseDateWithoutIntegerTruncation() {
        long timestamp = 3_000_000_000L;
        when(fileManager.getLongByKey(clickingPlayer, "gameCloseDate")).thenReturn(timestamp);

        assertEquals(timestamp, data.loadGameCloseDate());
    }

    @Test
    void savesIntegerStatsAsIntegers() {
        YamlConfiguration config = new YamlConfiguration();
        when(fileManager.getConfig(clickingPlayer)).thenReturn(config);
        data.getGeneralIntegerStats().put("cookie", 42);
        data.getGeneralIntegerStats().put("playtime", 3600);

        data.saveGeneralIntegerStats();

        assertEquals(Integer.class, config.get("cookie").getClass());
        assertEquals(42, config.getInt("cookie"));
        assertEquals(Integer.class, config.get("playtime").getClass());
        assertEquals(3600, config.getInt("playtime"));
        verify(fileManager).saveConfig(clickingPlayer, config);
    }

    @Test
    void loadsAllPlayerDataFromOneConfigurationSnapshot() {
        YamlConfiguration config = new YamlConfiguration();
        ClickingPlayerSettings settings = org.mockito.Mockito.mock(ClickingPlayerSettings.class);
        config.set("doBigNumbers", true);
        when(fileManager.getConfig(clickingPlayer)).thenReturn(config);
        when(clickingPlayer.getSettings()).thenReturn(settings);

        data.loadEverythingFromFile();

        verify(fileManager).getConfig(clickingPlayer);
        verify(settings).setDoBigNumbers(true);
    }

    @Test
    void savesAllPlayerDataWithOneFileWrite() {
        YamlConfiguration config = new YamlConfiguration();
        ClickingPlayerSettings settings = org.mockito.Mockito.mock(ClickingPlayerSettings.class);
        when(fileManager.getConfig(clickingPlayer)).thenReturn(config);
        when(clickingPlayer.getSettings()).thenReturn(settings);
        when(settings.isDoBigNumbers()).thenReturn(true);
        when(fileManager.getBigDecimalByKey(same(config), anyString())).thenReturn(BigDecimal.ZERO);
        data.loadEverythingFromFile();
        clearInvocations(fileManager);

        data.saveEverythingToFile();

        verify(fileManager).getConfig(clickingPlayer);
        verify(fileManager).saveConfig(clickingPlayer, config);
        assertTrue(config.getBoolean("doBigNumbers"));
    }
}
