package net.quantrax.citybuild.backend.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Preconditions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerCache {

    private static PlayerCache instance;
    private final Cache<Player, CityBuildPlayer> cache = Caffeine.newBuilder().build();

    public static PlayerCache getInstance() {
        if (instance == null) instance = new PlayerCache();
        return instance;
    }

    public void track(@NotNull Player player) {
        Preconditions.state(cache.getIfPresent(player) == null, "The player is already tracked");
        cache.put(player, CityBuildPlayer.create(player));
    }

    public void untrack(@NotNull Player player) {
        Preconditions.state(cache.getIfPresent(player) != null, "The player is not tracked");
        cache.invalidate(player);
    }

    public @Nullable CityBuildPlayer get(@NotNull Player player) {
        return cache.getIfPresent(player);
    }
}