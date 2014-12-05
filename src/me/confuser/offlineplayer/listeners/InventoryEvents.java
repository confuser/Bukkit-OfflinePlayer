package me.confuser.offlineplayer.listeners;

import java.util.HashMap;
import java.util.UUID;

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
	public static HashMap<UUID, UUID> openedInvs = new HashMap<>();
	public static HashMap<UUID, UUID> openedEnderInvs = new HashMap<>();

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
		UUID uuid = event.getPlayer().getUniqueId();
		boolean isPlayerInv = false;
		boolean isEnderInv = false;

		if (openedInvs.get(uuid) != null)
			isPlayerInv = true;
		else if (openedEnderInvs.get(uuid) != null)
			isEnderInv = true;

		if (!isPlayerInv && !isEnderInv)
			return;

		UUID playerInvUUID = null;

		if (isPlayerInv)
			playerInvUUID = openedInvs.remove(uuid);
		else if (isEnderInv)
			playerInvUUID = openedEnderInvs.remove(uuid);

		OfflinePlayerFile player = new OfflinePlayerFile((CommandSender) event.getPlayer(), playerInvUUID);

		if (isPlayerInv)
			player.setInventory(event.getInventory());
		else if (isEnderInv)
			player.setEnderChest(event.getInventory());
	}
}
