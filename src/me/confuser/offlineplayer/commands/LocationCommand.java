package me.confuser.offlineplayer.commands;

import java.io.IOException;

import me.confuser.offlineplayer.OfflinePlayerFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.comphenix.attribute.NbtFactory.NbtList;

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

			@SuppressWarnings("deprecation")
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
				
				// Handle x y z
				NbtList pos = file.getNbt().getList("Pos", false);
				
				pos.set(0, x);
				pos.set(1, y);
				pos.set(2, z);
				
				file.getNbt().putPath("Pos", pos);
				
				// Handle world
				file.getNbt().putPath("WorldUUIDMost", world.getUID().getMostSignificantBits());
			    file.getNbt().putPath("WorldUUIDLeast", world.getUID().getLeastSignificantBits());
			    file.getNbt().put("Dimension", world.getEnvironment().getId());

				file.save();

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
