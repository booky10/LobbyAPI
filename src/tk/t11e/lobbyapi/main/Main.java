package tk.t11e.lobbyapi.main;
// Created by booky10 in LobbyAPI (18:31 02.03.20)

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tk.t11e.lobbyapi.commands.LobbyAPI;
import tk.t11e.lobbyapi.listener.InteractListener;
import tk.t11e.lobbyapi.listener.JoinLeaveListener;
import tk.t11e.lobbyapi.manager.LobbyManager;
import tk.t11e.lobbyapi.util.Game;

import java.util.UUID;

public class Main extends JavaPlugin {

    public static final String PREFIX = "§7[§bT11E§7]§c ", NO_PERMISSION = PREFIX + "You don't have " +
            "the permissions for this!";

    @Override
    public void onEnable() {
        long milliseconds = System.currentTimeMillis();

        config();
        initCommands();
        initListener(Bukkit.getPluginManager());
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "lobby:api");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "lobby:api", new LobbyManager());
        initCash();

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            Game game = new Game("KnockIt", UUID.randomUUID(), Material.DEBUG_STICK, "knocktit",
                    Game.Type.PASSIVE, "KnockIt");
            LobbyManager.registerGame(game);
            LobbyManager.requestUpdate(LobbyManager.SendType.PASSIVE);
        }, 20 * 5);

        milliseconds = System.currentTimeMillis() - milliseconds;
        getLogger().info("[LobbyAPI] It took " + milliseconds + "ms to initialize this plugin!");
    }

    private void initCash() {
        //Spawn
        Bukkit.getScheduler().runTask(this, () -> {
            String item = getConfig().getString("items.spawn");
            Material spawnMaterial = Material.valueOf(item);
            ItemStack itemStack = new ItemStack(spawnMaterial);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName("§6Spawn");
            itemStack.setItemMeta(itemMeta);
            LobbyManager.getItemCash().put("spawn", itemStack);
        });
        //Compass
        Bukkit.getScheduler().runTask(this, () -> {
            FileConfiguration config = tk.t11e.api.main.Main.main.getConfig();
            String item = config.getString("items.compass");
            Material spawnMaterial = Material.valueOf(item);
            ItemStack itemStack = new ItemStack(spawnMaterial);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName("§6Game Compass");
            itemStack.setItemMeta(itemMeta);
            LobbyManager.getItemCash().put("compass", itemStack);
        });
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
        if (!config.contains("items.spawn")) config.set("items.spawn", Material.MAGMA_CREAM.toString());
        if (!config.contains("items.compass")) config.set("items.spawn", Material.COMPASS.toString());
        if (!config.contains("spawn.x")) config.set("spawn.x", "0");
        if (!config.contains("spawn.y")) config.set("spawn.y", "100");
        if (!config.contains("spawn.z")) config.set("spawn.z", "0");
        saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        reloadConfig();
        return super.getConfig();
    }
}