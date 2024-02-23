package net.quantrax.citybuild.backend.dao.impl.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.quantrax.citybuild.backend.dao.Repository;
import net.quantrax.citybuild.backend.dao.impl.entity.MessageEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.adapter.StaticQueryAdapter.builder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRepository implements Repository<MessageEntity> {

    private static final String PLUGIN_NAME = "CityBuild";
    private static MessageRepository instance;

    public static MessageRepository getInstance() {
        if (instance == null) instance = new MessageRepository();
        return instance;
    }

    @Override
    public void save(@NotNull MessageEntity messageEntity) {
        throw new UnsupportedOperationException();
    }

    public void create(@NotNull MessageEntity messageEntity) {
        builder()
                .query("INSERT INTO utility.message (i18n, plugin, de) VALUES (?, ?, ?);")
                .parameter(stmt -> stmt.setString(messageEntity.key()).setString(PLUGIN_NAME).setString(messageEntity.value()))
                .insert().send();
    }

    public void delete(@NotNull MessageEntity messageEntity) {
        builder()
                .query("DELETE FROM utility.message WHERE i18n=?;")
                .parameter(stmt -> stmt.setString(messageEntity.key()))
                .delete().send();
    }

    @Override
    public CompletableFuture<List<MessageEntity>> findAll() {
        return builder(MessageEntity.class)
                .query("SELECT * FROM utility.message WHERE plugin=?;")
                .parameter(stmt -> stmt.setString(PLUGIN_NAME))
                .readRow(MessageEntity::fromRow)
                .all();
    }
}