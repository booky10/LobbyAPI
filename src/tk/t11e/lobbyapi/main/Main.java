package tk.t11e.lobbyapi.main;
// Created by booky10 in LobbyAPI (18:31 02.03.20)

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tk.t11e.lobbyapi.commands.LobbyAPI;
import tk.t11e.lobbyapi.listener.InteractListener;
import tk.t11e.lobbyapi.listener.JoinLeaveListener;
import tk.t11e.lobbyapi.manager.LobbyManager;

public class Main extends JavaPlugin {

    public static final String PREFIX = "§7[§bT11E§7]§c ", NO_PERMISSION = PREFIX + "You don't have " +
            "the permissions for this!";

    @Override
    public void onEnable() {
        long milliseconds = System.currentTimeMillis();

        config();
        initCommands();
        System.out.println(getConfig().getString("items.spawn").toUpperCase());
        initListener(Bukkit.getPluginManager());
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "lobby:api");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "lobby:api", new LobbyManager());

        milliseconds = System.currentTimeMillis() - milliseconds;
        getLogger().info("[LobbyAPI] It took " + milliseconds + "ms to initialize this plugin!");
    }

    private void initListener(PluginManager pluginManager) {
        pluginManager.registerEvents(new JoinLeaveListener(), this);
        pluginManager.registerEvents(new InteractListener(), this);
    }

    private void initCommands() {
        new LobbyAPI();
    }

    private void config() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        if (!config.contains("items.spawn")) config.set("items.spawn", "MAGMA_CREAM");
        if (!config.contains("items.compass")) config.set("items.spawn", "COMPASS");
        if (!config.contains("spawn.x")) config.set("spawn.x", "0");
        if (!config.contains("spawn.y")) config.set("spawn.y", "100");
        if (!config.contains("spawn.z")) config.set("spawn.z", "0");
        saveConfig();
    }
}