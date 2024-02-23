package net.quantrax.citybuild.backend.dao.impl.repository;

import net.quantrax.citybuild.backend.dao.Repository;
import net.quantrax.citybuild.backend.dao.impl.entity.LocationEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.adapter.StaticQueryAdapter.builder;

public class LocationRepository implements Repository<LocationEntity> {

    @Override
    public void save(@NotNull LocationEntity locationEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<List<LocationEntity>> findAll() {
        return builder(LocationEntity.class)
                .queryWithoutParams("SELECT * FROM utility.location;")
                .readRow(row -> {
                    String name = row.getString("name");
                    Location location = new Location(Bukkit.getWorld(row.getString("world")), row.getDouble("x"), row.getDouble("y"), row.getDouble("z"),
                            row.getFloat("yaw"), row.getFloat("pitch"));

                    return new LocationEntity(name, location);
                })
                .all();
    }

    public CompletableFuture<Map<String, Location>> findAllMapped() {
        return findAll().thenCompose(this::map);
    }

    private @NotNull CompletableFuture<Map<String, Location>> map(@NotNull List<LocationEntity> entities) {
        Map<String, Location> map = new HashMap<>();

        entities.forEach(entity -> map.put(entity.name(), entity.location()));

        return CompletableFuture.supplyAsync(() -> map);
    }
}