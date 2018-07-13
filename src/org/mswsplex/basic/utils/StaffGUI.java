package org.mswsplex.basic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.msws.basic.Main;

public class StaffGUI {
	PlayerManager pManager = new PlayerManager();

	public static Inventory getStaffInventory(int page) {
		Inventory inv = Bukkit.createInventory(null, 54, "Staff");

		List<String> online = new ArrayList<String>(), offline = new ArrayList<String>();
		List<String> staff = Main.plugin.data.getStringList("Staff");
		for (String res : staff) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(res));
			if (Main.plugin.data.getStringList("Staff").contains(target.getUniqueId() + "")) {
				if (target.isOnline()) {
					online.add(target.getUniqueId() + "");
				} else {
					offline.add(target.getUniqueId() + "");
				}
			}
		}

		int slot = 0;

		for (String uuid : online) {
			if (slot < page * inv.getSize() || slot > page * inv.getSize() + (inv.getSize() - 9)) {
				slot++;
				continue;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
			ItemStack skull = new ItemStack(Material.SKULL_ITEM);
			skull.setDurability((short) 3);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(target.getName());
			meta.setDisplayName(MSG.color("&a&l" + target.getName()));
			List<String> lore = new ArrayList<String>();
			lore.add(MSG.color("&rStatus: &aOnline"));
			meta.setLore(lore);
			skull.setItemMeta(meta);
			inv.setItem(slot, skull);
			slot++;
		}

		for (int i = inv.getSize() * page; i < page * inv.getSize() + (inv.getSize() - 9); i += 9) {
			if (i > slot) {
				slot = i + 9;
				break;
			}
		}

		for (String uuid : offline) {
			if (slot < page * inv.getSize() || slot > page * inv.getSize() + (inv.getSize() - 10)) {
				//slot++;
				continue;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
			ItemStack skull = new ItemStack(Material.SKULL_ITEM);
			skull.setDurability((short) 3);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(target.getName());
			meta.setDisplayName(MSG.color("&c&l" + target.getName()));
			List<String> lore = new ArrayList<String>();
			lore.add(MSG.color("&rStatus: &cOffline"));
			lore.add(MSG.color("&rLast Seen: "
					+ TimeManager.getTime((double) System.currentTimeMillis() - (double) target.getLastPlayed())));
			meta.setLore(lore);
			skull.setItemMeta(meta);
			inv.setItem(slot%inv.getSize(), skull);
			slot++;
		}
		if (page > 0)
			inv.setItem(inv.getSize() - 9, new ItemStack(Material.ARROW));
		if (inv.getItem(inv.getSize() - 10) != null)
			inv.setItem(inv.getSize() - 1, new ItemStack(Material.ARROW));
		return inv;
	}
}
