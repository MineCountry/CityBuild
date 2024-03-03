package net.quantrax.citybuild.listener;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.events.PlayerCoinsChangeEvent;
import net.quantrax.citybuild.utils.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

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

        try (WebhookClient client = WebhookClient.withUrl(System.getProperty("WEBHOOK_URL"))) {
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new EmbedTitle("Plausibilitätscheck", null))
                    .setDescription(String.format("Unplausible Werte für Coins bei Spieler %s (%s)", player.getName(), after))
                    .setFooter(new EmbedFooter(String.format("Festgestellt durch Sensor ChangeType#%s", event.changeType().name()), null))
                    .setTimestamp(Instant.now())
                    .setColor(0xFF0000)
                    .build();

            client.send(embed).exceptionally(throwable -> {
                Log.severe("Executing webhook failed with an exception: %s", throwable.getMessage());
                return null;
            });
        }
    }
}