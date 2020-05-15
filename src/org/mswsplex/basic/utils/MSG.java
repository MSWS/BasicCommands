package org.mswsplex.basic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.msws.basic.Main;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

import net.milkbowl.vault.economy.Economy;

public class MSG {
	public static String color(String msg) {
		if (msg == null || msg.isEmpty())
			return null;
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String camelCase(String string) {
		String prevChar = " ";
		String res = "";
		for (int i = 0; i < string.length(); i++) {
			if (i > 0)
				prevChar = string.charAt(i - 1) + "";
			if (!prevChar.matches("[a-zA-Z]")) {
				res = res + ((string.charAt(i) + "").toUpperCase());
			} else {
				res = res + ((string.charAt(i) + "").toLowerCase());
			}
		}
		return res.replace("_", " ");
	}

	public static String getString(String id, String def) {
		return Main.plugin.lang.contains(id) ? Main.plugin.lang.getString(id) : "[" + id + "] " + def;
	}

	public static void tell(CommandSender sender, String msg) {
		if (msg != null && !msg.isEmpty())
			sender.sendMessage(color(msg.replace("%prefix%", prefix())));
	}

	public static void tell(World world, String msg) {
		if (world != null && msg != null) {
			for (Player target : world.getPlayers()) {
				tell(target, msg);
			}
		}
	}

	public static void tell(String perm, String msg) {
		for (Player target : Bukkit.getOnlinePlayers()) {
			if (target.hasPermission(perm))
				tell(target, msg);
		}
	}

	public static void tell(String msg) {
		for (Player target : Bukkit.getOnlinePlayers()) {
			tell(target, msg);
		}
	}

	public static String prefix() {
		return Main.plugin.config.contains("Prefix") ? Main.plugin.config.getString("Prefix") : "&9Teams>&7";
	}

	public static void noPerm(CommandSender sender) {
		tell(sender, getString("NoPermission", "Insufficient Permissions"));
	}

	public static void log(String msg) {
		tell(Bukkit.getConsoleSender(), "[" + Main.plugin.getDescription().getName() + "] " + msg);
	}

	public static String TorF(Boolean bool) {
		if (bool) {
			return "&aTrue&r";
		} else {
			return "&cFalse&r";
		}
	}

	public static void sendHelp(CommandSender sender, int page, String command) {
		if (!Main.plugin.lang.contains("Help." + command.toLowerCase())) {
			tell(sender, getString("UnknownCommand", "There is no help available for this command."));
			return;
		}
		int length = Main.plugin.config.getInt("HelpLength");
		List<String> help = Main.plugin.lang.getStringList("Help." + command.toLowerCase()),
				list = new ArrayList<String>();
		for (String res : help) {
			if (res.startsWith("perm:")) {
				String perm = "";
				res = res.substring(5, res.length());
				for (char a : res.toCharArray()) {
					if (a == ' ')
						break;
					perm = perm + a;
				}
				if (!sender.hasPermission("crystal." + perm))
					continue;
				res = res.replace(perm + " ", "");
			}
			list.add(res);
		}
		if (help.size() > length)
			tell(sender, "Page: " + (page + 1) + " of " + (int) Math.ceil((list.size() / length) + 1));
		for (int i = page * length; i < list.size() && i < page * length + length; i++) {
			String res = list.get(i);
			tell(sender, res);
		}
		if (command.equals("default"))
			tell(sender, "&d&lPlugin &ev" + Main.plugin.getDescription().getVersion() + " &7created by &bMSWS");
	}

	public static String progressBar(double prog, double total, int length) {
		return progressBar("&a\u258D", "&c\u258D", prog, total, length);
	}

	public static String progressBar(String progChar, String incomplete, double prog, double total, int length) {
		String disp = "";
		double progress = Math.abs(prog / total);
		int len = length;
		for (double i = 0; i < len; i++) {
			if (i / len < progress) {
				disp = disp + progChar;
			} else {
				disp = disp + incomplete;
			}
		}
		return color(disp);
	}

	/**
	 * if oldVer is < newVer, both versions can only have numbers and .'s Outputs:
	 * 5.5, 10.3 | true 2.3.1, 3.1.4.6 | true 1.2, 1.1 | false
	 **/
	public static Boolean outdated(String oldVer, String newVer) {
		oldVer = oldVer.replace(".", "");
		newVer = newVer.replace(".", "");
		Double oldV = null, newV = null;
		try {
			oldV = Double.valueOf(oldVer);
			newV = Double.valueOf(newVer);
		} catch (Exception e) {
			log("&cError! &7Versions incompatible.");
			return false;
		}
		if (oldVer.length() > newVer.length()) {
			newV = newV * (10 * (oldVer.length() - newVer.length()));
		} else if (oldVer.length() < newVer.length()) {
			oldV = oldV * (10 * (newVer.length() - oldVer.length()));
		}
		return oldV < newV;
	}

	public static void nonSpam(Player player, String msg) {
		PlayerManager pManager = new PlayerManager();
		if (pManager.getDouble(player, msg + "time") == null) {
			tell(player, msg);
			pManager.setInfo(player, msg + "time", (double) System.currentTimeMillis());
			return;
		}
		if (System.currentTimeMillis() - pManager.getDouble(player, msg + "time") > 2000) {
			tell(player, msg);
			pManager.setInfo(player, msg + "time", (double) System.currentTimeMillis());
			return;
		}
	}

	public static void nonSpam(Player player, String msg, double delay) {
		PlayerManager pManager = new PlayerManager();
		if (pManager.getDouble(player, msg) == null) {
			tell(player, msg);
			pManager.setInfo(player, msg, (double) System.currentTimeMillis());
			return;
		}
		if (System.currentTimeMillis() - pManager.getDouble(player, msg) > delay) {
			tell(player, msg);
			pManager.setInfo(player, msg, (double) System.currentTimeMillis());
			return;
		}
	}

	public static String parseDecimal(String name, int length) {
		if (name.contains(".")) {
			if (name.split("\\.")[1].length() > 2) {
				name = name.split("\\.")[0] + "."
						+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), length));
			}
		}
		return name;
	}

	static Runtime runtime = Runtime.getRuntime();
	static Economy eco = Main.plugin.getEcononomy();
	static PlayerManager pManager = new PlayerManager();

	@SuppressWarnings("deprecation")
	public static String parse(Player player, String entry) {
		int ping = 0;
		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String color = "&a";
		if (ping > 1000) {
			color = "&4";
		} else if (ping > 500) {
			color = "&c";
		} else if (ping > 300) {
			color = "&e";
		}
		Block target = null;
		try {
			target = player.getTargetBlock((Set<Material>) null, 100);
		} catch (Exception e) {
		}
		String result = MSG.color(entry.replace("%world%", player.getWorld().getName())
				.replace("%time%", Utils.worldTime(player.getWorld().getTime()))
				.replace("%online%", Bukkit.getOnlinePlayers().size() + "")
				.replace("%rank%", pManager.getPrefix(player).equals("") ? "Default" : pManager.getPrefix(player))
				.replace("%x%", Utils.parseDecimal(player.getLocation().getX() + "", 2))
				.replace("%y%", Utils.parseDecimal(player.getLocation().getY() + "", 2))
				.replace("%z%", Utils.parseDecimal(player.getLocation().getZ() + "", 2))
				.replace("%clientground%", MSG.TorF(player.isOnGround()))
				.replace("%serverground%", MSG.TorF(player.getLocation().getY() % .5 == 0))
				.replace("%totalmemory%", runtime.totalMemory() / 1048576L + "")
				.replace("%freememory%", runtime.freeMemory() / 1048576L + "")
				.replace("%usedmemory%", (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + "")
				.replace("%memory%",
						Utils.parseDecimal((((double) runtime.totalMemory() - (double) runtime.freeMemory())
								/ (double) runtime.totalMemory()) * 100.0 + "", 2))
				.replace("%ping%", color + ping).replace("%targetblock%", MSG.camelCase(target.getType().toString()))
				.replace("%uuid%", player.getUniqueId() + "").replace("%flying%", MSG.TorF(player.isFlying()))
				.replace("%pitch%", Utils.parseDecimal(player.getLocation().getPitch() + "", 2))
				.replace("%yaw%", Utils.parseDecimal(player.getLocation().getYaw() + "", 2))
				.replace("%vanish%", MSG.TorF(pManager.isVanished(player))));
		if (!player.getName().equals(player.getDisplayName())) {
			result = result.replace("%player%", player.getName() + " &3(&b" + player.getDisplayName() + "&3)");
		} else {
			result = result.replace("%player%", player.getDisplayName());
		}
		if (eco != null) {
			result = result.replace("%balance%", Utils.parseDecimal(eco.getBalance(player) + "", 2));
		} else {
			result = result.replace("%balance%", "0");

		}

		if (pManager.getInfo(player, "lastJoin") != null) {
			result = result.replace("%playtime%", TimeManager.getTime(pManager.getDouble(player, "playtime")
					+ (System.currentTimeMillis() - pManager.getDouble(player, "lastJoin"))));
		}

		if (result == null || Bukkit.getPluginManager().getPlugin("Factions") == null)
			return MSG.color(result);

		if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			MPlayer mp = MPlayer.get(player);
			Faction f = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
			result = result.replace("%claimed%", f.getName());
			if (mp.hasFaction()) {
				result = result.replace("%faction%", mp.getFactionName())
						.replace("%power%", Utils.parseDecimal(mp.getFaction().getPower() + "", 2))
						.replace("%maxpower%", Utils.parseDecimal(mp.getFaction().getPowerMax() + "", 2))
						.replace("%factiononline%", mp.getFaction().getOnlinePlayers().size() + "");
			} else {
				result = result.replace("%faction%", "None").replace("%power%", "0").replace("%maxpower%", "0")
						.replace("%factiononline%", "0");
			}
		} else {
			result = result.replace("%faction%", "None").replace("%power%", "0").replace("%maxpower%", "0");
		}
		// return MSG.color(result.substring(0, Math.min(result.length(), 40)));
		return MSG.color(result);
	}
}
