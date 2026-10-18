package io.github.stealingdapenta.foodclicker;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

class FoodClickerLifecycleTest {

    @Test
    void reenableAfterSuccessfulDrainDeliversServerThreadCallbacksAgain() {
        FoodClicker plugin = mock(FoodClicker.class, CALLS_REAL_METHODS);
        doReturn(Logger.getAnonymousLogger()).when(plugin).getLogger();
        doReturn(mock(PluginCommand.class)).when(plugin).getCommand(anyString());
        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        Runnable callback = mock(Runnable.class);
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
            bukkit.when(Bukkit::getPluginManager).thenReturn(mock(PluginManager.class));
            try {
                plugin.onEnable();
                plugin.onDisable();
                plugin.onEnable();
                plugin.onServerThread(callback);
                ArgumentCaptor<Runnable> scheduled = ArgumentCaptor.forClass(Runnable.class);
                verify(scheduler).runTask(eq(plugin), scheduled.capture());
                scheduled.getValue().run();
                verify(callback).run();
            } finally {
                plugin.onDisable();
            }
        }
    }
}
