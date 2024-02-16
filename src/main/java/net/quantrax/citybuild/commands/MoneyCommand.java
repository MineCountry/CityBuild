package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.global.CityBuildPlayer;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("money")
@Description("Base command for money")
public class MoneyCommand extends BaseCommand {

    @Dependency private Toml toml;
    @Dependency private PlayerCache cache;

    @Default
    @Description("Shows your own current balance")
    public void onDefault(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            Messenger.builder(toml).sender(sender).message("no-player").build().send();
            return;
        }

        CityBuildPlayer cityBuildPlayer = cache.get(player);
        if (cityBuildPlayer == null) throw new NullPointerException("citybuildPlayer cannot be null");

        Messenger.builder(toml).sender(player).message("money.balance-own").replacements(new Replacement<>("%balance%", cityBuildPlayer.coins())).build().send();
    }

    @HelpCommand
    public void onHelp(Player player) {
        Messenger.builder(toml).sender(player).message("money.invalid-usage").build().send();
    }
}