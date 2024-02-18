package net.quantrax.citybuild.backend.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.backend.dao.impl.repository.PlayerRepository;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Preconditions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerCache {

    private static PlayerCache instance;
    private final Cache<Player, CityBuildPlayer> cache = Caffeine.newBuilder().build();
    private final PlayerRepository repository;

    public static PlayerCache getInstance(@NotNull PlayerRepository playerRepository) {
        if (instance == null) instance = new PlayerCache(playerRepository);
        return instance;
    }

    public void track(@NotNull Player player) {
        Preconditions.state(cache.getIfPresent(player) == null, "The player is already tracked");

        repository.findByUUID(player.getUniqueId()).whenComplete((optional, $) -> {
            CityBuildPlayer cityBuildPlayer = optional.orElseGet(() -> repository.create(player));
            cache.put(player, cityBuildPlayer);
        });
    }

    public void untrack(@NotNull Player player) {
        Preconditions.state(cache.getIfPresent(player) != null, "The player is not tracked");
        cache.invalidate(player);
    }

    public CityBuildPlayer get(@NotNull Player player) {
        return cache.getIfPresent(player);
    }
}