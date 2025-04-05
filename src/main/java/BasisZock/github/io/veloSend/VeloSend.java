package BasisZock.github.io.veloSend;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

public final class VeloSend extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        int pluginId = 25370; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "velocity:player_info");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "velocity:connect");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getCommand("vsend").setExecutor( new SendCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
