package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.Utils;
import org.mswsplex.msws.basic.Main;

public class TimeCommand implements CommandExecutor {
	public TimeCommand() {
		Main.plugin.getCommand("time").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.time")) {
			MSG.noPerm(sender);
			return true;
		}

		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			MSG.tell(sender,
					MSG.getString("Command.Time.Info", "%prefix% time is %time%")
							.replace("%prefix%", MSG.getString("Command.Time.Prefix", "Time"))
							.replace("%time%", Utils.worldTime(player.getWorld().getTime()))
							.replace("%ticks%", player.getWorld().getTime() + ""));
			return true;
		}
		switch (args[0].toLowerCase()) {
		case "set":
			if (args.length < 2)
				return false;
			long time = 0;
			switch (args[1].toLowerCase()) {
			case "morning":
				time = 0;
				break;
			case "day":
				time = 1000;
				break;
			case "night":
				time = 12000;
				break;
			case "midnight":
				time = 18000;
				break;
			case "sunset":
				time = 12500;
				break;
			case "sunrise":
				time = 23500;
				break;
			case "midday":
				time = 800;
				break;
			default:
				try {
					time = Long.parseLong(args[1]);
				} catch (Exception e) {
					return false;
				}
			}
			player.getWorld().setTime(time);
			MSG.tell(sender,
					MSG.getString("Command.Time.Set", "you set it to %time%")
							.replace("%prefix%", MSG.getString("Command.Time.Prefix", "Time"))
							.replace("%time%", Utils.worldTime(time)));
			break;
		case "add":
			if (args.length < 2)
				return false;
			try {
				long add = Long.parseLong(args[1]);
				player.getWorld().setTime(player.getWorld().getTime() + add);
				MSG.tell(sender,
						MSG.getString("Command.Time.Set", "you set it to %time%")
								.replace("%prefix%", MSG.getString("Command.Time.Prefix", "Time"))
								.replace("%time%", Utils.worldTime(player.getWorld().getTime())));
			} catch (Exception e) {
				return false;
			}
			break;
		case "stop":
			MSG.tell(sender,
					MSG.getString("Command.Time.Toggle", "you %status% time")
							.replace("%status%", MSG.getString("Command.Disable", "disabled"))
							.replace("%prefix%", MSG.getString("Command.Time.Prefix", "Time")));
			player.getWorld().setGameRuleValue("doDaylightCycle", "false");
			break;
		case "start":
			MSG.tell(sender,
					MSG.getString("Command.Time.Toggle", "you %status% time")
							.replace("%status%", MSG.getString("Command.Enable", "enabled"))
							.replace("%prefix%", MSG.getString("Command.Time.Prefix", "Time")));
			player.getWorld().setGameRuleValue("doDaylightCycle", "true");
			break;
		default:
			return false;
		}

		return true;
	}
}
