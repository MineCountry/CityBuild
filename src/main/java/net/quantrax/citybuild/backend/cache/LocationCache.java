package net.quantrax.citybuild.backend.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.quantrax.citybuild.backend.dao.impl.repository.LocationRepository;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationCache {

    private static LocationCache instance;
    private final Cache<String, Location> cache = Caffeine.newBuilder().build();

    private LocationCache(@NotNull LocationRepository repository) {
        repository.findAllMapped().thenAccept(map -> map.forEach(cache::put));
    }

    public static LocationCache getInstance(@NotNull LocationRepository repository) {
        if (instance == null) instance = new LocationCache(repository);
        return instance;
    }

    public Location get(@NotNull String name) {
        return cache.getIfPresent(name);
    }
}
