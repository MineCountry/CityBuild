package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("pay")
public class PayCommand extends BaseCommand {

    @Dependency private Toml toml;
    @Dependency private PlayerCache cache;

    @Default
    @Syntax("<Name> <Betrag>")
    @Description("Überweist einem Spieler einen Betrag")
    public void onPay(@NotNull Player player, @Flags("other") Player arg, int value) {
        CityBuildPlayer payer = cache.get(player);
        CityBuildPlayer receiver = cache.get(arg);

        if (player.equals(arg)) {
            Messenger.builder(toml).sender(player).message("money.pay-self").build().send();
            return;
        }

        if (value < 0) {
            Messenger.builder(toml).sender(player).message("money.pay-failure").build().send();
            return;
        }

        if (payer.coins() < value) {
            Messenger.builder(toml).sender(player).message("money.pay-not-enough").build().send();
            return;
        }

        payer.removeCoins(value);
        Messenger.builder(toml).sender(player).message("money.pay-sender").replacements(new Replacement<>("%target%", arg.getName()), new Replacement<>("%amount%", value)).build().send();

        receiver.addCoins(value);
        Messenger.builder(toml).sender(arg).message("money.pay-receiver").replacements(new Replacement<>("%target%", arg.getName()), new Replacement<>("%amount%", value)).build().send();
    }

    @HelpCommand
    @Syntax("<help>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}