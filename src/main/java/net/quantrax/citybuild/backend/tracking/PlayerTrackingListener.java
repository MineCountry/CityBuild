package net.quantrax.citybuild.backend.tracking;

import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.backend.dao.impl.PlayerRepository;
import net.quantrax.citybuild.global.CityBuildPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PlayerTrackingListener implements Listener {

    private final PlayerCache cache;
    private final PlayerRepository repository;

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        cache.track(event.getPlayer());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CityBuildPlayer cityBuildPlayer = cache.get(player);

        if (cityBuildPlayer == null) throw new IllegalStateException("The citybuild player cannot be null at this point");

        repository.save(cityBuildPlayer);
        cache.untrack(player);
    }
}