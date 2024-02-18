package net.quantrax.citybuild.backend.dao.impl.entity;

import net.quantrax.citybuild.backend.dao.Entity;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record LocationEntity(@NotNull String name, @NotNull Location location) implements Entity<String> {
}