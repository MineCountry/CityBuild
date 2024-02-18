package net.quantrax.citybuild.backend.dao.impl.repository;

import de.chojo.sadu.wrapper.util.UpdateResult;
import net.quantrax.citybuild.backend.dao.Repository;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Log;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.adapter.StaticQueryAdapter.builder;

public class PlayerRepository implements Repository<CityBuildPlayer> {

    public CityBuildPlayer create(@NotNull Player player) {
        builder()
                .query("INSERT INTO citybuild.player_coins (player_uid, value) VALUES (?, ?);")
                .parameter(stmt -> stmt.setUuidAsString(player.getUniqueId()).setInt(0))
                .insert().send()
                .exceptionally(throwable -> {
                    Log.severe("Creating player with uuid %s failed with an exception: %s", player.getUniqueId(), throwable.getMessage());
                    return new UpdateResult(0);
                });

        return CityBuildPlayer.create(player);
    }

    @Override
    public void save(@NotNull CityBuildPlayer player) {
        builder()
                .query("UPDATE citybuild.player_coins SET value=? WHERE player_uid=?;")
                .parameter(stmt -> stmt.setInt(player.coins()).setUuidAsString(player.uuid()))
                .update().send()
                .exceptionally(throwable -> {
                    Log.severe("Saving player with uuid %s failed with an exception: %s", player.uuid(), throwable.getMessage());
                    return new UpdateResult(0);
                });
    }

    public CompletableFuture<Optional<CityBuildPlayer>> findByUUID(@NotNull UUID uuid) {
        return builder(CityBuildPlayer.class)
                .query("SELECT * FROM api.player WHERE uuid=?;")
                .parameter(stmt -> stmt.setUuidAsString(uuid))
                .readRow(CityBuildPlayer::fromRow)
                .first()
                .thenCompose(cityBuildPlayer -> fetchCoins(cityBuildPlayer.orElseThrow()))
                .exceptionally(throwable -> {
                    Log.severe("Fetching player data from uuid %s failed with an exception: %s", uuid, throwable.getMessage());
                    return Optional.empty();
                });
    }

    @Override
    public CompletableFuture<List<CityBuildPlayer>> findAll() {
        throw new UnsupportedOperationException();
    }

    private @NotNull CompletableFuture<Optional<CityBuildPlayer>> fetchCoins(@NotNull CityBuildPlayer player) {
        int coins = builder(Integer.class)
                .query("SELECT value FROM citybuild.player_coins WHERE player_uid=?;")
                .parameter(stmt -> stmt.setUuidAsString(player.uuid()))
                .readRow(row -> row.getInt("value"))
                .firstSync().orElse(0);

        player.setCoins(coins);

        return CompletableFuture.supplyAsync(() -> Optional.of(player));
    }
}