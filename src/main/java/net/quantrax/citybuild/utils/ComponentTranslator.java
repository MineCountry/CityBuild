package net.quantrax.citybuild.utils;

import com.moandjiezana.toml.Toml;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComponentTranslator {

    public static @NotNull Component fromString(@NotNull String path) {
        return MiniMessage.miniMessage().deserialize(path, TagResolver.standard());
    }

    public static @NotNull Component fromConfig(@NotNull Toml toml, @NotNull String path) {
        String input = toml.getString(path);
        Preconditions.state(input != null, "Could not find %s in config.toml", path);

        return fromString(input);
    }

    public static @NotNull Component withReplacements(@NotNull Toml toml, @NotNull String path, @NotNull List<Replacement<?>> replacements) {
        if (replacements.isEmpty()) return fromConfig(toml, path);

        String raw = toml.getString(path);
        Preconditions.state(raw != null, "Could not find %s in config.toml", path);

        String parsed = raw;
        for (Replacement<?> replacement : replacements) parsed = parsed.replace(replacement.placeholder(), String.valueOf(replacement.replacement()));

        return fromString(parsed);
    }

}
