package io.github.stealingdapenta.foodclicker.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
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

    private Player player(String name) {
        Player player = Mockito.mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn(name);
        return player;
    }
}
