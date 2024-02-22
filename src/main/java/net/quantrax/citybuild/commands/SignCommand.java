package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.ComponentTranslator;
import net.quantrax.citybuild.utils.ItemBuilder;
import net.quantrax.citybuild.utils.Messenger;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@CommandAlias("sign")
@CommandPermission("citybuild.command.sign")
public class SignCommand extends BaseCommand {

    @Dependency private MessageCache cache;

    @Default
    @Syntax("<Text>")
    @Description("Signiert ein Item mit einem Text")
    public void onDefault(@NotNull Player player, @NotNull String text) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType() == Material.AIR) {
            Messenger.builder(cache).sender(player).message("sign-no-block").build().send();
            return;
        }

        Date date = Date.from(Instant.now());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Component message = ComponentTranslator.fromString(text).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD);
        Component signMessage = ComponentTranslator.withReplacements(cache, "sign-by-at-date", List.of(new Replacement<>("%date%", simpleDateFormat.format(date)), new Replacement<>("%target%", player.getName())));

        ItemStack signed = new ItemBuilder(itemStack).lore(Component.empty(), message, signMessage).build();

        player.getInventory().setItemInMainHand(signed);
        Messenger.builder(cache).sender(player).message("sign-success").build().send();
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }
}