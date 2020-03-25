package tk.t11e.lobbyapi.listener;
// Created by booky10 in LobbyAPI (21:09 12.03.20)

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tk.t11e.lobbyapi.manager.LobbyManager;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.equals(LobbyManager.getCompassItem())) {
            event.setCancelled(true);
            player.openInventory(LobbyManager.getMainInventory());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("§0Games")) {
            event.setCancelled(true);
            if (event.getCurrentItem().hasItemMeta())
                switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§6Spawn":
                        player.closeInventory();
                        player.teleport(LobbyManager.getSpawn());
                        break;
                    case "§cActive Games":
                        player.closeInventory();
                        player.openInventory(LobbyManager.getActiveGamesInventory());
                        break;
                    case "§9Passive Games":
                        player.closeInventory();
                        player.openInventory(LobbyManager.getPassiveGamesInventory());
                        break;
                    case "§fOther Games":
                        player.closeInventory();
                        player.openInventory(LobbyManager.getOtherGamesInventory());
                        break;
                    default:
                        break;
                }
        }
    }
}