package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.kyori.adventure.text.Component;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import net.quantrax.citybuild.utils.Preconditions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("money")
public class MoneyCommand extends BaseCommand {

    @Dependency private Toml toml;
    @Dependency private PlayerCache cache;

    @Default
    @Description("Zeigt deinen aktuellen Kontostand")
    public void onDefault(Player player) {
        CityBuildPlayer cityBuildPlayer = cache.get(player);

        Messenger.builder(toml).sender(player).message("money.balance-own").replacements(new Replacement<>("%balance%", cityBuildPlayer.coins())).build().send();
    }

    @Subcommand("player")
    @Syntax("<Name>")
    @CommandCompletion("@players")
    @CommandPermission("citybuild.command.money")
    @Description("Zeigt den Kontostand eines anderen Spielers")
    public void onPlayer(@NotNull Player player, @Flags("other") Player arg) {
        CityBuildPlayer cityBuildPlayer = cache.get(arg);

        Messenger.builder(toml).sender(player).message("money.balance-other").replacements(new Replacement<>("%balance%", cityBuildPlayer.coins()), new Replacement<>("%target%", arg.getName())).build().send();
    }

    @Subcommand("add")
    @Syntax("<Name> <Betrag>")
    @CommandCompletion("@players")
    @CommandPermission("citybuild.command.money")
    @Description("Fügt einem Spieler einen Betrag hinzu")
    public void onMoneyAdd(@NotNull Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer cityBuildPlayer = cache.get(arg);

        boolean successful = cityBuildPlayer.addCoins(value);

        if (!successful) {
            Messenger.builder(toml).sender(player).message("money.add-failure").build().send();
            return;
        }

        Messenger.builder(toml).sender(player).message("money.add-success").replacements(new Replacement<>("%target%", arg.getName()), new Replacement<>("%amount%", value)).build().send();
    }

    @Subcommand("remove")
    @Syntax("<Name> <Betrag>")
    @CommandPermission("citybuild.command.money")
    @CommandCompletion("@players")
    @Description("Entfernt einen Spieler einen Betrag vom Konto")
    public void onMoneyRemove(Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer cityBuildPlayer = cache.get(arg);

        boolean successful = cityBuildPlayer.removeCoins(value);

        if (!successful) {
            Messenger.builder(toml).sender(player).message("money.remove-failure").build().send();
            return;
        }

        Messenger.builder(toml).sender(player).message("money.remove-success").replacements(new Replacement<>("%target%", arg.getName()), new Replacement<>("%amount%", value)).build().send();
    }

    @Subcommand("set")
    @Syntax("<Name> <Betrag>")
    @CommandPermission("citybuild.command.money")
    @CommandCompletion("@players")
    @Description("Setzt den Kontostand eines Spielers auf einen Betrag")
    public void onMoneySet(@NotNull Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer cityBuildPlayer = cache.get(arg);

        boolean successful = cityBuildPlayer.setCoins(value);

        if (!successful) {
            Messenger.builder(toml).sender(player).message("money.set-failure").build().send();
            return;
        }

        Messenger.builder(toml).sender(player).message("money.set-success").replacements(new Replacement<>("%target%", arg.getName()), new Replacement<>("%amount%", value)).build().send();
    }

    @Subcommand("clear")
    @Syntax("<Name>")
    @CommandPermission("citybuild.command.money")
    @CommandCompletion("@players")
    @Description("Leert den Kontostand eines Spielers")
    public void onClear(Player player, @Flags("other") Player arg) {
        CityBuildPlayer cityBuildPlayer = cache.get(arg);

        cityBuildPlayer.clearCoins();
        Messenger.builder(toml).sender(player).message("money.clear").replacements(new Replacement<>("%target%", arg.getName())).build().send();
    }

    @HelpCommand
    @Syntax("<help>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}