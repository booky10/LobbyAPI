package tk.t11e.lobbyapi.commands;
// Created by booky10 in LobbyAPI (20:33 12.03.20)

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tk.t11e.api.commands.CommandExecutor;
import tk.t11e.api.main.Main;
import tk.t11e.lobbyapi.manager.LobbyManager;

import java.util.List;

public class LobbyAPI extends CommandExecutor {


    public LobbyAPI() {
        super(Main.main, "", "/lobby ", "lobbyapi.use", Receiver.PLAYER,
                "lobby", "lobbyapi", "spawn");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        help(sender);
    }

    @Override
    public void onPlayerExecute(Player player, String[] args) {
        if(args.length==1){
            if(args[0].equalsIgnoreCase("setSpawn")){
                LobbyManager.setSpawn(player.getLocation());
                player.sendMessage(Main.PREFIX+"Successfully set spawn!");
            }else
                help(player);
        }else
            help(player);
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args, List<String> completions) {
        if(sender instanceof Player)
            completions.add("setSpawn");
        return completions;
    }
}