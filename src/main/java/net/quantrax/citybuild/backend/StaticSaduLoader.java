package net.quantrax.citybuild.backend;

import de.chojo.sadu.adapter.StaticQueryAdapter;
import de.chojo.sadu.databases.MariaDb;
import de.chojo.sadu.datasource.DataSourceCreator;

public final class StaticSaduLoader {

    public static void load() {
        StaticQueryAdapter.init(DataSourceCreator.create(MariaDb.get())
                .configure(config -> config.host(System.getProperty("DB_HOST")).port(System.getProperty("DB_PORT")).database(System.getProperty("DB_SCHEMA"))
                        .user(System.getProperty("DB_USER")).password(System.getProperty("DB_PASSWORD")))
                .create()
                .withPoolName("CityBuild")
                .withMaximumPoolSize(3)
                .withMinimumIdle(1)
                .build());
    }
}