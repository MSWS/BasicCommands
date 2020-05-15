package org.mswsplex.basic.utils;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.msws.basic.Main;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

import me.confuser.barapi.BarAPI;
import net.milkbowl.vault.economy.Economy;

public class BarManager {
	Scoreboard board;
	PlayerManager pManager = new PlayerManager();

	int tick = 0;
	double spd = 1;
	int length = 25;
	String name = "", prefix = "";
	Runtime runtime = Runtime.getRuntime();
	Economy eco = Main.plugin.getEcononomy();

	public void refresh() {
		System.gc();
	}

	public void register() {
		refresh();
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					MSG.tell(player, "tick: "+tick);
					if (pManager.getInfo(player, "bar") != null && !pManager.getBoolean(player, "bar")
							|| pManager.getInfo(player, "login") != null)
						continue;
					name = Main.plugin.config.getStringList("BarMessages.Messages").get(tick);
					BarAPI.setMessage(player, parse(player, name));
				}
				tick = (tick + 1) % (Main.plugin.config.getStringList("BarMessages.Messages").size());
			}
		}.runTaskTimer(Main.plugin, 0, (long) Main.plugin.config.getDouble("BarMessages.ChangeEvery"));

	}

	@SuppressWarnings("deprecation")
	private String parse(Player player, String entry) {
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
		if (result == null || Bukkit.getPluginManager().getPlugin("Factions") == null)
			return MSG.color(result);

		if (pManager.getInfo(player, "lastJoin") != null) {
			result = result.replace("%playtime%", TimeManager.getTime(pManager.getDouble(player, "playtime")
					+ (System.currentTimeMillis() - pManager.getDouble(player, "lastJoin"))));
		}

		if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			MPlayer mp = MPlayer.get(player);
			Faction f = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
			result = result.replace("%claimed%", f.getName());
			if (mp.hasFaction()) {
				result = result.replace("%faction%", mp.getFactionName())
						.replace("%power%", Utils.parseDecimal(mp.getPower() + "", 2))
						.replace("%maxpower%", Utils.parseDecimal(mp.getFaction().getPowerMax() + "", 2));
			} else {
				result = result.replace("%faction%", "None").replace("%power%", "0").replace("%maxpower%", "0");
			}
		} else {
			result = result.replace("%faction%", "None").replace("%power%", "0").replace("%maxpower%", "0");
		}
		// return MSG.color(result.substring(0, Math.min(result.length(), 40)));
		return MSG.color(result);
	}
}