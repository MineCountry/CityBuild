package net.quantrax.citybuild.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.inventory.impl.StrangerInventory.ImmutableStrangerInventory;
import net.quantrax.citybuild.utils.ComponentTranslator;
import net.quantrax.citybuild.utils.ItemBuilder;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@CommandAlias("invsee")
@CommandPermission("citybuild.command.invsee")
public class InvseeCommand extends BaseCommand {

    @Dependency MessageCache cache;

    @Default
    @Syntax("<Name>")
    @CommandCompletion("@players")
    @Description("Zeigt das Inventar eines Spielers")
    public void onDefault(@NotNull Player player, @NotNull @Flags("other") Player target) {
        Component title = ComponentTranslator.withReplacements(cache, "invsee-inventory-name", List.of(new Replacement<>("%target%", target.getName())));
        ImmutableStrangerInventory inventory = new ImmutableStrangerInventory(player, title);

        applyLayout(target, inventory.getInventory());

        inventory.openInventory();
    }

    @Subcommand("modify")
    @Syntax("<Name>")
    @CommandCompletion("@players")
    @CommandPermission("citybuild.command.invsee.modify")
    @Description("Zeigt das veränderbare Inventar eines Spielers an")
    public void onModify(@NotNull Player player, @Flags("other") @NotNull Player target) {
        player.openInventory(target.getInventory());
    }

    @HelpCommand
    @Syntax("<Page>")
    @Description("Zeigt diese UI")
    public void onHelp(@NotNull CommandHelp help) {
        help.showHelp();
    }

    private void applyLayout(Player player, @NotNull Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).build());
        }

        for (int i = 0; i <= player.getInventory().getSize(); i++) {
            if (i <= 8) {
                inventory.setItem(i, player.getInventory().getItem(i));
                continue;
            }

            if (i == 41) {
                inventory.setItem(50, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).build());
                continue;
            }

            inventory.setItem(i + 9, player.getInventory().getItem(i));
        }
    }
}