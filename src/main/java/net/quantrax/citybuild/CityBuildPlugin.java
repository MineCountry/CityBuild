package net.quantrax.citybuild;

import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.Environment;
import net.quantrax.citybuild.backend.StaticSaduLoader;
import net.quantrax.citybuild.backend.dao.impl.PlayerRepository;
import net.quantrax.citybuild.backend.tracking.PlayerTrackingListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CityBuildPlugin extends JavaPlugin {

    private Toml toml;

    private PlayerRepository playerRepository;

    @Override
    public void onLoad() {
        Environment.prepareSystem();
        Environment.copyToml(this);
        StaticSaduLoader.load();

        toml = Environment.readConfig(this);
    }

    @Override
    public void onEnable() {
        playerRepository = new PlayerRepository();

        registerListener();
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerTrackingListener(playerRepository), this);
    }
}
