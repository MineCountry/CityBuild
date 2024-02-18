package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.utils.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("heal")
@CommandPermission("citybuild.command.heal")
public class HealCommand extends BaseCommand {

    @Dependency private Toml toml;

    @Default
    @Description("Heilt dich")
    public void onDefault(@NotNull Player player) {
        player.setHealthScale(20.0D);
        player.setFoodLevel(20);
        Messenger.builder(toml).sender(player).message("essentials.heal-self").build().send();
    }

    @Subcommand("player")
    @Syntax("<Name>")
    @CommandCompletion("@players")
    @Description("Heilt einen anderen Spieler")
    public void onOther(@NotNull Player player, @Flags("other") @NotNull Player arg) {
        if (player.equals(arg)) {
            Messenger.builder(toml).sender(player).message("essentials.heal-other-self").build().send();
            return;
        }

        arg.setHealthScale(20.0D);
        arg.setFoodLevel(20);

        Messenger.builder(toml).sender(player).message("essentials.heal-other").replacements(new Messenger.Replacement<>("%target%", arg.getName())).build().send();
        Messenger.builder(toml).sender(arg).message("essentials.heal-other-notify").replacements(new Messenger.Replacement<>("%target%", player.getName())).build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}