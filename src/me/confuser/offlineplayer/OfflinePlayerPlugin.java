package me.confuser.offlineplayer;

import java.io.File;
import java.io.IOException;

import me.confuser.offlineplayer.listeners.InventoryEvents;
import net.gravitydevelopment.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class OfflinePlayerPlugin extends JavaPlugin {
	private static OfflinePlayerPlugin statPlugin;
	public static File playerWorldFolder;

	public void onEnable() {
		statPlugin = this;
		playerWorldFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");

		getConfig().options().copyDefaults(true);
		saveConfig();

		if (getConfig().getBoolean("autoUpdate"))
			new Updater(this, 70666, getFile(), Updater.UpdateType.DEFAULT, false);

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

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
