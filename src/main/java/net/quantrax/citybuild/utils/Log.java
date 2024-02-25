package net.quantrax.citybuild.utils;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class Log {

    private static final Logger LOGGER = Bukkit.getLogger();

    @FormatMethod
    public static void severe(@FormatString @NotNull String template, Object... args) {
        LOGGER.severe(String.format(template, args));
    }

    @FormatMethod
    public static void info(@FormatString @NotNull String template, Object... args) {
        LOGGER.info(String.format(template, args));
    }

    @FormatMethod
    public static void warn(@FormatString @NotNull String template, Object... args) {
        LOGGER.warning(String.format(template, args));
    }
}