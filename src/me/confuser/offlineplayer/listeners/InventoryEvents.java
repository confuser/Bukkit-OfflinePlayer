package me.confuser.offlineplayer.listeners;

import java.util.HashMap;

import me.confuser.offlineplayer.OfflinePlayerFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class InventoryEvents implements Listener {
	public static HashMap<String, String> openedInvs = new HashMap<String, String>();
	public static HashMap<String, String> openedEnderInvs = new HashMap<String, String>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void serverTransfer(ServerCommandEvent event) {

		if (event.getCommand().equalsIgnoreCase("stop")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.closeInventory();
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		String playerName = event.getPlayer().getName();
		boolean isPlayerInv = false;
		boolean isEnderInv = false;

		if (openedInvs.get(playerName) != null)
			isPlayerInv = true;
		else if (openedEnderInvs.get(playerName) != null)
			isEnderInv = true;

		if (!isPlayerInv && !isEnderInv)
			return;

		String playerInvName = null;

		if (isPlayerInv)
			playerInvName = openedInvs.remove(playerName);
		else if (isEnderInv)
			playerInvName = openedEnderInvs.remove(playerName);

		OfflinePlayerFile player = new OfflinePlayerFile((CommandSender) event.getPlayer(), playerInvName);

		if (isPlayerInv)
			player.setInventory(event.getInventory());
		else if (isEnderInv)
			player.setEnderChest(event.getInventory());
	}
}
