package me.confuser.offlineplayer;

import java.io.File;

import me.confuser.offlineplayer.listeners.InventoryEvents;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OfflinePlayerPlugin extends JavaPlugin {
	private static OfflinePlayerPlugin statPlugin;
	public static File playerWorldFolder;

	public void onEnable() {
		statPlugin = this;
		playerWorldFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
		
		getCommand("offline").setExecutor(new CommandHandler("offline"));
		
		getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
	}

	public static OfflinePlayerPlugin getPlugin() {
		return statPlugin;
	}

	public String getPluginFriendlyName() {
		return "OfflinePlayer";
	}

	public String getPermissionBase() {
		return "offlineplayer";
	}
}
