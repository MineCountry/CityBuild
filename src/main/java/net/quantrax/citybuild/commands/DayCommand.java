package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.utils.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("day")
@CommandPermission("citybuild.command.day")
public class DayCommand extends BaseCommand {

    @Dependency private Toml toml;

    @Default
    @Description("Setzt die Uhrzeit in der Welt auf Tag")
    public void onDefault(@NotNull Player player) {
        player.getWorld().setTime(0L);
        Messenger.builder(toml).sender(player).message("essentials.day").build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}