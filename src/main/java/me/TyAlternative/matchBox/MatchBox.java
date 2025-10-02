package me.TyAlternative.matchBox;

import me.TyAlternative.matchBox.composition.CompoCommandListener;
import me.TyAlternative.matchBox.composition.CompositionGUI;
import org.bukkit.plugin.java.JavaPlugin;

public final class MatchBox extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("compo").setExecutor(new CompoCommandListener());

        getServer().getPluginManager().registerEvents(new CompositionGUI(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static MatchBox getInstance() {
        return getPlugin(MatchBox.class);
    }
}
