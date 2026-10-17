package io.github.stealingdapenta.foodclicker.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.basics.GUI;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public final class SessionFixture implements AutoCloseable {

    public final FoodClicker plugin = mock(FoodClicker.class);
    public final Player player = mock(Player.class);
    public final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    public final FileManager files;
    public final PlayerPersistence persistence = new PlayerPersistence();
    public final Queue<Runnable> callbacks = new ConcurrentLinkedQueue<>();
    private final MockedStatic<FoodClicker> plugins = mockStatic(FoodClicker.class);
    private final MockedStatic<FileManager> managers = mockStatic(FileManager.class);
    private final MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class);
    private final MockedConstruction<GUI> guis = Mockito.mockConstruction(GUI.class);

    public SessionFixture(Path directory) {
        files = Mockito.spy(new FileManager(directory::toFile));
        plugins.when(FoodClicker::getInstance).thenReturn(plugin);
        managers.when(FileManager::getInstance).thenReturn(files);
        bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        bukkit.when(Bukkit::isPrimaryThread).thenReturn(true);
        when(plugin.getPersistence()).thenReturn(persistence);
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        when(plugin.getLogger()).thenReturn(logger);
        doAnswer(call -> {
            callbacks.add(call.getArgument(0));
            return null;
        }).when(plugin).onServerThread(any());
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn("TestPlayer");
        when(player.isOnline()).thenReturn(true);
    }

    public ClickingPlayer connect() {
        InventoryManager.getInstance().connect(player);
        return InventoryManager.getInstance().getClickingPlayerMap().get(player.getUniqueId());
    }

    public void finishIo() {
        persistence.submit(() -> null).join();
        Runnable callback;
        while ((callback = callbacks.poll()) != null) {
            callback.run();
        }
    }

    @Override
    public void close() {
        persistence.drain(Duration.ofSeconds(5));
        InventoryManager.getInstance().getClickingPlayerMap().clear();
        guis.close();
        bukkit.close();
        managers.close();
        plugins.close();
    }
}
