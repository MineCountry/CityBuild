package net.quantrax.citybuild.listener;

import net.quantrax.citybuild.inventory.Clickable;
import net.quantrax.citybuild.inventory.CustomInventory;
import net.quantrax.citybuild.utils.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        try {
            InventoryHolder holder = inventory.getHolder();

            if (holder == null) return;
            if (!(holder instanceof CustomInventory customInventory)) return;

            if (customInventory instanceof Clickable clickable) {
                clickable.click(event);
                return;
            }

            event.setCancelled(true);

        } catch (NullPointerException exception) {
            Log.severe("Running inventory logic failed: %s", exception.getMessage());
        }
    }
}