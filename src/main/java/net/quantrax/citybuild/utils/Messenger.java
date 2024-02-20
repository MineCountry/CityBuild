package net.quantrax.citybuild.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.backend.cache.MessageCache;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Messenger {

    private MessageCache cache;
    private String message;
    private CommandSender sender;
    private List<Replacement<?>> replacements;

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull MessengerBuilder builder(@NotNull MessageCache cache) {
        return new MessengerBuilder(cache);
    }

    public void send() {
        sender.sendMessage(ComponentTranslator.withReplacements(cache, message, replacements));
    }

    public record Replacement<T>(@NotNull String placeholder, @NotNull T replacement) {
    }

    @RequiredArgsConstructor
    public static class MessengerBuilder {

        private final MessageCache cache;
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

        public MessengerBuilder target(@NotNull String replacement) {
            this.replacements.add(new Replacement<>("%target%", replacement));
            return this;
        }

        public MessengerBuilder amount(int replacement) {
            this.replacements.add(new Replacement<>("%amount%", replacement));
            return this;
        }

        public MessengerBuilder balance(int replacement) {
            this.replacements.add(new Replacement<>("%balance%", replacement));
            return this;
        }

        public MessengerBuilder replacements(Replacement<?>... replacements) {
            this.replacements = Arrays.asList(replacements);
            return this;
        }

        public Messenger build() {
            return new Messenger(cache, message, sender, replacements);
        }
    }
}