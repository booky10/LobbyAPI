package tk.t11e.lobbyapi.listener;
// Created by booky10 in LobbyAPI (21:09 12.03.20)

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tk.t11e.lobbyapi.manager.LobbyManager;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack=player.getInventory().getItemInMainHand();
        if(itemStack.equals(LobbyManager.getCompassItem())){
            event.setCancelled(true);
            player.openInventory(LobbyManager.getMainInventory());
        }
    }
}