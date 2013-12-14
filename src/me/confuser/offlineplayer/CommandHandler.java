package me.confuser.offlineplayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import me.confuser.offlineplayer.commands.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
	private HashMap<String, SubCommand> commands;
	private String commandName;
	private OfflinePlayerPlugin plugin = OfflinePlayerPlugin.getPlugin();

	public CommandHandler(String name) {
		commands = new HashMap<String, SubCommand>();
		commandName = name;

		commands.put("fly", new FlyCommand());
		commands.put("gm", new GameModeCommand());
		commands.put("inv", new InventoryCommand());
		commands.put("location", new LocationCommand());

		if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null)
			commands.put("socialspy", new SocialSpyCommand());

	}

	public void registerSubCommand(String commandName, SubCommand command) {
		commands.put(commandName, command);
	}

	public HashMap<String, SubCommand> getCommands() {
		return commands;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd1, String commandLabel, String[] args) {
		String cmd = cmd1.getName();

		if (cmd.equalsIgnoreCase(commandName)) {
			if (args == null || args.length < 1) {
				sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getPluginFriendlyName());
				sender.sendMessage(ChatColor.GOLD + "Type /" + commandName + " help for help");

				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				help(sender);
				return true;
			}
			String sub = args[0];

			Vector<String> l = new Vector<String>();
			l.addAll(Arrays.asList(args));
			l.remove(0);
			args = (String[]) l.toArray(new String[0]);

			if (!commands.containsKey(sub)) {
				sender.sendMessage(ChatColor.RED + "Command dosent exist.");
				sender.sendMessage(ChatColor.DARK_AQUA + "Type /" + commandName + " help for help");
				return true;
			}
			try {
				if (!sender.hasPermission(plugin.getPermissionBase() + "." + commands.get(sub).permission()))
					sender.sendMessage(ChatColor.RED + "You do not have permission for this command");
				else {
					SubCommand command = commands.get(sub);
					if (!command.onCommand(sender, args) && command.help(sender) != null)
						sender.sendMessage(ChatColor.RED + "/" + commandName + " " + command.help(sender));
				}
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "An error occured while executing the command. Check the console");
				sender.sendMessage(ChatColor.BLUE + "Type /" + commandName + " help for help");
			}
			return true;
		}
		return false;
	}

	public void help(CommandSender p) {
		p.sendMessage(ChatColor.GOLD + "/" + commandName + " <command> <args>");
		p.sendMessage(ChatColor.GOLD + "Commands are as follows:");
		for (SubCommand v : commands.values()) {
			if (p.hasPermission(v.permission()) && v.help(p) != null)
				p.sendMessage(ChatColor.AQUA + v.help(p));
		}
	}
}
