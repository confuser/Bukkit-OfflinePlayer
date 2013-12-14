package me.confuser.offlineplayer;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.StreamOptions;
import com.google.common.io.Files;

public class OfflinePlayerFile {
	private NbtCompound loaded = null;
	private File file;

	public OfflinePlayerFile(CommandSender sender, String playerName) throws IOException {
		file = new File(OfflinePlayerPlugin.playerWorldFolder, playerName + ".dat");;

		if (!file.exists()) {
			sender.sendMessage(ChatColor.RED + playerName + " does not exist. Are you sure you got the case right?");
			return;
		}

		if (!file.canWrite()) {
			sender.sendMessage(ChatColor.RED + "Unable to write to " + playerName + ".dat please check server file permissions are correct.");
			return;
		}

		loaded = NbtFactory.fromStream(Files.newInputStreamSupplier(file), StreamOptions.GZIP_COMPRESSION);
	}

	public NbtCompound getNbt() {
		return loaded;
	}

	public void save() {
		try {
			loaded.saveTo(Files.newOutputStreamSupplier(file), StreamOptions.GZIP_COMPRESSION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
