package net.quantrax.citybuild.backend.dao.impl;

import net.quantrax.citybuild.backend.dao.Repository;
import net.quantrax.citybuild.global.CityBuildPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.adapter.StaticQueryAdapter.builder;

public class PlayerRepository implements Repository<CityBuildPlayer> {

    @Override
    public void save(@NotNull CityBuildPlayer player) {
        builder()
                .query("UPDATE citybuild.player_coins SET value=? WHERE player_uid=?;")
                .parameter(stmt -> stmt.setInt(player.coins()).setUuidAsString(player.uuid()))
                .update().send();
    }

    public CompletableFuture<Optional<CityBuildPlayer>> findByUUID(@NotNull UUID uuid) {
        return builder(CityBuildPlayer.class)
                .query("SELECT * FROM api.player WHERE uuid=?;")
                .parameter(stmt -> stmt.setUuidAsString(uuid))
                .readRow(CityBuildPlayer::fromRow)
                .first()
                .thenCompose(cityBuildPlayer -> fetchCoins(cityBuildPlayer.orElseThrow()));
    }

    @Override
    public CompletableFuture<Collection<CityBuildPlayer>> findAll() {
        throw new UnsupportedOperationException();
    }

    private @NotNull CompletableFuture<Optional<CityBuildPlayer>> fetchCoins(@NotNull CityBuildPlayer player) {
        int coins = builder(Integer.class)
                .query("SELECT value FROM citybuild.player_coins WHERE player_uid=?;")
                .parameter(stmt -> stmt.setUuidAsString(player.uuid()))
                .readRow(row -> row.getInt("value"))
                .firstSync().orElse(-1);

        player.setCoins(coins);

        return CompletableFuture.supplyAsync(() -> Optional.of(player));
    }

}