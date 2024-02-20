package net.quantrax.citybuild.backend.dao.impl.entity;

import de.chojo.sadu.wrapper.util.Row;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageEntity {

    private String key;
    private String value;

    @Contract("_ -> new")
    public static @NotNull MessageEntity fromRow(@NotNull Row row) throws SQLException {
        return new MessageEntity(row.getString("i18n"), row.getString("de"));
    }

    @Contract("_ -> new")
    public static @NotNull MessageEntity create(@NotNull String key, @NotNull String value) {
        return new MessageEntity(key, value);
    }
}