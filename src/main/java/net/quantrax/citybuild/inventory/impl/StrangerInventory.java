package net.quantrax.citybuild.inventory.impl;

import net.kyori.adventure.text.Component;
import net.quantrax.citybuild.inventory.CustomInventory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class StrangerInventory extends CustomInventory {

    public StrangerInventory(@NotNull Player player, int size, @NotNull Component title) {
        super(player, size, title);
    }

    public static class ImmutableStrangerInventory extends StrangerInventory {
        public ImmutableStrangerInventory(Player player, Component title) {
            super(player, 9 * 6, title);
        }
    }
}