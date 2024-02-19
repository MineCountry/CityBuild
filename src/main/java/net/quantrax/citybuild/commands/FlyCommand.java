package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.utils.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("fly")
@CommandPermission("citybuild.command.fly")
public class FlyCommand extends BaseCommand {

    @Dependency private Toml toml;

    @Default
    @Description("Ändert deinen Flugstatus")
    public void onDefault(@NotNull Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);

            Messenger.builder(toml).sender(player).message("essentials.fly-disallow").build().send();
            return;
        }

        player.setAllowFlight(true);
        Messenger.builder(toml).sender(player).message("essentials.fly-allow").build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}