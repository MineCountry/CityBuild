package net.quantrax.citybuild.backend.dao;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Repository<T> {

    void save(@NotNull T t);

    CompletableFuture<Collection<T>> findAll();

}
