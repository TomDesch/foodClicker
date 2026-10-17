package io.github.stealingdapenta.foodclicker.utils;

import io.github.stealingdapenta.foodclicker.FoodClicker;
import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class FileManager {

    private static FileManager fileManager;
    private final Supplier<File> dataFolderSupplier;
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    private FileManager() {
        this(() -> FoodClicker.getInstance().getDataFolder());
    }

    FileManager(Supplier<File> dataFolderSupplier) {
        this.dataFolderSupplier = dataFolderSupplier;
    }

    public static FileManager getInstance() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager;
    }

    public double getDoubleByKey(ClickingPlayer cp, String key) {
        return getConfig(cp).getDouble(key);
    }

    public BigDecimal getBigDecimalByKey(ClickingPlayer cp, String key) {
        return getBigDecimalByKey(getConfig(cp), key);
    }

    public BigDecimal getBigDecimalByKey(YamlConfiguration config, String key) {
        String bigString = config.getString(key, "0");
        try {
            return new BigDecimal(bigString);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid decimal value for player data key '" + key + "'", e);
        }
    }

    public void setBigDecimalByKey(ClickingPlayer cp, String key, BigDecimal score) {
        update(cp.getPlayer().getUniqueId(), config -> config.set(key, score.toPlainString()));
    }

    public int getIntByKey(ClickingPlayer cp, String key) {
        return getIntByKey(cp.getPlayer(), key);
    }

    public int getIntByKey(Player cp, String key) {
        return getConfig(cp).getInt(key);
    }

    public long getLongByKey(ClickingPlayer cp, String key) {
        return getLongByKey(cp.getPlayer(), key);
    }

    public long getLongByKey(Player cp, String key) {
        return getConfig(cp).getLong(key);
    }

    public boolean getBooleanByKey(ClickingPlayer cp, String key) {
        return getConfig(cp).getBoolean(key);
    }

    public void setDoubleByKey(ClickingPlayer cp, String key, double score) {
        update(cp.getPlayer().getUniqueId(), config -> config.set(key, score));
    }

    public void setLongByKey(ClickingPlayer cp, String key, long score) {
        setLongByKey(cp.getPlayer(), key, score);
    }

    public void setLongByKey(Player cp, String key, long score) {
        update(cp.getUniqueId(), config -> config.set(key, score));
    }

    public void setIntByKey(ClickingPlayer cp, String key, int score) {
        setIntByKey(cp.getPlayer(), key, score);
    }

    public void setIntByKey(Player cp, String key, int score) {
        update(cp.getUniqueId(), config -> config.set(key, score));
    }

    public void setBooleanByKey(ClickingPlayer cp, String key, boolean score) {
        update(cp.getPlayer().getUniqueId(), config -> config.set(key, score));
    }

    public YamlConfiguration getConfig(ClickingPlayer cp) {
        return getConfig(cp.getPlayer());
    }

    public YamlConfiguration getConfig(Player cp) {
        return read(cp.getUniqueId());
    }

    public void saveConfig(ClickingPlayer cp, YamlConfiguration config) {
        saveConfig(config, getPlayerFile(cp));
    }

    public void createFile(Player cp) {
        UUID id = cp.getUniqueId();
        String name = cp.getName();
        update(id, config -> config.set("Player Name", name));
    }

    public File getPlayerFile(ClickingPlayer cp) {
        return getPlayerFile(cp.getPlayer());
    }

    public File getPlayerFile(Player cp) {
        File file = playerFilePath(cp);

        if (!file.exists()) {
            createFile(cp);
        }

        return file;
    }

    public File getUserFiles() {
        File userFiles = new File(dataFolderSupplier.get(), "foodclicker");
        if (!userFiles.isDirectory() && !userFiles.mkdirs()) {
            throw new IllegalStateException("Unable to create player data directory: " + userFiles);
        }
        return userFiles;
    }

    public void savePlayerFile(ClickingPlayer cp) {
        savePlayerFile(cp.getPlayer());
    }

    public void savePlayerFile(Player cp) {
        File file = playerFilePath(cp);

        if (!file.exists()) {
            createFile(cp);
            return;
        }
        saveConfig(read(cp.getUniqueId()), file);
    }

    private File playerFilePath(Player player) {
        return playerFilePath(player.getUniqueId());
    }

    private File playerFilePath(UUID id) {
        return new File(getUserFiles(), id + ".yml");
    }

    public YamlConfiguration read(UUID id) {
        return readFile(playerFilePath(id));
    }

    public YamlConfiguration readFile(File file) {
        synchronized (lock(file)) {
            return strictRead(file);
        }
    }

    private YamlConfiguration strictRead(File file) {
        YamlConfiguration config = new YamlConfiguration();
        if (!Files.exists(file.toPath())) {
            return config;
        }
        try {
            config.load(file);
            return config;
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read player data file: " + file, e);
        } catch (InvalidConfigurationException e) {
            throw new IllegalStateException("Invalid player data file: " + file, e);
        }
    }

    public void update(UUID id, Consumer<YamlConfiguration> mutation) {
        File file = playerFilePath(id);
        synchronized (lock(file)) {
            YamlConfiguration config = strictRead(file);
            mutation.accept(config);
            saveConfig(config, file);
        }
    }

    public void save(UUID id, String yaml) {
        YamlConfiguration snapshot = new YamlConfiguration();
        try {
            snapshot.loadFromString(yaml);
        } catch (InvalidConfigurationException e) {
            throw new IllegalArgumentException("Invalid player snapshot", e);
        }
        saveConfig(snapshot, playerFilePath(id));
    }

    private Object lock(File file) {
        return locks.computeIfAbsent(file.getAbsolutePath(), ignored -> new Object());
    }

    private void saveConfig(YamlConfiguration config, File file) {
        synchronized (lock(file)) {
            // Never replace a file that became unreadable or corrupt since the snapshot was loaded.
            strictRead(file);
            Path pending = null;
            try {
                pending = Files.createTempFile(file.toPath().getParent(), file.getName(), ".pending");
                config.save(pending.toFile());
                replace(pending, file.toPath());
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to save player data file: " + file, e);
            } finally {
                if (pending != null) {
                    try {
                        Files.deleteIfExists(pending);
                    } catch (IOException e) {
                        pending.toFile().deleteOnExit();
                    }
                }
            }
        }
    }

    void replace(Path source, Path destination) throws IOException {
        // If the filesystem cannot replace atomically, fail closed and retain the original.
        Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }
}
