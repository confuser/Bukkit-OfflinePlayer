package me.confuser.offlineplayer.commands;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.confuser.offlineplayer.OfflinePlayerFile;
import me.confuser.offlineplayer.Util;
import me.confuser.offlineplayer.listeners.InventoryEvents;
import net.minecraft.server.v1_7_R1.EntityHuman;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryCommand implements SubCommand {

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Not usable via console.");
			return true;
		}

		final String playerName = args[0];

		if (Bukkit.getPlayerExact(playerName) != null) {
			sender.sendMessage(ChatColor.RED + playerName + " is online!");
			return true;
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {

				OfflinePlayerFile file = null;
				try {
					file = new OfflinePlayerFile(sender, playerName);
				} catch (IOException e) {
					sender.sendMessage(ChatColor.RED + "An error occured, please check the console.");
					e.printStackTrace();
					return;
				}
				
				if (file.getNbt() == null)
					return;

				Class<?> inventoryClass = Util.getCraftClass("PlayerInventory");
				Object inventory = null;
				final Player player = (Player) sender;

				try {
					inventory = inventoryClass.getConstructor(EntityHuman.class).newInstance((EntityHuman) null);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
					e1.printStackTrace();
				}

				Method inventoryMethod = Util.getMethod(inventoryClass, "b", new Class<?>[] { Util.getCraftClass("NBTTagList") });
				try {
					inventoryMethod.invoke(inventory, file.getNbt().getList("Inventory", false).getHandle());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}

				Class<?> inventoryPlayerClass = Util.getCraftBukkitClass("inventory.CraftInventoryPlayer");
				Inventory playerInv = null;
				try {
					playerInv = (Inventory) inventoryPlayerClass.getConstructor(inventoryClass).newInstance(inventory);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}

				if (playerInv == null)
					return;

				final Inventory playerInventory = playerInv;

				plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

					@Override
					public void run() {
						player.openInventory(playerInventory);
						
						InventoryEvents.openedInvs.put(player.getName(), playerName);
					}

				});
			}

		});

		return true;
	}

	@Override
	public String help(CommandSender p) {
		return "inv <playerName>";
	}

	@Override
	public String permission() {
		return "inventory";
	}

}
