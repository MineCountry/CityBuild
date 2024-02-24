package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.cache.LocationCache;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.Messenger;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("nether")
public class NetherCommand extends BaseCommand {

    @Dependency private Toml toml;
    @Dependency private MessageCache messageCache;
    @Dependency private LocationCache locationCache;

    @Default
    @Description("Teleport dich ins Nether")
    public void onDefault(Player player) {
        Location location = locationCache.get(toml.getString("nether-spawn-name"));

        if (location == null) {
            Messenger.builder(messageCache).sender(player).message("no-spawn").build().send();
            return;
        }

        player.teleport(location);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8F, 1.0F);
        Messenger.builder(messageCache).sender(player).message("nether-teleport").build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}