package net.quantrax.citybuild.backend;

import com.moandjiezana.toml.Toml;
import io.github.cdimascio.dotenv.Dotenv;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class Environment {

    public static void prepareSystem() {
        Dotenv env = Dotenv.configure().filename("credentials.env").load();
        env.entries().forEach(dotenvEntry -> System.setProperty(dotenvEntry.getKey(), dotenvEntry.getValue()));
    }

    public static @Nullable Toml readConfig(@NotNull Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "/config.toml");
        return file.exists() ? new Toml().read(file) : null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyConfig(@NotNull JavaPlugin plugin) {
        try (InputStream inputStream = Environment.class.getClassLoader().getResourceAsStream("config.toml")) {
            if (inputStream == null) throw new IllegalStateException("Cannot find config.toml");

            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();

            File file = new File(dataFolder, "/config.toml");
            if (!file.exists()) Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}