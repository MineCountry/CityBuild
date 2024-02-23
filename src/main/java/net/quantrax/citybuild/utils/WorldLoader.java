package net.quantrax.citybuild.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

public final class WorldLoader {

    public static void loadWorlds(@NotNull String... worlds) {
        for (String world : worlds) {
            if (Bukkit.getWorld(world) != null) continue;
            new WorldCreator(world).environment(World.Environment.NORMAL).createWorld();
        }
    }
}