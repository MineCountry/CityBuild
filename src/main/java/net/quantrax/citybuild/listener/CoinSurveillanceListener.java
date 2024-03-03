package net.quantrax.citybuild.listener;

import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.events.PlayerCoinsChangeEvent;
import net.quantrax.citybuild.utils.DiscordWebhook;
import net.quantrax.citybuild.utils.DiscordWebhook.EmbedObject;
import net.quantrax.citybuild.utils.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CoinSurveillanceListener implements Listener {

    private final int surveillanceLimit;

    public CoinSurveillanceListener(@NotNull Toml toml) {
        this.surveillanceLimit = Math.toIntExact(toml.getLong("unrealistic-coins-limit")) * 1_000_000;
    }

    @EventHandler
    public void onCoinChange(@NotNull PlayerCoinsChangeEvent event) {
        Player player = event.player().asBukkitPlayer();
        int after = event.after();

        if (player == null) return;
        if (after < surveillanceLimit) return;

        DiscordWebhook webhook = new DiscordWebhook(System.getProperty("WEBHOOK_URL"));
        webhook.addEmbed(new EmbedObject().title("Plausibilitätscheck").description("Fehler bei Coins von " + player.getName() + " (" + after + ")"));
        try {
            webhook.execute();
        } catch (IOException exception) {
            Log.severe("Executing webhook failed with an exception: %s", exception.getMessage());
        }
    }
}