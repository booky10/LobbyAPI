package tk.t11e.lobbyapi.manager;
// Created by booky10 in LobbyAPI (18:47 02.03.20)

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.messaging.PluginMessageListener;
import tk.t11e.api.main.Main;
import tk.t11e.api.util.InventoryUtils;
import tk.t11e.lobbyapi.util.Game;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class LobbyManager implements PluginMessageListener {

    private static List<Game> games;
    private static HashMap<String, ItemStack> itemCash = new HashMap<>();

    public static List<Game> getGames() {
        return games;
    }

    public static Boolean exitsId(String id) {
        return exitsId(UUID.fromString(id));
    }

    public static Boolean exitsId(UUID id) {
        boolean exits = false;
        for (Game game : games)
            if (game.getId().equals(id)) {
                exits = true;
                break;
            }
        return exits;
    }

    public static void requestUpdate(SendType type) {
        if (Bukkit.getOnlinePlayers().size() >= 1) {
            Player receiver = Bukkit.getOnlinePlayers().iterator().next();
            sendPluginMessageUpdate(receiver, type);
        }
    }

    public static void registerGame(Game game) {
        if (Bukkit.getOnlinePlayers().size() >= 1) {
            Player receiver = Bukkit.getOnlinePlayers().iterator().next();
            sendPluginMessageRegister(receiver, game);
        }
    }

    private static void sendPluginMessageUpdate(Player receiver, SendType type) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(type.getSubChannel());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        receiver.sendPluginMessage(Main.main, "lobby:api", byteArrayOutputStream.toByteArray());
    }

    private static void sendPluginMessageRegister(Player receiver, Game game) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("RegisterGame");
            dataOutputStream.writeUTF(game.getName());
            dataOutputStream.writeUTF(game.getId().toString());
            dataOutputStream.writeUTF(game.getMaterial().toString().toUpperCase());
            dataOutputStream.writeUTF(game.getServer());
            dataOutputStream.writeUTF(game.getType().toString().toUpperCase());
            dataOutputStream.writeUTF(game.getPluginMessage());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        receiver.sendPluginMessage(Main.main, "lobby:api", byteArrayOutputStream.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] byteArgs) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.main, () -> {
            if (!channel.equals("lobby:api")) return;

            ByteArrayDataInput dataInput = ByteStreams.newDataInput(byteArgs);
            String subChannel = dataInput.readUTF();
            if (SendType.getSubChannels().contains(subChannel)) {
                String name = dataInput.readUTF();
                if (name.equalsIgnoreCase("START"))
                    games = new ArrayList<>();
                else {
                    UUID id = UUID.fromString(dataInput.readUTF());
                    Material material = Material.valueOf(dataInput.readUTF().toUpperCase());
                    String server = dataInput.readUTF();
                    Game.Type type = Game.Type.valueOf(dataInput.readUTF().toUpperCase());
                    String pluginMessage = dataInput.readUTF();

                    Game game = new Game(name, id, material, server, type, pluginMessage);
                    games.add(game);
                }
            }
        });
    }

    public static List<Game> getActiveGames() {
        List<Game> activeGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.ACTIVE))
                activeGames.add(game);
        return activeGames;
    }

    public static List<Game> getPassiveGames() {
        List<Game> passiveGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.PASSIVE))
                passiveGames.add(game);
        return passiveGames;
    }

    public static List<Game> getOtherGames() {
        List<Game> otherGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.OTHER))
                otherGames.add(game);
        return otherGames;
    }

    public static Inventory getMainInventory() {
        Inventory inventory = InventoryUtils.createGrayInventory(9 * 5, "§0Games");
        inventory.setItem(13, getSpawnItem());
        inventory.setItem(19, getActiveItem());
        inventory.setItem(25, getPassiveItem());
        inventory.setItem(31, getOtherItem());
        return inventory;
    }

    public static Inventory getActiveGamesInventory() {
        Inventory inventory = InventoryUtils.createGrayInventory(9 * 5, "§cActive Games");
        for (int i = 0; i < getActiveGames().size(); i++)
            inventory.setItem(i, getActiveGames().get(i).getItem());
        return inventory;
    }

    public static Inventory getPassiveGamesInventory() {
        Inventory inventory = InventoryUtils.createGrayInventory(9 * 5, "§9Passive Games");
        for (int i = 0; i < getPassiveGames().size(); i++)
            inventory.setItem(i, getPassiveGames().get(i).getItem());
        return inventory;
    }

    public static Inventory getOtherGamesInventory() {
        Inventory inventory = InventoryUtils.createGrayInventory(9 * 5, "§fOther Games");
        for (int i = 0; i < getOtherGames().size(); i++)
            inventory.setItem(i, getOtherGames().get(i).getItem());
        return inventory;
    }

    public static HashMap<String, ItemStack> getItemCash() {
        return itemCash;
    }

    public static ItemStack getSpawnItem() {
        return itemCash.get("spawn");
    }

    public static ItemStack getCompassItem() {
        return itemCash.get("compass");
    }

    public static ItemStack getActiveItem() {
        if (itemCash.containsKey("active"))
            return itemCash.get("active");
        else {
            ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName("§cActive Games");
            itemStack.setItemMeta(itemMeta);
            itemCash.put("active", itemStack);
            return itemStack;
        }
    }

    public static ItemStack getPassiveItem() {
        if (itemCash.containsKey("passive"))
            return itemCash.get("passive");
        else {
            ItemStack itemStack = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName("§9Passive Games");
            itemStack.setItemMeta(itemMeta);
            itemCash.put("passive", itemStack);
            return itemStack;
        }
    }

    public static ItemStack getOtherItem() {
        if (itemCash.containsKey("other"))
            return itemCash.get("other");
        else {
            ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            itemMeta.setDisplayName("§fOther Games");
            itemStack.setItemMeta(itemMeta);
            itemCash.put("other", itemStack);
            return itemStack;
        }
    }

    public static Location getSpawn() {
        FileConfiguration config = Main.main.getConfig();
        World world = Bukkit.getWorld(config.getString("spawn.world"));
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        float yaw = Float.parseFloat(config.getString("spawn.yaw"));
        float pitch = Float.parseFloat(config.getString("spawn.pitch"));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void setSpawn(Location location) {
        FileConfiguration config = Main.main.getConfig();
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getBlockX());
        config.set("spawn.y", location.getBlockY());
        config.set("spawn.z", location.getBlockZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        Main.main.saveConfig();
    }

    public enum SendType {
        ACTIVE("GetActiveGames"),
        PASSIVE("GetPassiveGames"),
        OTHER("GetOtherGames"),
        ALL("GetGames"),
        SCHEDULED_GAME_SYNC("ScheduledGameSync");

        private String subChannel;

        SendType(String subChannel) {
            this.subChannel = subChannel;
        }

        public static List<String> getSubChannels() {
            List<String> subChannels = new ArrayList<>();
            for (SendType sendType : values())
                subChannels.add(sendType.getSubChannel());
            return subChannels;
        }

        public String getSubChannel() {
            return subChannel;
        }
    }
}