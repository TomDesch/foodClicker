package io.github.stealingdapenta.foodclicker.clickingplayers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.stealingdapenta.foodclicker.utils.FileManager;
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
        data.getGeneralIntegerStats().put("cookie", 42);
        data.getGeneralIntegerStats().put("playtime", 3600);

        data.saveGeneralIntegerStats();

        verify(fileManager).setIntByKey(clickingPlayer, "cookie", 42);
        verify(fileManager).setIntByKey(clickingPlayer, "playtime", 3600);
        verify(fileManager, never()).setDoubleByKey(eq(clickingPlayer), anyString(), anyDouble());
    }
}
