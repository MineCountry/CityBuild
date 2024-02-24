package net.quantrax.citybuild.listener;

import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.utils.TPSProtector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TPSProtectionListener implements Listener {

    private final TPSProtector protector;

    @EventHandler
    public void onRedstone(@NotNull BlockRedstoneEvent event) {
        if (!protector.allowRedstone()) {
            event.setNewCurrent(0);
        }
    }
}