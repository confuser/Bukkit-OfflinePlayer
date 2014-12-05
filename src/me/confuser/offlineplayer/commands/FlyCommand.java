package me.confuser.offlineplayer.commands;

import me.confuser.offlineplayer.OfflinePlayerFile;
import net.frostcast.playeridapi.PlayerIdAPI;
import net.frostcast.playeridapi.storage.CachedPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FlyCommand implements SubCommand {

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		if (args.length != 2)
			return false;

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

		final int flyMode;

		try {
			flyMode = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "flyMode should be a number.");
			return false;
		}

		if (flyMode < 0 || flyMode > 1) {
			sender.sendMessage(ChatColor.RED + "flyMode should be 0 or 1.");
			return false;
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {

				OfflinePlayerFile player = new OfflinePlayerFile(sender, cachedPlayer.getUuid());

				if (player.getNbt() == null)
					return;

				player.setFlying(flyMode);

				String status = "enabled";

				if (flyMode == 0)
					status = "disabled";

				sender.sendMessage(ChatColor.GREEN + cachedPlayer.getCurrentName() + " flight " + status + ".");
			}

		});

		return true;
	}

	@Override
	public String help(CommandSender p) {
		return "fly <playerName> <0|1>";
	}

	@Override
	public String permission() {
		return "fly";
	}

}
