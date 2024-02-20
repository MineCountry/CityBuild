package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("pay")
public class PayCommand extends BaseCommand {

    @Dependency private MessageCache messageCache;
    @Dependency private PlayerCache playerCache;

    @Default
    @Syntax("<Name> <Betrag>")
    @Description("Überweist einem Spieler einen Betrag")
    public void onPay(@NotNull Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer payer = playerCache.get(player);
        CityBuildPlayer receiver = playerCache.get(arg);

        if (player.equals(arg)) {
            Messenger.builder(messageCache).sender(player).message("pay-self").build().send();
            return;
        }

        if (value < 0) {
            Messenger.builder(messageCache).sender(player).message("pay-failure").build().send();
            return;
        }

        if (payer.coins() < value) {
            Messenger.builder(messageCache).sender(player).message("pay-not-enough").build().send();
            return;
        }

        payer.removeCoins(value);
        Messenger.builder(messageCache).sender(player).message("pay-sender").replacements(new Replacement<>("%target%", arg.getName()), new Replacement<>("%amount%", value)).build().send();

        receiver.addCoins(value);
        Messenger.builder(messageCache).sender(arg).message("pay-receiver").replacements(new Replacement<>("%target%", arg.getName()), new Replacement<>("%amount%", value)).build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}