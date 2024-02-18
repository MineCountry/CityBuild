package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.CityBuildPlugin;
import net.quantrax.citybuild.backend.cache.LocationCache;
import net.quantrax.citybuild.utils.Messenger;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("farmwelt")
public class FarmworldCommand extends BaseCommand {

    @Dependency CityBuildPlugin plugin;
    @Dependency private Toml toml;
    @Dependency private LocationCache cache;

    @Default
    @Description("Teleportiert den Spieler in die Farmwelt")
    public void onDefault(Player player) {
        Location location = cache.get(toml.getString("farmworld.spawn-name"));

        if (location == null) {
            Messenger.builder(toml).sender(player).message("no-spawn").build().send();
            return;
        }

        player.teleport(location);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8F, 1.0F);
        Messenger.builder(toml).sender(player).message("farmworld.teleport").build().send();
    }

    @HelpCommand
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}