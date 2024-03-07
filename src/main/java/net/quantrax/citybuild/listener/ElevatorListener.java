package net.quantrax.citybuild.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.moandjiezana.toml.Toml;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;


public class ElevatorListener implements Listener {

    private final String worldName;

    public ElevatorListener(@NotNull Toml toml) {
        this.worldName = toml.getString("plotworld-name");
    }

    @EventHandler
    public void onSneak(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (!event.isSneaking()) return;
        if (!location.getWorld().getName().equalsIgnoreCase(worldName)) return;
        if (location.getBlock().getType() != Material.DAYLIGHT_DETECTOR) return;

        useElevator(player, location, Direction.DOWN);
    }

    @EventHandler
    public void onJump(@NotNull PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (!location.getWorld().getName().equalsIgnoreCase(worldName)) return;
        if (location.getBlock().getType() != Material.DAYLIGHT_DETECTOR) return;

        useElevator(player, location, Direction.UP);
    }

    private void useElevator(Player player, @NotNull Location location, Direction direction) {
        World world = location.getWorld();

        if (direction == Direction.UP) {
            for (int y = location.getBlockY() + 1; y <= world.getMaxHeight(); y++) {
                if (isNotLocationSuitable(player, location.getBlockX(), y, location.getBlockZ())) continue;
                return;
            }
            return;
        }

        // Direction is down

        for (int y = location.getBlockY() - 3; y >= 3; y--) {
            if (isNotLocationSuitable(player, location.getBlockX(), y, location.getBlockZ())) continue;
            return;
        }
    }

    private boolean isNotLocationSuitable(@NotNull Player player, int x, int y, int z) {
        Block block = player.getWorld().getBlockAt(x, y, z);

        if (block.getType() != Material.DAYLIGHT_DETECTOR) return true;
        if (!hasEmptySpaceAbove(block)) return true;

        Location location = player.getLocation();

        player.teleport(new Location(block.getWorld(), block.getX() + 0.5D, block.getY() + 0.3D, block.getZ() + 0.5D, location.getYaw(), location.getPitch()));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8F, 0.8F);
        return false;
    }

    private boolean hasEmptySpaceAbove(@NotNull Block block) {
        Location location = block.getLocation();
        return (location.add(0D, 1D, 0D).getBlock().isEmpty() && location.add(0D, 2.3D, 0D).getBlock().isEmpty());
    }

    private enum Direction {
        UP, 
        DOWN
    }
}