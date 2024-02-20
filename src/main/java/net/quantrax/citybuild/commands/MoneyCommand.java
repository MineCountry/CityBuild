package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("money")
public class MoneyCommand extends BaseCommand {

    @Dependency private MessageCache messageCache;
    @Dependency private PlayerCache playerCache;

    @Default
    @Description("Zeigt deinen aktuellen Kontostand")
    public void onDefault(Player player) {
        CityBuildPlayer cityBuildPlayer = playerCache.get(player);

        Messenger.builder(messageCache).sender(player).message("balance-own").balance(cityBuildPlayer.coins()).build().send();
    }

    @Subcommand("player")
    @Syntax("<Name>")
    @CommandCompletion("@players")
    @CommandPermission("citybuild.command.money")
    @Description("Zeigt den Kontostand eines anderen Spielers")
    public void onPlayer(@NotNull Player player, @Flags("other") Player arg) {
        CityBuildPlayer cityBuildPlayer = playerCache.get(arg);

        Messenger.builder(messageCache).sender(player).message("balance-other").target(arg.getName()).balance(cityBuildPlayer.coins()).build().send();
    }

    @Subcommand("add")
    @Syntax("<Name> <Betrag>")
    @CommandCompletion("@players")
    @CommandPermission("citybuild.command.money")
    @Description("Fügt einem Spieler einen Betrag hinzu")
    public void onMoneyAdd(@NotNull Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer cityBuildPlayer = playerCache.get(arg);

        boolean successful = cityBuildPlayer.addCoins(value);

        if (!successful) {
            Messenger.builder(messageCache).sender(player).message("add-failure").build().send();
            return;
        }

        Messenger.builder(messageCache).sender(player).message("add-success").target(arg.getName()).amount(value).build().send();
    }

    @Subcommand("remove")
    @Syntax("<Name> <Betrag>")
    @CommandPermission("citybuild.command.money")
    @CommandCompletion("@players")
    @Description("Entfernt einen Spieler einen Betrag vom Konto")
    public void onMoneyRemove(Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer cityBuildPlayer = playerCache.get(arg);

        boolean successful = cityBuildPlayer.removeCoins(value);

        if (!successful) {
            Messenger.builder(messageCache).sender(player).message("remove-failure").build().send();
            return;
        }

        Messenger.builder(messageCache).sender(player).message("remove-success").target(arg.getName()).amount(value).build().send();
    }

    @Subcommand("set")
    @Syntax("<Name> <Betrag>")
    @CommandPermission("citybuild.command.money")
    @CommandCompletion("@players")
    @Description("Setzt den Kontostand eines Spielers auf einen Betrag")
    public void onMoneySet(@NotNull Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer cityBuildPlayer = playerCache.get(arg);

        boolean successful = cityBuildPlayer.setCoins(value);

        if (!successful) {
            Messenger.builder(messageCache).sender(player).message("set-failure").build().send();
            return;
        }

        Messenger.builder(messageCache).sender(player).message("set-success").target(arg.getName()).amount(value).build().send();
    }

    @Subcommand("clear")
    @Syntax("<Name>")
    @CommandPermission("citybuild.command.money")
    @CommandCompletion("@players")
    @Description("Leert den Kontostand eines Spielers")
    public void onClear(Player player, @Flags("other") Player arg) {
        CityBuildPlayer cityBuildPlayer = playerCache.get(arg);

        cityBuildPlayer.clearCoins();
        Messenger.builder(messageCache).sender(player).message("clear").target(arg.getName()).build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}