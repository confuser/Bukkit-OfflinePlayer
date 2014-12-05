package me.confuser.offlineplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import net.minecraft.util.org.apache.commons.lang3.Validate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.NbtList;
import com.comphenix.attribute.NbtFactory.StreamOptions;
import com.google.common.io.Files;

public class OfflinePlayerFile {
	private UUID uuid;
	private NbtCompound nbt = null;
	private File file;
	private File folder;
	private boolean autoSave = true;

	public OfflinePlayerFile(CommandSender sender, UUID uuid) {
		this.uuid = uuid;
		this.folder = OfflinePlayerPlugin.playerWorldFolder;

		try {
			load();
		} catch (FileNotFoundException e) {
			sender.sendMessage(ChatColor.RED + uuid.toString() + " could not be found");
			return;
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "Unable to write to " + uuid.toString() + ".dat please check server file permissions are correct.");
			return;
		}
	}

	public OfflinePlayerFile(CommandSender sender, UUID uuid, boolean autoSave) {
		this(sender, uuid);
		this.autoSave = autoSave;
	}

	public OfflinePlayerFile(UUID uuid) throws IOException, FileNotFoundException {
		this(uuid, OfflinePlayerPlugin.playerWorldFolder);
	}

	public OfflinePlayerFile(UUID uuid, boolean autoSave) throws IOException, FileNotFoundException {
		this(uuid, OfflinePlayerPlugin.playerWorldFolder);
		this.autoSave = autoSave;
	}

	public OfflinePlayerFile(UUID uuid, File folder) throws IOException, FileNotFoundException {
		this.uuid = uuid;
		this.folder = folder;

		load();
	}

	public OfflinePlayerFile(UUID uuid, File folder, boolean autoSave) throws IOException, FileNotFoundException {
		this(uuid, folder);
		this.autoSave = autoSave;
	}

	private void load() throws IOException, FileNotFoundException {
		file = new File(folder, uuid.toString() + ".dat");

		if (!file.exists())
			throw new FileNotFoundException();

		if (!file.canWrite())
			throw new IOException();

		nbt = NbtFactory.fromStream(Files.newInputStreamSupplier(file), StreamOptions.GZIP_COMPRESSION);
	}

	public NbtCompound getNbt() {
		return nbt;
	}

	public void save() {
		try {
			nbt.saveTo(Files.newOutputStreamSupplier(file), StreamOptions.GZIP_COMPRESSION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Location getLocation() {
		NbtList position = nbt.getList("Pos", false);
		NbtList rotation = nbt.getList("Rotation", false);

		UUID world = new UUID(nbt.getLong("WorldUUIDMost", null), nbt.getLong("WorldUUIDLeast", null));

		double x = (double) position.get(0);
		double y = (double) position.get(1);
		double z = (double) position.get(2);
		float yaw = (float) rotation.get(0);
		float pitch = (float) rotation.get(1);

		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	@SuppressWarnings("deprecation")
	public void setLocation(World world, double x, double y, double z, float yaw, float pitch) {
		NbtList pos = nbt.getList("Pos", false);
		NbtList rotation = nbt.getList("Rotation", false);

		pos.set(0, x);
		pos.set(1, y);
		pos.set(2, z);

		rotation.set(0, yaw);
		rotation.set(1, pitch);

		nbt.putPath("Pos", pos);
		nbt.putPath("Rotation", rotation);

		// Handle world
		nbt.putPath("WorldUUIDMost", world.getUID().getMostSignificantBits());
		nbt.putPath("WorldUUIDLeast", world.getUID().getLeastSignificantBits());
		nbt.put("Dimension", world.getEnvironment().getId());

		if (autoSave)
			save();
	}

	public void setLocation(World world, double x, double y, double z) {
		setLocation(world, x, y, z, 0.0F, 0.0F);
	}

	public void setLocation(Location loc) {
		setLocation(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}

	@SuppressWarnings("deprecation")
	public GameMode getGameMode() {
		return GameMode.getByValue(nbt.getInteger("playerGameType", 0));
	}

	@SuppressWarnings("deprecation")
	public void setGameMode(GameMode mode) {
		setGameMode(mode.getValue());
	}

	public void setGameMode(int mode) {
		nbt.putPath("playerGameType", mode);

		if (autoSave)
			save();
	}

	public boolean isFlying() {
		return nbt.getInteger("abilities.flying", 0) != 0;
	}

	public void setFlying(boolean canFly) {
		setFlying(canFly ? 1 : 0);
	}

	public void setFlying(int can) {
		Validate.inclusiveBetween(0, 1, can);

		nbt.putPath("abilities.mayfly", can);
		nbt.putPath("abilities.flying", can);

		if (autoSave)
			save();
	}

	private Inventory getInventory(Player holder, String path) {
		Class<?> inventoryClass = Util.getCraftClass("PlayerInventory");
		Class<?> entityHumanClass = Util.getCraftClass("EntityHuman");
		Class<?> craftPlayerClass = Util.getCraftBukkitClass("entity.CraftPlayer");
		Object inventory = null;

		try {
			Method getHandle = craftPlayerClass.getDeclaredMethod("getHandle", new Class<?>[]{});
			Object craftPlayer = getHandle.invoke(craftPlayerClass.cast(holder), new Object[]{});

			inventory = inventoryClass.getConstructor(entityHumanClass).newInstance(entityHumanClass.cast(craftPlayer));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		Method inventoryMethod = Util.getMethod(inventoryClass, "b", new Class<?>[] { Util.getCraftClass("NBTTagList") });
		try {
			inventoryMethod.invoke(inventory, nbt.getList(path, false).getHandle());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		Class<?> inventoryPlayerClass = Util.getCraftBukkitClass("inventory.CraftInventoryPlayer");
		Inventory playerInv = null;
		try {
			playerInv = (Inventory) inventoryPlayerClass.getConstructor(inventoryClass).newInstance(inventory);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return playerInv;
	}

	public Inventory getInventory(Player holder) {
		return getInventory(holder, "Inventory");
	}

	public void setInventory(Inventory inventory) {
		setInventory("Inventory", inventory);
	}

	private void setInventory(String path, Inventory inventory) {
		Class<?> inventoryPlayerClass = Util.getCraftBukkitClass("inventory.CraftInventoryPlayer");

		Object craftInventory = inventoryPlayerClass.cast(inventory);
		Method getInventoryMethod = Util.getMethod(inventoryPlayerClass, "getInventory");

		Object getInventory = null;

		try {
			getInventory = getInventoryMethod.invoke(craftInventory);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Class<?> inventoryClass = Util.getCraftClass("PlayerInventory");
		NbtList nbtInv = nbt.getList(path, false);
		Method toNbtMethod = Util.getMethod(inventoryClass, "a", new Class<?>[] { nbtInv.getHandle().getClass() });
		nbtInv.clear();
		try {
			toNbtMethod.invoke(getInventory, nbtInv.getHandle());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		nbt.putPath(path, nbtInv);

		if (autoSave)
			save();
	}

	public Inventory getEnderChest(Player holder) {
		return getInventory(holder, "EnderItems");
	}

	public void setEnderChest(Inventory inventory) {
		setInventory("EnderItems", inventory);
	}

	public long getLastSeen() {
		return nbt.getLong("bukkit.lastPlayed", (long) file.lastModified());
	}
}
