package net.quantrax.citybuild.global;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.utils.Preconditions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CityBuildPlayer {

    private final UUID uuid;
    private int coins;

    @Internal
    @Contract("_ -> new")
    public static @NotNull CityBuildPlayer create(@NotNull Player player) {
        return new CityBuildPlayer(player.getUniqueId());
    }

    public void addCoins(int coins) {
        Preconditions.state(coins >= 0, "Negative coins cannot be added");
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        Preconditions.state(coins >= 0, "Negative coins cannot be removed");
        Preconditions.state(this.coins - coins >= 0, "The difference cannot be negative");
        this.coins -= coins;
    }

    public void setCoins(int coins) {
        Preconditions.state(coins >= 0, "Coins cannot be negative");
        this.coins = coins;
    }
}
