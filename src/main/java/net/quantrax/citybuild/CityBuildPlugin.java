package net.quantrax.citybuild;

import co.aikar.commands.PaperCommandManager;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.Environment;
import net.quantrax.citybuild.backend.StaticSaduLoader;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.backend.dao.impl.PlayerRepository;
import net.quantrax.citybuild.backend.tracking.PlayerTrackingListener;
import net.quantrax.citybuild.commands.MoneyCommand;
import net.quantrax.citybuild.commands.PayCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class CityBuildPlugin extends JavaPlugin {

    private Toml toml;
    private PlayerRepository playerRepository;
    private PlayerCache playerCache;

    @Override
    public void onLoad() {
        Environment.prepareSystem();
        Environment.copyConfig(this);
        StaticSaduLoader.load();

        toml = Environment.readConfig(this);
    }

    @Override
    public void onEnable() {
        playerRepository = new PlayerRepository();
        playerCache = PlayerCache.getInstance(playerRepository);

        registerListener();
        registerCommands();
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerTrackingListener(playerCache, playerRepository), this);
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.getLocales().setDefaultLocale(Locale.GERMAN);

        commandManager.registerDependency(Toml.class, toml);
        commandManager.registerDependency(PlayerCache.class, playerCache);
        commandManager.registerDependency(PlayerRepository.class, playerRepository);

        commandManager.registerCommand(new MoneyCommand());
        commandManager.registerCommand(new PayCommand());
    }
}