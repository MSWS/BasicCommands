package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class PlaytimeCommand implements CommandExecutor {
	public PlaytimeCommand() {
		Main.plugin.getCommand("playtime").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.playtime")) {
			MSG.noPerm(sender);
			return true;
		}

		OfflinePlayer target;

		if (args.length < 1) {
			if (!(sender instanceof Player)) {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
			target = (Player) sender;
		} else {
			if (args.length > 2) {
				if (args[0].equalsIgnoreCase("set")) {
					if (!sender.hasPermission("basic.playtime.set")) {
						MSG.noPerm(sender);
						return true;
					}
					target = Bukkit.getOfflinePlayer(args[1]);
					pManager.setInfo(target, "playtime", TimeManager.getMills(args[2]));
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("top")) {
				if (!sender.hasPermission("basic.playtime.top")) {
					MSG.noPerm(sender);
					return true;
				}
				int size = 5;
				if(args.length>1) {
					try {
						size = Integer.valueOf(args[1]);
					}catch(Exception e) {
						MSG.tell(sender, "&cInvalid number");
					}
				}
				Map<String, Double> playtimes = new HashMap<>();
				for (OfflinePlayer t : Bukkit.getOfflinePlayers()) {
					double cTime = 0;
					if (t.isOnline()) {
						cTime = pManager.getDouble(t, "playtime")
								+ (System.currentTimeMillis() - pManager.getDouble(t, "lastJoin"));
					} else {
						cTime = pManager.getDouble(t, "playtime");
					}
					playtimes.put(t.getUniqueId().toString(), cTime);
				}
				
				final List<Map.Entry<String, Double>> sorted = new ArrayList<Map.Entry<String, Double>>(
						playtimes.entrySet());
				Collections.sort(sorted, new Comparator<Map.Entry<String, Double>>() {
					public int compare(final Entry<String, Double> entry1, final Entry<String, Double> entry2) {
						return entry2.getValue().compareTo(entry1.getValue());
					}
				});
				MSG.tell(sender, "&7Listing top Playtimes");
				for(int i=0;i<size;i++) {
					Entry<String, Double> entry = sorted.get(i);
					OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
					double playtime = entry.getValue();
					MSG.tell(sender, " &a"+t.getName()+" &7has played for &e"+TimeManager.getTime(playtime));
				}
				return true;
			}
			target = Bukkit.getOfflinePlayer(args[0]);
		}

		if (target != sender && !sender.hasPermission("basic.playtime.others")) {
			MSG.noPerm(sender);
			return true;
		}

		if (target == sender) {
			MSG.tell(sender,
					MSG.getString("Command.Playtime.Self", "you've played for %time%")
							.replace("%time%",
									TimeManager.getTime(pManager.getDouble(target, "playtime")
											+ (System.currentTimeMillis() - pManager.getDouble(target, "lastJoin"))))
							.replace("%prefix%", MSG.getString("Command.Playtime.Prefix", "Playtime")));

		} else {
			if (target.isOnline()) {
				MSG.tell(sender,
						MSG.getString("Command.Playtime.Other", "you've played for %time%")
								.replace("%time%", TimeManager.getTime(pManager.getDouble(target, "playtime")
										+ (System.currentTimeMillis() - pManager.getDouble(target, "lastJoin"))))
								.replace("%prefix%", MSG.getString("Command.Playtime.Prefix", "Playtime"))
								.replace("%player%", target.getName()));
			} else {
				if (pManager.getInfo(target, "playtime") == null) {
					MSG.tell(sender,
							MSG.getString("Command.Playtime.NotJoined", "they haven't joined")
									.replace("%prefix%", MSG.getString("Command.Playtime.Prefix", "Playtime"))
									.replace("%player%", target.getName()));
					return true;
				}
				MSG.tell(sender,
						MSG.getString("Command.Playtime.Other", "you've played for %time%")
								.replace("%time%", TimeManager.getTime(pManager.getDouble(target, "playtime")))
								.replace("%prefix%", MSG.getString("Command.Playtime.Prefix", "Playtime"))
								.replace("%player%", target.getName()));
			}
		}

		return true;
	}
}
