package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class WarpCommand implements CommandExecutor, TabCompleter {
	public WarpCommand() {
		Main.plugin.getCommand("warp").setExecutor(this);
		Main.plugin.getCommand("warp").setTabCompleter(this);

	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}

		Player player = (Player) sender, tpme = player;

		if (!sender.hasPermission("basic.warp")) {
			MSG.noPerm(sender);
			return true;
		}

		if (args.length == 0) {
			String names = "";
			for (String res : Main.plugin.data.getConfigurationSection("Warps").getKeys(false)) {
				if (sender.hasPermission("basic.warp." + res))
					names = names +"&e"+ res + "&7, ";
			}
			if(names.equals("")) {
				names = "&cNo warps available";
			}else {
				names = names.substring(0, Math.max(0, names.length()-2));
			}
			MSG.tell(sender, MSG.getString("Command.Warp.Prefix", "Warp")+" "+names);
			return true;
		}

		if (pManager.getInfo(tpme, "lastHit") != null) {
			if (pManager.getDouble(tpme, "lastHit") + 10000 > System.currentTimeMillis()) {
				MSG.tell(sender,
						MSG.getString("Command.Warp.InCombat", "in combat for %time%").replace("%time%",
								TimeManager.getTime(
										pManager.getDouble(tpme, "lastHit") + 10000 - System.currentTimeMillis()))
								.replace("%prefix%", MSG.getString("Command.Warp.Prefix", "Warp")));
				return true;
			}
		}

		Player target = player;
		String name = args[0];

		if (args.length > 1) {
			name = args[1];
			if (sender.hasPermission("basic.warp.others")) {
				for (Player t : Bukkit.getOnlinePlayers()) {
					if (args[0].equals(t.getDisplayName())) {
						target = t;
						break;
					}
				}
			}
		}
		
		ConfigurationSection warps = Main.plugin.data.getConfigurationSection("Warps");
		if (warps != null) {
			for(String res:warps.getKeys(false)) {
				if(name.equalsIgnoreCase(res)) {
					name = res;
					break;
				}
			}
		}
		
		if(!sender.hasPermission("basic.warp."+name)) {
			MSG.noPerm(sender);
			return true;
		}

		if (!Main.plugin.data.contains("Warps." + name)) {
			MSG.tell(sender, MSG.getString("Command.Warp.Unknown", "Unknown Warp").replace("%prefix%",
					MSG.getString("Command.Warp.Prefix", "Warp")));
			return true;
		}
		target.teleport((Location) Main.plugin.data.get("Warps." + name));

		if (target == player) {
			MSG.tell(player, MSG.getString("Command.Warp.Self", "you warped to %warp%").replace("%warp%", name)
					.replace("%prefix%", MSG.getString("Command.Warp.Prefix", "Warp")));
		} else {
			MSG.tell(player,
					MSG.getString("Command.Warp.Other", "you warped %target% to %warp%").replace("%warp%", name)
							.replace("%prefix%", MSG.getString("Command.Warp.Prefix", "Warp"))
							.replace("%target%", target.getDisplayName()));
		}

		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if (!sender.hasPermission("basic.warp"))
			return result;
		ConfigurationSection warps = Main.plugin.data.getConfigurationSection("Warps");
		if (warps == null)
			return result;
		if (args.length <= 1) {
			if (sender.hasPermission("basic.warp.others"))
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (target.getDisplayName().toLowerCase().startsWith(args[0].toLowerCase()))
						result.add(target.getDisplayName());
				}
			for (String res : warps.getKeys(false)) {
				if (res.toLowerCase().startsWith(args[0].toLowerCase())&&sender.hasPermission("basic.warp."+res))
					result.add(res);
			}
		} else {
			for (String res : warps.getKeys(false)) {
				if (res.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(res);
			}
		}
		return result;
	}
}
