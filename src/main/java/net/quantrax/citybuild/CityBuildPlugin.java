package net.quantrax.citybuild;

import net.quantrax.citybuild.backend.Environment;
import net.quantrax.citybuild.backend.StaticSaduLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class CityBuildPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        Environment.prepare();
        StaticSaduLoader.load();
    }
}
