package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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

public class KitCommand implements CommandExecutor, TabCompleter {
	public KitCommand() {
		Main.plugin.getCommand("kit").setExecutor(this);
		Main.plugin.getCommand("kit").setTabCompleter(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.kit")) {
			MSG.noPerm(sender);
			return true;
		}
//		if (!(sender instanceof Player)) {
//			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be player"));
//			return true;
//		}

		Player target = null;

		if (args.length == 0) {
			String names = "";
			for (String res : Main.plugin.config.getConfigurationSection("Kits").getKeys(false)) {
				if (sender.hasPermission("basic.kit." + res))
					names = names +"&e"+ res + "&7, ";
			}
			if(names.equals("")) {
				names = "&cNo kit available";
			}else {
				names = names.substring(0, Math.max(0, names.length()-2));
			}
			MSG.tell(sender, MSG.getString("Command.Kit.Prefix", "Kit")+" "+names);
			return true;
		}
		
		if (args.length == 1||!sender.hasPermission("basic.kit.others")) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
		} else {
			List<Player> results = Bukkit.matchPlayer(args[1]);
			if (results.size() == 1) {
				target = results.get(0);
			}

			for (Player t : Bukkit.getOnlinePlayers()) {
				if (!t.getDisplayName().equals(t.getName())) {
					if (args[0].equals(t.getDisplayName())) {
						target = t;
						break;
					}
				}
			}
		}
		

		if (target == null) {
			MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
			return true;
		}
		
		if (!Main.plugin.config.contains("Kits." + args[0])) {
			MSG.tell(sender, MSG.getString("Command.Kit.Unknown", "Unknown Kit").replace("%prefix%",
					MSG.getString("Command.Kit.Prefix", "Kit")));
			return true;
		}
		if (!sender.hasPermission("basic.kit." + args[0])) {
			MSG.noPerm(sender);
			return true;
		}
		if(sender instanceof Player&&!sender.hasPermission("basic.bypass.kitcooldown")) {
			Player player = (Player) sender;
			if (pManager.getInfo((Player) sender, "kit" + args[0]) != null) {
				if (pManager.getDouble(player, "kit" + args[0])
						+ Main.plugin.config.getDouble("Kits." + args[0] + ".Delay") > System.currentTimeMillis()) {
					MSG.tell(sender,
							MSG.getString("Command.Kit.Delay", "kit is on cooldown for %time%")
									.replace("%time%",
											TimeManager.getTime((pManager.getDouble(player, "kit" + args[0])
													+ Main.plugin.config.getDouble("Kits." + args[0] + ".Delay"))
													- System.currentTimeMillis()))
									.replace("%prefix%", MSG.getString("Command.Kit.Prefix", "Kit")));
					return true;
				}
			}
		}

		for (String res : Main.plugin.config.getConfigurationSection("Kits." + args[0]).getKeys(false)) {
			if (Main.plugin.config.contains("Kits." + args[0] + "." + res + ".Icon")) {
				target.getInventory()
						.addItem(pManager.parseItem(Main.plugin.config, "Kits." + args[0] + "." + res, target));
			}
			if (Main.plugin.config.contains("Kits." + args[0] + "." + res + ".Commands")) {
				for (String cmd : Main.plugin.config.getStringList("Kits." + args[0] + "." + res + ".Commands"))
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", target.getDisplayName()));
			}
		}
		MSG.tell(target, MSG.getString("Command.Kit.Received", "kit %kit% received").replace("%kit%", args[0])
				.replace("%prefix%", MSG.getString("Command.Kit.Prefix", "Kit")));
		pManager.setInfo(target, "kit" + args[0], System.currentTimeMillis());
		Main.plugin.saveData();
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		ConfigurationSection kits = Main.plugin.config.getConfigurationSection("Kits");
		if (kits == null)
			return result;
		if (args.length > 1) {
			if (sender.hasPermission("basic.warp.others"))
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (target.getDisplayName().toLowerCase().startsWith(args[args.length-1].toLowerCase()))
						result.add(target.getDisplayName());
				}
		} else {
			for (String res : kits.getKeys(false)) {
				if (sender.hasPermission("basic.kit." + res) && args.length < 2 && res.toLowerCase().startsWith(args[0]))
					result.add(res);
			}
		}
		return result;
	}

}
