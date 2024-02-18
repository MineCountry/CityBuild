package net.quantrax.citybuild.utils;

import com.moandjiezana.toml.Toml;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Messenger {

    private Toml toml;
    private String message;
    private CommandSender sender;
    private List<Replacement<?>> replacements;

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull MessengerBuilder builder(@NotNull Toml toml) {
        return new MessengerBuilder(toml);
    }

    public void send() {
        sender.sendMessage(ComponentTranslator.withReplacements(toml, message, replacements));
    }

    public record Replacement<T>(@NotNull String placeholder, @NotNull T replacement) {
    }

    @RequiredArgsConstructor
    public static class MessengerBuilder {

        private final Toml toml;
        private String message = null;
        private CommandSender sender = null;
        private List<Replacement<?>> replacements = Collections.emptyList();

        public MessengerBuilder sender(@NotNull CommandSender sender) {
            this.sender = sender;
            return this;
        }

        public MessengerBuilder message(@NotNull String message) {
            this.message = message;
            return this;
        }

        public MessengerBuilder replacements(Replacement<?>... replacements) {
            this.replacements = Arrays.asList(replacements);
            return this;
        }

        public Messenger build() {
            return new Messenger(toml, message, sender, replacements);
        }
    }
}