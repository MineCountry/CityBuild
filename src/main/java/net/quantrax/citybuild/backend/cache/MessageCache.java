package net.quantrax.citybuild.backend.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.quantrax.citybuild.backend.dao.impl.repository.MessageRepository;
import org.jetbrains.annotations.NotNull;


public class MessageCache {

    private static MessageCache instance;
    private final Cache<String, String> cache = Caffeine.newBuilder().build();

    private MessageCache(@NotNull MessageRepository repository) {
        repository.findAll().whenComplete((entities, $) -> entities.forEach(entity -> cache.put(entity.key(), entity.value())));
    }

    public static MessageCache getInstance(@NotNull MessageRepository repository) {
        if (instance == null) instance = new MessageCache(repository);
        return instance;
    }

    public String get(@NotNull String key) {
        return cache.getIfPresent(key);
    }
}