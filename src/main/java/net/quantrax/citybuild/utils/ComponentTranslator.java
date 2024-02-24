package net.quantrax.citybuild.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComponentTranslator {

    public static @NotNull Component fromString(@NotNull String path) {
        return MiniMessage.miniMessage().deserialize(path, TagResolver.standard());
    }

    public static @NotNull Component fromCache(@NotNull MessageCache cache, @NotNull String key) {
        String input = cache.get(key);
        Preconditions.state(input != null, "Could not find %s in database", key);

        return fromString(input);
    }

    public static @NotNull Component withReplacements(@NotNull MessageCache cache, @NotNull String key, @NotNull List<Replacement<?>> replacements) {
        if (replacements.isEmpty()) return fromCache(cache, key);

        String raw = cache.get(key);
        Preconditions.state(raw != null, "Could not find %s in config.toml", key);

        String parsed = raw;
        for (Replacement<?> replacement : replacements) parsed = parsed.replace(replacement.placeholder(), String.valueOf(replacement.replacement()));

        return fromString(parsed);
    }
}