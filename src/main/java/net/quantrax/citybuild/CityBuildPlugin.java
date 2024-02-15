package net.quantrax.citybuild;

import com.moandjiezana.toml.Toml;
import net.quantrax.citybuild.backend.Environment;
import net.quantrax.citybuild.backend.StaticSaduLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class CityBuildPlugin extends JavaPlugin {

    private Toml toml;

    @Override
    public void onLoad() {
        Environment.prepareSystem();
        Environment.copyToml(this);

        StaticSaduLoader.load();

        toml = Environment.readConfig(this);
    }
}
