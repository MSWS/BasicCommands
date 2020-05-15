package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class TeleportCommand implements CommandExecutor, TabCompleter {
	public TeleportCommand() {
		Main.plugin.getCommand("teleport").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String prefix = MSG.getString("Command.Teleport.Prefix", "teleport");
		Player player;
		if (!sender.hasPermission("basic.teleport")) {
			MSG.noPerm(sender);
			return true;
		}
		if (args.length == 0) {
			return false;
		}
		if (args.length == 1) {
			if (sender instanceof Player) {
				player = (Player) sender;
				if (args[0].equals("all")) {
					if (sender.hasPermission("basic.teleport.all")) {
						for (Player tptarget : Bukkit.getOnlinePlayers()) {
							tptarget.teleport(player.getLocation());
							tptarget.sendMessage(
									MSG.color(prefix + " You were teleported to &e" + player.getDisplayName() + "&7."));
						}
						sender.sendMessage(MSG.color(prefix + " Succesfully teleported &eeveryone &7to you."));
					} else {
						MSG.noPerm(sender);
					}
					return true;
				}
				if (args[0].equals("here")) {
					if (sender instanceof Player) {
						if (player.hasPermission("basic.teleport.here")) {
							for (Player tptarget : ((Player) sender).getWorld().getPlayers()) {
								tptarget.teleport(player.getLocation());
								tptarget.sendMessage(MSG.color(
										prefix + " You were teleported to &e" + player.getDisplayName() + "&7."));
							}
							sender.sendMessage(
									MSG.color(prefix + " Succesfully teleported &eeveryone (same world) &7to you."));
						} else {
							MSG.noPerm(sender);
						}
						return true;
					} else {
						sender.sendMessage(MSG.color(prefix + "You must be a player!"));
					}

				}
				Player tpto = Bukkit.getPlayer(args[0]);
				for (Player t : Bukkit.getOnlinePlayers()) {
					if (args[0].equals(t.getDisplayName())) {
						tpto = t;
						break;
					}
				}
				if (tpto == null) {
					MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
				} else {
					player.teleport(tpto.getLocation());
					sender.sendMessage(
							MSG.color(prefix + " Succesfully teleported to &e" + tpto.getDisplayName() + "&7."));
				}
			} else {
				sender.sendMessage(MSG.color(prefix + " You must be a player."));
			}
		}
		if (args.length == 2) {
			Player tpme;
			Player tpto;
			if (args[0].equals("all")) {
				tpto = Bukkit.getPlayer(args[1]);

				for (Player t : Bukkit.getOnlinePlayers()) {
					if (!t.getDisplayName().equals(t.getName())) {
						if (args[1].equals(t.getDisplayName())) {
							tpto = t;
							break;
						}
					}
				}

				if (tpto == null) {
					MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
				} else {
					for (Player tptarget : Bukkit.getOnlinePlayers()) {
						tptarget.teleport(tpto.getLocation());
						tptarget.sendMessage(MSG.color("&7Teleport &8>> &e"
								+ ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName())
								+ " &7teleported you to &e" + tpto.getDisplayName() + "&7."));
					}
					tpto.sendMessage(MSG.color("&7Teleport &8>> &e"
							+ ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName())
							+ " &7teleported &eeveryone &7to you."));
				}
				return true;
			}
			if (args[0].equals("here")) {
				if (sender instanceof Player) {
					tpto = Bukkit.getPlayer(args[1]);
					for (Player t : Bukkit.getOnlinePlayers()) {
						if (!t.getDisplayName().equals(t.getName())) {
							if (args[1].equals(t.getDisplayName())) {
								tpto = t;
								break;
							}
						}
					}

					if (tpto == null) {
						MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
					} else {
						for (Player tptarget : ((Player) sender).getWorld().getPlayers()) {
							tptarget.teleport(tpto.getLocation());
							tptarget.sendMessage(MSG.color("&7Teleport &8>> &e"
									+ ((sender instanceof Player) ? ((Player) sender).getDisplayName()
											: sender.getName())
									+ " &7teleported you to &e" + tpto.getDisplayName() + "&7."));
						}
						tpto.sendMessage(MSG.color("&7Teleport &8>> &e"
								+ ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName())
								+ " &7teleported &eeveryone &7to you."));
					}
					return true;
				} else {
					sender.sendMessage(MSG.color(prefix + "You must be a player."));
				}
			}
			tpme = Bukkit.getPlayer(args[0]);
			tpto = Bukkit.getPlayer(args[1]);

			for (Player t : Bukkit.getOnlinePlayers()) {
				if (!t.getDisplayName().equals(t.getName())) {
					if (args[1].equals(t.getDisplayName())) {
						tpto = t;
					}
					if (args[0].equals(t.getDisplayName())) {
						tpme = t;
						break;
					}
				}
			}

			if (tpme == null || tpto == null) {
				MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
				return true;
			} else {
				tpme.teleport(tpto.getLocation());
				tpme.sendMessage(MSG.color("&7Teleport &8> &e"
						+ ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName())
						+ " &7teleported you to &e" + tpto.getDisplayName() + "&7."));
				sender.sendMessage(MSG.color(prefix + " Succesfully teleported &e" + tpme.getDisplayName() + " &7to &e"
						+ tpto.getDisplayName() + "&7."));
			}
		}
		if (args.length >= 3) {
			Player tptarget = null;
			if (args.length == 3) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(MSG.color(prefix + " You must be a player!"));
					return true;
				}
				tptarget = (Player) sender;
				tptarget.teleport(new Location(((Player) sender).getWorld(), Double.valueOf(args[0]),
						Double.valueOf(args[1]), Double.valueOf(args[2])));
				sender.sendMessage(MSG
						.color(prefix + " Succesfully teleported &7to &e" + args[0] + " " + args[1] + " " + args[2]));
			}
			if (args.length == 4) {
				if (args[0].equalsIgnoreCase("all")) {
					for (Player tptarget1 : Bukkit.getOnlinePlayers()) {
						tptarget1.teleport(new Location(((Player) sender).getWorld(), Double.valueOf(args[1]),
								Double.valueOf(args[2]), Double.valueOf(args[3])));
					}
					sender.sendMessage(MSG.color(prefix + " Succesfully teleported &eeveryone &7to &e" + args[1] + " "
							+ args[2] + " " + args[3]));
					return true;
				}
				if (args[0].equalsIgnoreCase("here")) {
					if (sender instanceof Player) {
						for (Player tptarget1 : Bukkit.getOnlinePlayers()) {
							tptarget1.teleport(new Location(((Player) sender).getWorld(), Double.valueOf(args[1]),
									Double.valueOf(args[2]), Double.valueOf(args[3])));
						}
						sender.sendMessage(MSG.color(prefix + " Succesfully teleported &eeveryone &7to &e" + args[1]
								+ " " + args[2] + " " + args[3]));
						return true;
					} else {
						sender.sendMessage(MSG.color(prefix + "You must be a player!"));
					}
				}

				tptarget = Bukkit.getPlayer(args[0]);

				for (Player t : Bukkit.getOnlinePlayers()) {
					if (!t.getDisplayName().equals(t.getName())) {
						if (args[0].equals(t.getDisplayName())) {
							tptarget = t;
							break;
						}
					}
				}
				if (tptarget == null) {
					MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
					return true;
				}

				tptarget.teleport(new Location(((Player) sender).getWorld(), Double.valueOf(args[1]),
						Double.valueOf(args[2]), Double.valueOf(args[3])));
				sender.sendMessage(MSG.color(prefix + " Succesfully teleported &e" + tptarget.getDisplayName()
						+ " &7to &e" + args[1] + " " + args[2] + " " + args[3]));

			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if (!sender.hasPermission("basic.teleport"))
			return result;
		for (Player t : Bukkit.getOnlinePlayers())
			if (t.getDisplayName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				result.add(t.getDisplayName());
		return result;
	}
}
