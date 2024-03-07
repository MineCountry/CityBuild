package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.quantrax.citybuild.CityBuildPlugin;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.vanish.VanishManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@CommandPermission("vanish.use")
@CommandAlias("vanish")
public class VanishCommand extends BaseCommand {

    @Dependency private MessageCache cache;

    private final VanishManager vanishManager;

    public VanishCommand(CityBuildPlugin plugin) {
        this.vanishManager = VanishManager.getInstance(plugin);
    }

    @Default
    @Description("Versteckt dich")
    public void onDefault(@NotNull Player player) {
        if (vanishManager.isVanished(player)) {
            vanishManager.unVanish(player);
            Messenger.builder(cache).sender(player).message("vanish-self-off").build().send();
            return;
        }
        vanishManager.vanish(player);
        Messenger.builder(cache).sender(player).message("vanish-self-on").build().send();
    }

    @Subcommand("player")
    @Syntax("<Name>")
    @CommandCompletion("@players")
    @Description("Verstecke einen anderen Spieler")
    public void onOther(@NotNull Player player, @Flags("other") @NotNull Player arg) {
        if (vanishManager.isVanished(arg)) {
            vanishManager.unVanish(arg);
            Messenger.builder(cache).sender(player).message("vanish-other-off").target(arg.getName()).build().send();
            Messenger.builder(cache).sender(arg).message("vanish-other-off-notify").target(arg.getName()).build().send();
            return;
        }
        vanishManager.vanish(arg);
        Messenger.builder(cache).sender(player).message("vanish-other-on").target(arg.getName()).build().send();
        Messenger.builder(cache).sender(arg).message("vanish-other-on-notify").target(arg.getName()).build().send();
    }

}
