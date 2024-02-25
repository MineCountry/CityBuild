package net.quantrax.citybuild.utils;

import com.moandjiezana.toml.Toml;
import net.kyori.adventure.text.Component;
import net.quantrax.citybuild.CityBuildPlugin;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClearLag {

    private final ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(3);
    private final CityBuildPlugin plugin;
    private final MessageCache cache;
    private final long period;

    private int seconds;

    public ClearLag(@NotNull CityBuildPlugin plugin, @NotNull MessageCache cache, @NotNull Toml toml) {
        this.plugin = plugin;
        this.cache = cache;

        this.period = toml.getLong("clearlag-period");
        this.seconds = Math.toIntExact(TimeUnit.MINUTES.toSeconds(period));
    }

    public void execute() {
        threadPool.scheduleAtFixedRate(() -> {

            switch (seconds) {
                case 60, 30, 20, 10, 5, 4, 3, 2 -> broadcast("countdown", true);
                case 1 -> broadcast("countdown-last-second", false);
                case 0 -> {
                    runClearLag();
                    broadcast("executed", false);
                }
            }

            seconds--;

        }, 0L, 1, TimeUnit.SECONDS);
    }

    private void broadcast(@NotNull String key, boolean hasReplacement) {
        Component component;

        if (hasReplacement) {
            component = ComponentTranslator.withReplacements(cache, key, List.of(new Replacement<>("%value%", seconds)));

        } else {
            component = ComponentTranslator.fromCache(cache, key);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(component);
        }
    }

    private void runClearLag() {
        seconds = Math.toIntExact(TimeUnit.MINUTES.toSeconds(period)) + 1;
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(Item.class).forEach(Item::remove)));
    }
}