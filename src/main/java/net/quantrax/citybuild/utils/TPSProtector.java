package net.quantrax.citybuild.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import net.kyori.adventure.text.Component;
import net.quantrax.citybuild.CityBuildPlugin;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.DiscordWebhook.EmbedObject;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TPSProtector {

    public static final double LIMIT_SHUTDOWN = 7.1D,
            LIMIT_ALLOW_REDSTONE = 10.1D,
            LIMIT_WARN = 15.1D;

    private final ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(3);
    @Getter @Setter private boolean allowRedstone = true;
    @Setter private boolean sentWarn = false;

    private final CityBuildPlugin plugin;
    private final MessageCache cache;

    public void supervice() {
        RegisteredServiceProvider<Spark> provider = Bukkit.getServicesManager().getRegistration(Spark.class);

        if (provider == null) {
            Log.warn("Disabling supervision of server tps");
            return;
        }

        Spark spark = provider.getProvider();

        threadPool.scheduleAtFixedRate(() -> {
            DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
            if (tps == null) return;

            double tpsLast10Secs = tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10);

            // Running relaxing tps logic

            if (tpsLast10Secs > LIMIT_WARN && sentWarn) {
                DiscordWebhook webhook = new DiscordWebhook(System.getProperty("WEBHOOK_URL"));
                webhook.addEmbed(new EmbedObject().title("TPS-Protection").description("TPS (letzte 10s) liegt wieder bei 15").color(Color.decode("#62FF00")));
                executeWebhook(webhook);

                sentWarn(false);
                return;
            }

            if (tpsLast10Secs > LIMIT_ALLOW_REDSTONE && !allowRedstone) {
                Log.info("TPS is again over 10. Allowing redstone..");
                allowRedstone(true);
                broadcast(3);
                return;
            }

            // Running stressed tps logic

            if (inRange(tpsLast10Secs, LIMIT_WARN, LIMIT_ALLOW_REDSTONE) && !sentWarn) {
                Log.warn("TPS (last 10s) dropped below 15");
                broadcast(1);

                DiscordWebhook webhook = new DiscordWebhook(System.getProperty("WEBHOOK_URL"));
                webhook.addEmbed(new EmbedObject().title("TPS-Protection").description("TPS (letzte 10s) ist unter 15 gefallen").color(Color.decode("#007BFF")));
                executeWebhook(webhook);

                sentWarn(true);
                return;
            }

            if (inRange(tpsLast10Secs, LIMIT_ALLOW_REDSTONE, LIMIT_SHUTDOWN) && allowRedstone) {
                Log.warn("TPS (last 10s) dropped below 10. Disallowing redstone..");
                allowRedstone(false);
                broadcast(2);

                DiscordWebhook webhook = new DiscordWebhook(System.getProperty("WEBHOOK_URL"));
                webhook.addEmbed(new EmbedObject().title("TPS-Protection").description("TPS (letzte 10s) ist unter 10 gefallen. Deaktiviere Redstone").color(Color.ORANGE));
                executeWebhook(webhook);

                return;
            }

            if (tpsLast10Secs <= LIMIT_SHUTDOWN) {
                Log.warn("TPS (last 10s) dropped below 7. Shutting down server..");

                DiscordWebhook webhook = new DiscordWebhook(System.getProperty("WEBHOOK_URL"));
                webhook.addEmbed(new EmbedObject().title("TPS-Protection").description("TPS (letzte 10s) ist unter 7 gefallen. Stoppe Server").color(Color.RED));
                executeWebhook(webhook);

                Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().shutdown(), 20L);
            }

        }, 0L, 10, TimeUnit.SECONDS);
    }

    private boolean inRange(double lookup, double min, double max) {
        return (lookup <= min && lookup > max);
    }

    private void executeWebhook(@NotNull DiscordWebhook webhook) {
        try {
            webhook.execute();
        } catch (IOException exception) {
            Log.severe("Executing discord webhook failed with an exception: %s", exception.getMessage());
        }
    }

    private void broadcast(int alertLevel) {
        Component restriction = Component.empty();
        int tps = 20;

        switch (alertLevel) {
            case 1 -> {
                tps = 15;
                restriction = ComponentTranslator.fromCache(cache, "no-restrictions");
            }
            case 2 -> {
                tps = 10;
                restriction = ComponentTranslator.fromCache(cache, "disallow-redstone");
            }
            case 3 -> {
                tps = 10;
                restriction = ComponentTranslator.fromCache(cache, "allow-redstone");
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ComponentTranslator.fromCache(cache, "single-line"));
            player.sendMessage(ComponentTranslator.withReplacements(cache, "warning", List.of(new Replacement<>("%value%", tps))));
            player.sendMessage(Component.empty());
            player.sendMessage(ComponentTranslator.fromCache(cache, "restrictions"));
            player.sendMessage(restriction);
            player.sendMessage(ComponentTranslator.fromCache(cache, "single-line"));

            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8F, 1.0F);
        }
    }
}