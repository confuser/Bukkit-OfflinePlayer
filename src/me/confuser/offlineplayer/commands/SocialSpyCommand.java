package me.confuser.offlineplayer.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class SocialSpyCommand implements SubCommand {
	Essentials e = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		if (args.length != 2)
			return false;

		final String playerName = args[0];

		if (Bukkit.getPlayerExact(playerName) != null) {
			sender.sendMessage(ChatColor.RED + playerName + " is online!");
			return true;
		}

		final int socialSpyMode;

		try {
			socialSpyMode = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "socialSpyMode should be a number.");
			return false;
		}

		if (socialSpyMode < 0 || socialSpyMode > 1) {
			sender.sendMessage(ChatColor.RED + "socialSpyMode should be 0 or 1.");
			return false;
		}

		User user = e.getUser(playerName);
		
		if (user == null) {
			sender.sendMessage(ChatColor.RED + playerName + " does not eixst!");
			return true;
		}
		
		boolean status = socialSpyMode == 1;
		user.setSocialSpyEnabled(status);

		if (status)
			sender.sendMessage(ChatColor.GREEN + playerName + " socialSpy enabled.");
		else
			sender.sendMessage(ChatColor.GREEN + playerName + " socialSpy disabled.");
	
		return true;
	}

	@Override
	public String help(CommandSender p) {
		return "socialspy <playerName> <0|1>";
	}

	@Override
	public String permission() {
		return "socialspy";
	}

}
