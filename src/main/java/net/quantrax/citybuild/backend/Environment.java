package net.quantrax.citybuild.backend;

import io.github.cdimascio.dotenv.Dotenv;

public final class Environment {

    public static void prepare() {
        Dotenv env = Dotenv.configure().filename("credentials.env").load();
        env.entries().forEach(dotenvEntry -> System.setProperty(dotenvEntry.getKey(), dotenvEntry.getValue()));
    }
}
