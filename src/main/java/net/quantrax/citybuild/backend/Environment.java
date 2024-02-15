package net.quantrax.citybuild.backend;

import com.moandjiezana.toml.Toml;
import io.github.cdimascio.dotenv.Dotenv;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public final class Environment {

    public static void prepareSystem() {
        Dotenv env = Dotenv.configure().filename("credentials.env").load();
        env.entries().forEach(dotenvEntry -> System.setProperty(dotenvEntry.getKey(), dotenvEntry.getValue()));
    }

    public static @Nullable Toml readConfig(@NotNull Plugin plugin) {
        File file = new File(plugin.getDataFolder() + "/CityBuild/config.toml");
        return file.exists() ? new Toml().read(file) : null;
    }

    public static void copyToml(@NotNull JavaPlugin plugin) {
        URL url = Environment.class.getClassLoader().getResource("toml/");

        if (url == null) throw new IllegalStateException("No toml files were found");

        List<File> files;
        try (Stream<Path> fileStream = Files.walk(Paths.get(url.toURI()))) {
            files = fileStream.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();

        } catch (IOException | URISyntaxException exception) {
            throw new RuntimeException(exception);
        }

        if (files.isEmpty()) return;

        files.forEach(file -> {
            try {
                FileUtils.copyFile(file, new File(plugin.getDataFolder() + "/CityBuild/" + file.getName()));

            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}