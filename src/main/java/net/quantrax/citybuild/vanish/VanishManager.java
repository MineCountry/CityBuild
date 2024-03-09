package net.quantrax.citybuild.vanish;

import co.aikar.commands.annotation.Dependency;
import com.moandjiezana.toml.Toml;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.CityBuildPlugin;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VanishManager {


    private static VanishManager instance;

    private final CityBuildPlugin plugin;
    private final List<UUID> vanishedPlayers = new ArrayList<>();

    @Dependency
    private Toml toml;

    @Dependency
    private MessageCache messageCache;

    public static VanishManager getInstance(CityBuildPlugin plugin) {
        if (VanishManager.instance == null) VanishManager.instance = new VanishManager(plugin);
        return VanishManager.instance;
    }

    public boolean isVanished(Player player) {
        return this.vanishedPlayers.contains(player.getUniqueId());
    }

    public void vanish(Player player) {
        if (this.vanishedPlayers.contains(player.getUniqueId())) return;
        this.vanishedPlayers.add(player.getUniqueId());
        this.vanishPlayerForAll(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Messenger.builder(messageCache).sender(onlinePlayer).message("vanish-default-quit-message").build().send();
        }
    }

    public void unVanish(Player player) {
        if (!this.vanishedPlayers.remove(player.getUniqueId())) return;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(this.plugin, player);
            Messenger.builder(messageCache).sender(onlinePlayer).message("vanish-default-join-message").build().send();
        }
    }

    public void vanishPlayerForAll(Player player) {
        Bukkit.getOnlinePlayers().stream()
                .filter(all -> !all.hasPermission("vanish.override"))
                .forEach(all -> all.hidePlayer(this.plugin, player));
    }

}
