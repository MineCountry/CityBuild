package net.quantrax.citybuild.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface Clickable {

    void click(@NotNull InventoryClickEvent event);

}