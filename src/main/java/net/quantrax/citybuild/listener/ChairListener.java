package net.quantrax.citybuild.listener;

import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.CityBuildPlugin;
import net.quantrax.citybuild.utils.Chair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ChairListener implements Listener {

    private final Set<Material> stairMaterials = createMaterialSet();
    private final Collection<Location> occupiedLocations = new ArrayList<>();
    private final Collection<Player> sittingPlayers = new ArrayList<>();
    private final Map<Player, ArmorStand> chairs = new ConcurrentHashMap<>();
    private final Map<Player, Long> lastInteract = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastLocation = new HashMap<>();

    private final CityBuildPlugin plugin;

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (lastInteract.getOrDefault(player, 0L) > System.currentTimeMillis()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() != null) return;
        if (block == null) return;
        if (!stairMaterials.contains(block.getType())) return;
        if (!(block.getBlockData() instanceof Stairs stairs)) return;
        if (stairs.getShape() != Stairs.Shape.STRAIGHT) return;
        if (stairs.getHalf() != Bisected.Half.BOTTOM) return;
        if (occupiedLocations.contains(block.getLocation())) return;
        if (sittingPlayers.contains(player)) return;

        lastLocation.put(player.getUniqueId(), player.getLocation());

        new Chair(plugin, block).use(player, chair -> {
            lastInteract.put(player, System.currentTimeMillis() + 3_000L);
            chairs.put(player, chair.armorStand());
            occupiedLocations.add(block.getLocation());
            sittingPlayers.add(player);

        }, onStandUp -> {
            sittingPlayers.remove(player);
            occupiedLocations.remove(block.getLocation());
            chairs.get(player).remove();
            chairs.remove(player);
        });
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!sittingPlayers.contains(player)) return;
        chairs.get(player).remove();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        teleport(event.getPlayer());
    }

    @EventHandler
    public void onToggleSneak(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!sittingPlayers.contains(player)) return;

        teleport(player);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        event.setCancelled(sittingPlayers.contains(event.getPlayer()));
    }

    private void teleport(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        if (!lastLocation.containsKey(uuid)) return;

        player.teleport(lastLocation.get(uuid));
        lastLocation.remove(uuid);
    }

    @Contract(pure = true)
    private @NotNull Set<Material> createMaterialSet() {
        Set<Material> set = new HashSet<>();

        for (Material material : Material.values()) {
            if (!material.name().contains("STAIR")) continue;
            if (material.name().contains("LEGACY")) continue;

            set.add(material);
        }

        return set;
    }
}