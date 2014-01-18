package me.confuser.offlineplayer.commands;

import java.io.IOException;

import me.confuser.offlineplayer.OfflinePlayerFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GameModeCommand implements SubCommand {

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		if (args.length != 2)
			return false;

		final String playerName = args[0];

		if (Bukkit.getPlayerExact(playerName) != null) {
			sender.sendMessage(ChatColor.RED + playerName + " is online!");
			return true;
		}

		final int gameMode;

		try {
			gameMode = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "gameMode should be a number.");
			return false;
		}

		if (gameMode < 0 || gameMode > 2) {
			sender.sendMessage(ChatColor.RED + "gameMode should be 0, 1 or 2.");
			return false;
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

				if (file.getNbt().getInteger("playerGameType", 0) == gameMode) {
					sender.sendMessage(ChatColor.RED + "The game mode of " + playerName + " is already set to " + gameMode + ".");
					return;
				}

				file.getNbt().putPath("playerGameType", gameMode);

				file.save();

				sender.sendMessage(ChatColor.GREEN + playerName + " game mode set to " + gameMode + ".");
			}

		});

		return true;
	}

	@Override
	public String help(CommandSender p) {
		return "gm <playerName> <0|1|2>";
	}

	@Override
	public String permission() {
		return "gamemode";
	}

}
