package me.confuser.offlineplayer.commands;

import java.io.IOException;

import me.confuser.offlineplayer.OfflinePlayerPlugin;

import org.bukkit.command.CommandSender;

public interface SubCommand {

	public OfflinePlayerPlugin plugin = OfflinePlayerPlugin.getPlugin();

	public boolean onCommand(CommandSender sender, String[] args) throws IOException;

	public String help(CommandSender p);

	public String permission();

}
