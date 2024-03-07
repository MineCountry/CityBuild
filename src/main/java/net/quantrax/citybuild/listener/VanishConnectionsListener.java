package net.quantrax.citybuild.listener;

import co.aikar.commands.annotation.Dependency;
import com.moandjiezana.toml.Toml;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.quantrax.citybuild.CityBuildPlugin;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.vanish.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishConnectionsListener implements Listener {

    private final CityBuildPlugin plugin;
    private final VanishManager vanishManager;
    @Dependency
    private MessageCache messageCache;
    @Dependency private Toml toml;

    public VanishConnectionsListener(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.vanishManager = VanishManager.getInstance(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!this.vanishManager.isVanished(event.getPlayer())) return;

        event.joinMessage(Component.empty());
        Bukkit.getOnlinePlayers().stream()
                .filter(all -> all.hasPermission(toml.getString("vanish-override-permission")))
                .forEach(player -> {
                    Messenger.builder(messageCache).sender(player).target(event.getPlayer().getName()).message("vanished-override-join-message").build().send();
                });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!this.vanishManager.isVanished(event.getPlayer())) return;

        event.quitMessage(Component.empty());
        Bukkit.getOnlinePlayers().stream()
                .filter(all -> all.hasPermission(toml.getString("vanish-override-permission")))
                .forEach(player -> {
                    Messenger.builder(messageCache).sender(player).target(event.getPlayer().getName()).message("vanished-override-quit-message").build().send();
                });
    }
}
