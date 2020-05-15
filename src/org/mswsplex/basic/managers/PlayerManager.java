package org.mswsplex.basic.managers;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.QRMap;
import org.mswsplex.basic.utils.TOTP;
import org.mswsplex.msws.basic.Main;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerManager {

	public void setInfo(OfflinePlayer player, String id, Object data) {
		if (!isSaveable(data)) {
			int currentLine = Thread.currentThread().getStackTrace()[2].getLineNumber();

			String fromClass = new Exception().getStackTrace()[1].getClassName();
			if (fromClass.contains("."))
				fromClass = fromClass.split("\\.")[fromClass.split("\\.").length - 1];
			MSG.log("WARNING!!! SAVING ODD DATA FROM " + fromClass + ":" + currentLine);
		}
		Main.plugin.data.set(player.getUniqueId() + "." + id, data);
	}

	public void deleteInfo(OfflinePlayer player) {
		Main.plugin.data.set(player.getUniqueId() + "", null);
	}

	public void removeInfo(OfflinePlayer player, String id) {
		Main.plugin.data.set(player.getUniqueId() + "." + id, null);
	}

	public Object getInfo(OfflinePlayer player, String id) {
		return Main.plugin.data.get(player.getUniqueId() + "." + id);
	}

	public String getString(OfflinePlayer player, String id) {
		return Main.plugin.data.getString(player.getUniqueId() + "." + id);
	}

	public Double getDouble(OfflinePlayer player, String id) {
		return Main.plugin.data.getDouble(player.getUniqueId() + "." + id);
	}

	public Boolean getBoolean(OfflinePlayer player, String id) {
		return Main.plugin.data.getBoolean(player.getUniqueId() + "." + id);
	}

	public List<String> getStringList(OfflinePlayer player, String id) {
		return Main.plugin.data.getStringList(player.getUniqueId() + "." + id);
	}

	public boolean isFrozen(OfflinePlayer player) {
		if (getInfo(player, "frozen") == null) {
			setInfo(player, "frozen", false);
			return false;
		}
		return getBoolean(player, "frozen");
	}

	public boolean isGod(OfflinePlayer player) {
		if (getInfo(player, "god") == null) {
			setInfo(player, "god", false);
			return false;
		}
		return getBoolean(player, "god");
	}

	public String getPrefix(Player player) {
		if (Bukkit.getPluginManager().getPlugin("PermissionsEx") == null)
			return "";
		return PermissionsEx.getUser(player).getPrefix();
	}

	public boolean isVanished(OfflinePlayer player) {
		if (getInfo(player, "vanished") == null) {
			setInfo(player, "vanished", false);
			return false;
		}
		return getBoolean(player, "vanished");
	}

	public int getVanishRank(Player player) {
		for (int i = 100; i >= 0; i--) {
			if (player.hasPermission("basic.vanish." + i))
				return i;
		}
		return 0;
	}

	public String parseDecimal(String name) {
		if (name.contains(".")) {
			if (name.split("\\.")[1].length() > 2) {
				name = name.split("\\.")[0] + "."
						+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), 2));
			}
		}
		return name;
	}

	public Inventory getGui(OfflinePlayer player, String id) {
		if (!Main.plugin.gui.contains(id))
			return null;
		ConfigurationSection gui = Main.plugin.gui.getConfigurationSection(id);
		if (!gui.contains("Size") || !gui.contains("Title"))
			return null;
		String title = gui.getString("Title").replace("%player%", player.getName());
		if (player.isOnline())
			title = title.replace("%world%", ((Player) player).getWorld().getName());
		title = title.replace("%world%", "");
		Inventory inv = Bukkit.createInventory(null, gui.getInt("Size"), MSG.color(title));
		ItemStack bg = null;
		boolean empty = true;
		for (String res : gui.getKeys(false)) {
			if (!gui.contains(res + ".Icon"))
				continue;
			empty = false;
			if (player.isOnline()) {
				if (gui.contains(res + ".Permission")
						&& !((Player) player).hasPermission(gui.getString(res + ".Permission"))) {
					continue;
				}
			}
			ItemStack item = parseItem(Main.plugin.gui, id + "." + res, player);
			if (res.equals("BACKGROUND_ITEM")) {
				bg = item;
				continue;
			}
			int slot = 0;
			if (!gui.contains(res + ".Slot")) {
				while (inv.getItem(slot) != null)
					slot++;
				inv.setItem(slot, item);
			} else {
				inv.setItem(gui.getInt(res + ".Slot"), item);
			}
		}
		if (empty)
			return null;
		if (bg != null) {
			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
					inv.setItem(i, bg);
				}
			}
		}
		return inv;
	}

	public ItemStack parseItem(ConfigurationSection section, String path, OfflinePlayer player) {
		ConfigurationSection gui = section.getConfigurationSection(path);
		ItemStack item = new ItemStack(Material.valueOf(gui.getString("Icon")));
		List<String> lore = new ArrayList<String>();
		if (gui.contains("Amount"))
			item.setAmount(gui.getInt("Amount"));
		if (gui.contains("Data"))
			item.setDurability((short) gui.getInt("Data"));
		ItemMeta meta = item.getItemMeta();
		if (gui.contains("Name"))
			meta.setDisplayName(MSG.color("&r" + gui.getString("Name")));
		if (gui.contains("Lore")) {
			for (String temp : gui.getStringList("Lore"))
				lore.add(MSG.color("&r" + temp));
		}
		if (gui.getBoolean("Unbreakable")) {
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		if (gui.contains("Cost")) {
			HashMap<Material, Integer> mats = new HashMap<>();
			ConfigurationSection costs = gui.getConfigurationSection("Cost");
			for (String material : costs.getKeys(false))
				mats.put(Material.valueOf(material), costs.getInt(material));
			lore.add("");
			if (mats.size() == 1) {
				lore.add(MSG.color("&aCost: &c" + mats.values().toArray()[0] + " "
						+ MSG.camelCase(mats.keySet().toArray()[0] + "")));
			} else {
				lore.add(MSG.color("&aCost:"));
				for (Material mat : mats.keySet()) {
					lore.add(MSG.color("&c* " + mats.get(mat) + " "
							+ MSG.camelCase(mat.name() + (mats.get(mat) == 1 ? "" : "s"))));
				}
			}
		}
		if (gui.contains("Enchantments")) {
			ConfigurationSection enchs = gui.getConfigurationSection("Enchantments");
			for (String enchant : enchs.getKeys(false)) {
				int level = 1;
				if (enchs.contains(enchant + ".Level"))
					level = enchs.getInt(enchant + ".Level");
				if (enchs.contains(enchant + ".Visible") && !enchs.getBoolean(enchant + ".Visible"))
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.getByName(enchant.toUpperCase()), level);
				meta = item.getItemMeta();
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Get whether an object is saveable in YAML
	 * 
	 * @param obj Object type to test
	 * @return True if saveable, false otherwise
	 */
	public boolean isSaveable(Object obj) {
		return (obj instanceof String || obj instanceof Integer || obj instanceof ArrayList || obj instanceof Boolean
				|| obj == null || obj instanceof Double || obj instanceof Short || obj instanceof Long
				|| obj instanceof Character);
	}

	@SuppressWarnings("deprecation")
	public void setup2fa(Player player) {
		removeInfo(player, "2fakey");
		setInfo(player, "setup", true);
		try {
			String key = TOTP.generateBase32Secret();
			for (String res : Main.plugin.lang.getStringList("Command.2FA.Setup")) {
				MSG.tell(player, res.replace("%key%", key + ""));
			}
			try {
				URL url = new URL(TOTP.qrImageUrl("MSWSServer@" + player.getName(), key));
				BufferedImage image = ImageIO.read(url);
				ItemStack i = new ItemStack(Material.MAP);
				MapView view = Bukkit.createMap(player.getWorld());
				view.getRenderers().clear();
				view.addRenderer((MapRenderer) new QRMap(image));
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
					view.getRenderers().clear();
				}, (long) 5);

				i.setDurability(view.getId());
				player.setItemInHand(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			setInfo(player, "2fakey", key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setInfo(player, "login", true);
	}

	public void twoSuccess(Player player) {
		MSG.tell(player, MSG.getString("Command.2FA.Success", "success").replace("%prefix%",
				MSG.getString("Command.2FA.Prefix", "2fa")));
		setInfo(player, "lastVerify", System.currentTimeMillis());
		removeInfo(player, "login");
		setInfo(player, "setup", null);
		if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.MAP)
			player.setItemInHand(new ItemStack(Material.AIR));
	}

	public static String getRankColor(int rank) {
		if (rank == 1) {
			return "&a";
		} else if (rank == 2) {
			return "&e";
		} else if (rank == 3) {
			return "&c";
		} else if (rank <= 50) {
			return "&2";
		} else if (rank <= 100) {
			return "&6";
		} else {
			return "&7";
		}
	}
}
