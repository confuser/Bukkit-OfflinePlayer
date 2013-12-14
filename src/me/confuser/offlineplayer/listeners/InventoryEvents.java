package me.confuser.offlineplayer.listeners;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import me.confuser.offlineplayer.OfflinePlayerFile;
import me.confuser.offlineplayer.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.comphenix.attribute.NbtFactory.NbtList;

public class InventoryEvents implements Listener {
	public static HashMap<String, String> openedInvs = new HashMap<String, String>();

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

		if (openedInvs.get(playerName) == null)
			return;
		OfflinePlayerFile file = null;
		try {
			file = new OfflinePlayerFile((CommandSender) event.getPlayer(), openedInvs.remove(playerName));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Class<?> inventoryPlayerClass = Util.getCraftBukkitClass("inventory.CraftInventoryPlayer");

		Object craftInventory = inventoryPlayerClass.cast(event.getInventory());
		Method getInventoryMethod = Util.getMethod(inventoryPlayerClass, "getInventory");

		Object getInventory = null;

		try {
			getInventory = getInventoryMethod.invoke(craftInventory);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Class<?> inventoryClass = Util.getCraftClass("PlayerInventory");
		NbtList nbtInv = file.getNbt().getList("Inventory", false);
		Method toNbtMethod = Util.getMethod(inventoryClass, "a", new Class<?>[] { nbtInv.getHandle().getClass() });
		nbtInv.clear();
		try {
			toNbtMethod.invoke(getInventory, nbtInv.getHandle());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		file.getNbt().putPath("Inventory", nbtInv);
		file.save();
	}

}
