package net.quantrax.citybuild.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class CustomInventory implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;

    public CustomInventory(@NotNull Player player, int size, @NotNull Component title) {
        this.player = player;
        inventory = Bukkit.createInventory(this, size, title);
    }

    public void openInventory() {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}
