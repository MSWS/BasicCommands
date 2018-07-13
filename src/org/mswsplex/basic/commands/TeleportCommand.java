package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class TeleportCommand implements CommandExecutor {
	public TeleportCommand() {
		Main.plugin.getCommand("teleport").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.teleport")) {
			MSG.noPerm(sender);
			return true;
		}

		String name = "";
		Location target = new Location(null, 0, 0, 0);

		if (args.length == 0) {
			return false;
		}

		getLocation: if (args.length > 1) {
			if (sender instanceof Player) {
				if (args.length > 3) {
					target.setWorld(((Player) sender).getWorld());
					target.setX(Double.parseDouble(args[1]));
					target.setY(Double.parseDouble(args[2]));
					target.setZ(Double.parseDouble(args[3]));
					for (int i = 1; i < 4; i++)
						name = name + args[i] + " ";
					name = name.trim();
					break getLocation;
				} else if(args.length>2){
					target.setWorld(((Player) sender).getWorld());
					target.setX(Double.parseDouble(args[0]));
					target.setY(Double.parseDouble(args[1]));
					target.setZ(Double.parseDouble(args[2]));
					for (int i = 0; i < 3; i++)
						name = name + args[i] + " ";
					name = name.trim();
					break getLocation;
				}else {
					
					target = ((Player) sender).getLocation();
					name = sender.getName();
				}
			}

			List<Player> results = Bukkit.matchPlayer(args[1]);
			if (results.size() == 1) {
				target = results.get(0).getLocation();
				name = results.get(0).getName();
			} else if (results.size() == 0) {
				MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
				return true;
			} else {
				MSG.tell(sender, MSG.getString("Unknown.ListPlayer", "%size% possible results").replace("%size%",
						results.size() + ""));
				return true;
			}
		} else if (args.length == 1 ) {
			if(args[0].equals("all") || args[0].equalsIgnoreCase("everyone")) {
				if(!(sender instanceof Player)) {
					MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
					return true;
				}
				target = ((Player)sender).getLocation();
				name = sender.getName();
			}else {
				List<Player> results = Bukkit.matchPlayer(args[0]);
				if (results.size() == 1) {
					target = results.get(0).getLocation();
					name = results.get(0).getName();
				} else if (results.size() == 0) {
					MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
					return true;
				} else {
					MSG.tell(sender, MSG.getString("Unknown.ListPlayer", "%size% possible results").replace("%size%",
							results.size() + ""));
					return true;
				}
			}
		}

		switch (args[0].toLowerCase()) {
		case "all":
		case "everyone":
			if (!sender.hasPermission("basic.teleport.all")) {
				MSG.noPerm(sender);
				return true;
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.teleport(target);
				if(player==sender)
					continue;
				MSG.tell(player,
						MSG.getString("Command.Teleport.Receiver", "%sender% teleported you to %location%")
								.replace("%prefix%", MSG.getString("Command.Teleport.Prefix", "Player Manager"))
								.replace("%sender%", sender.getName()).replace("%target%", name));
			}
			MSG.tell(sender,
					MSG.getString("Command.Teleport.Sender", "you teleported %subject% to %target%")
							.replace("%prefix%", MSG.getString("Command.Teleport.Prefix", "Player Manager"))
							.replace("%subject%", "everyone").replace("%target%", name));
			break;
		default:
			if (!(sender instanceof Player)) {
				MSG.tell(sender, MSG.getString("MustBePlayer", "you must be a player"));
				return true;
			}
			((Player) sender).teleport(target);
			MSG.tell(sender, MSG.getString("Command.Teleport.Self", "you teleported to %target%").replace("%prefix%",
					MSG.getString("Command.Teleport.Prefix", "Player Manager"))
					.replace("%target%", name)
					);
			break;
		}

		return true;
	}
}
