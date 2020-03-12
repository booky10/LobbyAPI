package tk.t11e.lobbyapi.listener;
// Created by booky10 in LobbyAPI (21:01 12.03.20)

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.t11e.lobbyapi.manager.LobbyManager;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player=event.getPlayer();
        player.getInventory().setHeldItemSlot(4);
        player.getInventory().setItem(4, LobbyManager.getCompassItem());
    }
}