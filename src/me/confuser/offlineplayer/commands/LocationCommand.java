package me.confuser.offlineplayer.commands;

import me.confuser.offlineplayer.OfflinePlayerFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class LocationCommand implements SubCommand {

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		if (args.length != 5)
			return false;

		final String playerName = args[0];

		if (Bukkit.getPlayerExact(playerName) != null) {
			sender.sendMessage(ChatColor.RED + playerName + " is online!");
			return true;
		}

		final double x, y, z;
		final String worldName = args[4];

		try {
			x = Double.parseDouble(args[1]);
			y = Double.parseDouble(args[2]);
			z = Double.parseDouble(args[3]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "x, y & z coordinates should be a number or decimal.");
			return false;
		}

		final World world = plugin.getServer().getWorld(worldName);

		if (world == null) {
			sender.sendMessage(ChatColor.RED + "world " + world + " does not exist.");
			return false;
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {

				OfflinePlayerFile player = new OfflinePlayerFile(sender, playerName);

				if (player.getNbt() == null)
					return;

				player.setLocation(world, x, y, z);

				sender.sendMessage(ChatColor.GREEN + playerName + " location set to " + x + ", " + y + ", " + z + " in " + worldName + ".");
			}

		});

		return true;
	}

	@Override
	public String help(CommandSender p) {
		return "location <playerName> <x> <y> <z> <world>";
	}

	@Override
	public String permission() {
		return "location";
	}

}
