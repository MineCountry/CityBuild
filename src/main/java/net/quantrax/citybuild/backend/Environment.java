package net.quantrax.citybuild.backend;

import com.moandjiezana.toml.Toml;
import io.github.cdimascio.dotenv.Dotenv;
import net.quantrax.citybuild.backend.dao.impl.entity.MessageEntity;
import net.quantrax.citybuild.backend.dao.impl.repository.MessageRepository;
import net.quantrax.citybuild.utils.Log;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.Map.Entry;

public final class Environment {

    private static final String UPDATER_PREFIX = "[CityBild-Updater]";

    public static void prepareSystem() {
        Dotenv env = Dotenv.configure().filename("credentials.env").load();
        env.entries().forEach(dotenvEntry -> System.setProperty(dotenvEntry.getKey(), dotenvEntry.getValue()));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static @Nullable Toml createOrReadConfig(@NotNull JavaPlugin plugin) {
        try (InputStream inputStream = Environment.class.getClassLoader().getResourceAsStream("config.toml")) {
            if (inputStream == null) throw new IllegalStateException("Cannot find config.toml");

            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();

            File file = new File(dataFolder, "/config.toml");
            if (!file.exists()) {
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            return new Toml().read(file);

        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static void clearUpMessages(@NotNull MessageRepository repository) {
        try (InputStream inputStream = Environment.class.getClassLoader().getResourceAsStream("messages.toml")) {
            if (inputStream == null) throw new IllegalStateException("Cannot find messages.toml");

            Map<String, Object> defaults = new Toml().read(inputStream).toMap();

            repository.findAll().whenComplete((entities, $) -> {
                addFreshMessages(entities, defaults, repository);
                removeDeprecatedMessages(entities, defaults, repository);
            });

        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static void addFreshMessages(@NotNull List<MessageEntity> actual, @NotNull Map<String, Object> defaults, @NotNull MessageRepository repository) {
        computeMap(actual, defaults, repository);
    }

    private static void computeMap(@NotNull List<MessageEntity> actual, @NotNull Map<String, Object> defaults, @NotNull MessageRepository repository) {
        for (Entry<String, Object> entry : defaults.entrySet()) {

            if (entry.getValue() instanceof String value) {
                computeValue(actual, entry.getKey(), value, repository);
                continue;
            }

            @SuppressWarnings("unchecked") Map<String, Object> inner = (Map<String, Object>) entry.getValue();
            computeMap(actual, inner, repository);
        }
    }

    private static void computeValue(@NotNull List<MessageEntity> actual, String key, String value, MessageRepository repository) {
        Optional<MessageEntity> optional = findByName(actual, key);
        if (optional.isPresent()) return;

        repository.create(MessageEntity.create(key, value));
    }

    private static Optional<MessageEntity> findByName(@NotNull List<MessageEntity> entities, String name) {
        for (MessageEntity entity : entities) {
            if (!entity.key().equalsIgnoreCase(name)) continue;
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    private static void removeDeprecatedMessages(@NotNull List<MessageEntity> actual, @NotNull Map<String, Object> defaults, @NotNull MessageRepository repository) {
        Collection<String> defaultKeys = toList(defaults, new ArrayList<>());
        if (defaultKeys.isEmpty()) return;

        int i = 0;
        for (MessageEntity entity : actual) {
            if (defaultKeys.contains(entity.key())) continue;

            i++;
            repository.delete(entity);
        }

        if (i == 0) return;
        Log.info("%s Removed %s deprecated %s from database", UPDATER_PREFIX, i, i == 1 ? "message" : "messages");
    }

    private static @NotNull Collection<String> toList(@NotNull Map<String, Object> map, Collection<String> result) {
        for (Entry<String, Object> entry : map.entrySet()) {

            if (entry.getValue() instanceof String) {
                result.add(entry.getKey());
                continue;
            }

            @SuppressWarnings("unchecked") Map<String, Object> inner = (Map<String, Object>) entry.getValue();
            toList(inner, result);
        }
        return result;
    }
}