package net.quantrax.citybuild.utils;

import com.moandjiezana.toml.Toml;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.quantrax.citybuild.utils.Messenger.Replacement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentTranslatorTest {

    private Toml toml;

    @BeforeEach
    public void setUp() {
        toml = new Toml();
        toml.read(getClass().getClassLoader().getResourceAsStream("test.toml"));
    }

    @Test
    public void testFromString() {
        String input = "Hier existiert kein Replacement";
        Component expected = MiniMessage.miniMessage().deserialize(input, TagResolver.standard());

        Component result = ComponentTranslator.fromString(input);

        assertEquals(expected, result);
    }

    @Test
    public void testFromConfig() {
        String path = "without-replacement";
        Component expected = MiniMessage.miniMessage().deserialize("Hier existiert kein Replacement", TagResolver.standard());

        Component result = ComponentTranslator.fromDatabase(toml, path);

        assertEquals(expected, result);
    }

    @Test
    public void testWithReplacements() {
        String path = "section.with-replacement";
        List<Replacement<?>> replacements = new ArrayList<>();
        replacements.add(new Replacement<>("%value1%", "StringValue"));
        replacements.add(new Replacement<>("%value2%", 100));

        Component expected = MiniMessage.miniMessage().deserialize("Hier giebt es zwei Replacements: a) StringValue und 2) 100", TagResolver.standard());

        Component result = ComponentTranslator.withReplacements(toml, path, replacements);

        assertEquals(expected, result);
    }
}