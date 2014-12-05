package me.confuser.offlineplayer.commands;

import me.confuser.offlineplayer.OfflinePlayerFile;
import net.frostcast.playeridapi.PlayerIdAPI;
import net.frostcast.playeridapi.storage.CachedPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GameModeCommand implements SubCommand {

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

				OfflinePlayerFile player = new OfflinePlayerFile(sender, cachedPlayer.getUuid());
				
				if (player.getNbt() == null)
					return;

				player.setGameMode(gameMode);

				sender.sendMessage(ChatColor.GREEN + cachedPlayer.getCurrentName() + " game mode set to " + gameMode + ".");
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
