package net.quantrax.citybuild;

import co.aikar.commands.PaperCommandManager;
import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.Environment;
import net.quantrax.citybuild.backend.StaticSaduLoader;
import net.quantrax.citybuild.backend.cache.LocationCache;
import net.quantrax.citybuild.backend.cache.MessageCache;
import net.quantrax.citybuild.backend.cache.PlayerCache;
import net.quantrax.citybuild.backend.dao.impl.repository.LocationRepository;
import net.quantrax.citybuild.backend.dao.impl.repository.MessageRepository;
import net.quantrax.citybuild.backend.dao.impl.repository.PlayerRepository;
import net.quantrax.citybuild.backend.tracking.PlayerTrackingListener;
import net.quantrax.citybuild.commands.*;
import net.quantrax.citybuild.listener.ChairListener;
import net.quantrax.citybuild.listener.CustomInventoryListener;
import net.quantrax.citybuild.listener.TPSProtectionListener;
import net.quantrax.citybuild.utils.ClearLag;
import net.quantrax.citybuild.utils.TPSProtector;
import net.quantrax.citybuild.utils.WorldLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class CityBuildPlugin extends JavaPlugin {

    private Toml toml;
    private PlayerRepository playerRepository;
    private MessageRepository messageRepository;
    private PlayerCache playerCache;
    private LocationCache locationCache;
    private MessageCache messageCache;

    private TPSProtector tpsProtector;

    @Override
    public void onLoad() {
        Environment.prepareSystem();
        StaticSaduLoader.load();

        messageRepository = MessageRepository.getInstance();
        Environment.clearUpMessages(messageRepository);

        toml = Environment.createOrReadConfig(this);
    }

    @Override
    public void onEnable() {
        WorldLoader.loadWorlds(toml.getString("farmworld-name"));

        playerRepository = new PlayerRepository();
        playerCache = PlayerCache.getInstance(playerRepository);
        messageCache = MessageCache.getInstance(messageRepository);
        locationCache = LocationCache.getInstance(new LocationRepository());

        tpsProtector = new TPSProtector(this, messageCache);
        tpsProtector.supervice();

        new ClearLag(this, messageCache, toml).execute();

        registerListener();
        registerCommands();
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerTrackingListener(playerCache, playerRepository), this);
        pluginManager.registerEvents(new CustomInventoryListener(), this);
        pluginManager.registerEvents(new TPSProtectionListener(tpsProtector), this);
        pluginManager.registerEvents(new ChairListener(this), this);
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.getLocales().setDefaultLocale(Locale.GERMAN);

        commandManager.registerDependency(Toml.class, toml);
        commandManager.registerDependency(PlayerCache.class, playerCache);
        commandManager.registerDependency(LocationCache.class, locationCache);
        commandManager.registerDependency(MessageCache.class, messageCache);

        commandManager.registerCommand(new MoneyCommand());
        commandManager.registerCommand(new PayCommand());
        commandManager.registerCommand(new FarmworldCommand());
        commandManager.registerCommand(new NetherCommand());
        commandManager.registerCommand(new FeedCommand());
        commandManager.registerCommand(new HealCommand());
        commandManager.registerCommand(new DayCommand());
        commandManager.registerCommand(new FlyCommand());
        commandManager.registerCommand(new InvseeCommand());
        commandManager.registerCommand(new SignCommand());
    }
}