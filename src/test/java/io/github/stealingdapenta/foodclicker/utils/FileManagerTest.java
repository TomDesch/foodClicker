package io.github.stealingdapenta.foodclicker.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

class FileManagerTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    void createsPlayerDirectoryAndValidYamlFile() {
        FileManager fileManager = new FileManager(() -> temporaryDirectory.toFile());
        Player player = player("TestPlayer");

        File playerFile = fileManager.getPlayerFile(player);

        assertTrue(playerFile.isFile());
        assertEquals("TestPlayer", YamlConfiguration.loadConfiguration(playerFile).getString("Player Name"));
    }

    @Test
    void reportsDirectoryCreationFailure() throws IOException {
        Path dataFolder = temporaryDirectory.resolve("not-a-directory");
        Files.createFile(dataFolder);
        FileManager fileManager = new FileManager(dataFolder::toFile);

        IllegalStateException exception = assertThrows(IllegalStateException.class, fileManager::getUserFiles);

        assertTrue(exception.getMessage().contains("Unable to create player data directory"));
    }

    @Test
    void corruptYamlCannotBeReadOrOverwritten() throws IOException {
        FileManager manager = new FileManager(() -> temporaryDirectory.toFile());
        UUID id = UUID.randomUUID();
        Path file = manager.getUserFiles().toPath().resolve(id + ".yml");
        String corrupt = "money: [not valid";
        Files.writeString(file, corrupt);

        assertThrows(IllegalStateException.class, () -> manager.read(id));
        assertThrows(IllegalStateException.class, () -> manager.save(id, "money: '0'"));
        assertThrows(IllegalStateException.class, () -> manager.update(id, config -> config.set("cookie", 1)));
        assertEquals(corrupt, Files.readString(file));
    }

    @Test
    void failedAtomicReplacementPreservesOriginalAndCleansPendingFile() throws IOException {
        FileManager manager = new FileManager(() -> temporaryDirectory.toFile()) {
            @Override
            void replace(Path source, Path destination) throws IOException {
                throw new IOException("disk full");
            }
        };
        UUID id = UUID.randomUUID();
        Path file = manager.getUserFiles().toPath().resolve(id + ".yml");
        Files.writeString(file, "money: '500'\n");

        assertThrows(UncheckedIOException.class, () -> manager.save(id, "money: '0'"));
        assertEquals("money: '500'\n", Files.readString(file));
        try (java.util.stream.Stream<Path> files = Files.list(file.getParent())) {
            assertEquals(1, files.count());
        }
    }

    @Test
    void unreadablePathCannotBeReplacedWithEmptyData() throws IOException {
        FileManager manager = new FileManager(() -> temporaryDirectory.toFile());
        UUID id = UUID.randomUUID();
        Path file = manager.getUserFiles().toPath().resolve(id + ".yml");
        Files.createDirectory(file);

        assertThrows(UncheckedIOException.class, () -> manager.read(id));
        assertThrows(UncheckedIOException.class, () -> manager.save(id, "money: '0'"));
        assertTrue(Files.isDirectory(file));
    }

    @Test
    void readingMissingDataDoesNotCreateOrWriteDefaults() {
        FileManager manager = new FileManager(() -> temporaryDirectory.toFile());
        Player player = player("ReadOnly");
        assertEquals(0, manager.getLongByKey(player, "gameCloseDate"));
        assertFalse(new File(manager.getUserFiles(), player.getUniqueId() + ".yml").exists());
    }

    @Test
    void creatingExistingFilePreservesStatistics() {
        FileManager manager = new FileManager(() -> temporaryDirectory.toFile());
        Player player = player("Renamed");
        manager.save(player.getUniqueId(), "money: '500'");
        manager.createFile(player);
        assertEquals("500", manager.read(player.getUniqueId()).getString("money"));
        assertEquals("Renamed", manager.read(player.getUniqueId()).getString("Player Name"));
    }

    private Player player(String name) {
        Player player = Mockito.mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn(name);
        return player;
    }
}
