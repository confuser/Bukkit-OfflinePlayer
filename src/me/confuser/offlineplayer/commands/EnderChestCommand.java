package me.confuser.offlineplayer.commands;

import me.confuser.offlineplayer.OfflinePlayerFile;
import me.confuser.offlineplayer.listeners.InventoryEvents;
import net.frostcast.playeridapi.PlayerIdAPI;
import net.frostcast.playeridapi.storage.CachedPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnderChestCommand implements SubCommand {

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Not usable via console.");
			return true;
		}

		final String playerName = args[0];
		final CachedPlayer cachedPlayer = PlayerIdAPI.getByCurrentName(playerName);
		
		if (cachedPlayer == null) {
			sender.sendMessage(ChatColor.RED + playerName + " not found.");
			return true;
		}

		if (Bukkit.getPlayer(cachedPlayer.getUuid()) != null) {
			sender.sendMessage(ChatColor.RED + playerName + " is online!");
			return true;
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {

				OfflinePlayerFile player = new OfflinePlayerFile(sender, cachedPlayer.getUuid());

				if (player.getNbt() == null)
					return;

				final Inventory playerInventory = player.getEnderChest((Player) sender);

				plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

					@Override
					public void run() {
						((Player) sender).openInventory(playerInventory);

						InventoryEvents.openedEnderInvs.put(((Player) sender).getUniqueId(), cachedPlayer.getUuid());
					}

				});
			}

		});

		return true;
	}

	@Override
	public String help(CommandSender p) {
		return "echest <playerName>";
	}

	@Override
	public String permission() {
		return "echest";
	}

}
