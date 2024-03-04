package net.quantrax.citybuild.global;

import de.chojo.sadu.wrapper.util.Row;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.backend.dao.Entity;
import net.quantrax.citybuild.backend.events.PlayerCoinsChangeEvent;
import net.quantrax.citybuild.backend.events.PlayerCoinsChangeEvent.ChangeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CityBuildPlayer implements Entity<UUID> {

    private final PluginManager pluginManager = Bukkit.getPluginManager();

    private final UUID uuid;
    private int coins;

    @Internal
    @Contract("_ -> new")
    public static @NotNull CityBuildPlayer create(@NotNull Player player) {
        return new CityBuildPlayer(player.getUniqueId());
    }

    @Internal
    @Contract("_ -> new")
    public static @NotNull CityBuildPlayer fromRow(@NotNull Row row) throws SQLException {
        return new CityBuildPlayer(row.getUuidFromString("uuid"));
    }

    public @Nullable Player asBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean addCoins(int coins) {
        if (!(coins >= 0)) return false;

        int before = this.coins;
        this.coins += coins;

        callCoinsChangeEvent(before, ChangeType.ADD);
        return true;
    }

    public boolean removeCoins(int coins) {
        if (!(coins >= 0) || !(this.coins - coins >= 0)) return false;

        int before = this.coins;
        this.coins -= coins;

        callCoinsChangeEvent(before, ChangeType.REMOVE);
        return true;
    }

    public boolean setCoins(int coins) {
        if (!(coins >= 0)) return false;

        int before = this.coins;
        this.coins = coins;

        callCoinsChangeEvent(before, ChangeType.SET);
        return true;
    }

    public void clearCoins() {
        this.coins = 0;
    }

    private void callCoinsChangeEvent(int before, ChangeType changeType) {
        pluginManager.callEvent(new PlayerCoinsChangeEvent(this, before, this.coins, changeType));
    }
}