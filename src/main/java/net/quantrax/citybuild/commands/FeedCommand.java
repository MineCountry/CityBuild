package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("feed")
@CommandPermission("citybuild.command.feed")
public class FeedCommand extends BaseCommand {

    @Dependency private Toml toml;

    @Default
    @Description("Sättigt dich")
    public void onDefault(@NotNull Player player) {
        player.setFoodLevel(20);
        Messenger.builder(toml).sender(player).message("essentials.feed-self").build().send();
    }

    @Subcommand("player")
    @Syntax("<Name>")
    @CommandCompletion("@players")
    @Description("Sättigt einen anderen Spieler")
    public void onOther(@NotNull Player player, @Flags("other") @NotNull Player arg) {
        if (player.equals(arg)) {
            Messenger.builder(toml).sender(player).message("essentials.feed-other-self").build().send();
            return;
        }

        arg.setFoodLevel(20);

        Messenger.builder(toml).sender(player).message("essentials.feed-other").replacements(new Replacement<>("%target%", arg.getName())).build().send();
        Messenger.builder(toml).sender(arg).message("essentials.feed-other-notify").replacements(new Replacement<>("%target%", player.getName())).build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}