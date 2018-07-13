package org.mswsplex.basic.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class ACommand implements CommandExecutor {
	public ACommand() {
		Main.plugin.getCommand("a").setExecutor(this);
		Main.plugin.getCommand("ma").setExecutor(this);
		Main.plugin.getCommand("ra").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String msg;
		Player p;
		switch (command.getName().toLowerCase()) {
		case "a":
			if (!sender.hasPermission("basic.a")) {
				MSG.noPerm(sender);
				return true;
			}
			if (args.length == 0)
				return false;
			msg = "";
			for (String res : args)
				msg = msg + res + " ";
			msg = msg.trim();
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (target == sender || target.hasPermission("basic.a.receive")) {
					MSG.tell(target,
							MSG.getString("Command.A.Regular", "%prefix% %player-prefix%%player% %message%")
									.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff"))
									.replace("%player-prefix%",
											(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
									.replace("%player%", sender.getName()).replace("%message%", msg));
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 2);
				}
			}
			break;
		case "ma":
			if (!sender.hasPermission("basic.ma")) {
				MSG.noPerm(sender);
				return true;
			}
			if (args.length < 2)
				return false;

			List<Player> results = Bukkit.matchPlayer(args[0]);
			if (results.size() == 1) {
				p = results.get(0);
			} else if (results.size() == 0) {
				MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
				return true;
			} else {
				MSG.tell(sender, MSG.getString("Unknown.ListPlayer", "%size% possible results").replace("%size%",
						results.size() + ""));
				return true;
			}

			msg = "";
			for (String res : args)
				if (!res.equals(args[0]))
					msg = msg + res + " ";

			if (sender instanceof Player)
				pManager.setInfo((Player) sender, "lastAMessage", p.getUniqueId() + "");

			for (Player target : Bukkit.getOnlinePlayers()) {
				if (target == sender) {
					MSG.tell(target,
							MSG.getString("Command.A.Sender", "%prefix% -> %target-prefix%%target% %message%")
									.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff"))
									.replace("%target-prefix%", pManager.getPrefix(target))
									.replace("%target%", sender.getName()).replace("%message%", msg));
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 2);
				}
				if (target == p) {
					MSG.tell(target,
							MSG.getString("Command.A.Receiver", "%prefix% -> %target-prefix%%target% %message%")
									.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff"))
									.replace("%sender-prefix%",
											(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
									.replace("%sender%", sender.getName()).replace("%message%", msg));
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 2);
					continue;
				}

				if (target.hasPermission("basic.ma.receive")) {
					MSG.tell(target,
							MSG.getString("Command.A.Receiver",
									"%prefix% %sender-prefix%%sender% -> %target-prefix%%target% %message%")
									.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff"))
									.replace("%sender-prefix%",
											(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
									.replace("%sender%", sender.getName()).replace("%message%", msg)
									.replace("%sender-prefix%",
											(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
									.replace("%sender%", sender.getName()).replace("%message%", msg));
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 1);
				}
			}
			break;
		case "ra":
			if(args.length==0)
				return false;
			if (!(sender instanceof Player)) {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
			Player player = (Player) sender;
			if (pManager.getInfo(player, "lastAMessage") == null) {
				MSG.tell(sender, MSG.getString("Command.A.NoRecent", "You haven't admin messaged anyone recently")
						.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff")));
				return true;
			}

			p = Bukkit.getPlayer(UUID.fromString(pManager.getString(player, "lastAMessage")));
			if (!p.isOnline()) {
				MSG.tell(sender, MSG.getString("Command.A.Offline", "That player is now offline").replace("%prefix%",
						MSG.getString("Command.A.Prefix", "Staff")));
				return true;
			}

			msg = "";
			for (String res : args)
				msg = msg + res + " ";

			for (Player target : Bukkit.getOnlinePlayers()) {
				if (target == sender) {
					MSG.tell(target,
							MSG.getString("Command.A.Sender", "%prefix% -> %target-prefix%%target% %message%")
									.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff"))
									.replace("%target-prefix%", pManager.getPrefix(target))
									.replace("%target%", sender.getName()).replace("%message%", msg));
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 2);
				}
				if (target == p) {
					MSG.tell(target,
							MSG.getString("Command.A.Receiver", "%prefix% -> %target-prefix%%target% %message%")
									.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff"))
									.replace("%sender-prefix%",
											(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
									.replace("%sender%", sender.getName()).replace("%message%", msg));
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 2);
					continue;
				}

				if (target.hasPermission("basic.ma.receive")) {
					MSG.tell(target,
							MSG.getString("Command.A.Receiver",
									"%prefix% %sender-prefix%%sender% -> %target-prefix%%target% %message%")
									.replace("%prefix%", MSG.getString("Command.A.Prefix", "Staff"))
									.replace("%sender-prefix%",
											(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
									.replace("%sender%", sender.getName()).replace("%message%", msg)
									.replace("%sender-prefix%",
											(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
									.replace("%sender%", sender.getName()).replace("%message%", msg));
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 1);
				}
			}

			break;
		}
		return true;
	}
}
