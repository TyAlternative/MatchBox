package me.TyAlternative.matchBox;

import me.TyAlternative.matchBox.composition.CompoCommandListener;
import me.TyAlternative.matchBox.composition.CompositionGUI;
import me.TyAlternative.matchBox.players.listeners.PlayersListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MatchBox extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("compo").setExecutor(new CompoCommandListener());
        getCommand("matchBox").setExecutor(new MatchBoxCommand());

        getServer().getPluginManager().registerEvents(new CompositionGUI(), this);
        getServer().getPluginManager().registerEvents(new PlayersListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static MatchBox getInstance() {
        return getPlugin(MatchBox.class);
    }
}
