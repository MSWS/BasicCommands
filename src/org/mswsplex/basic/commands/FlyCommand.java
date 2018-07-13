package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class FlyCommand implements CommandExecutor {
	public FlyCommand() {
		Main.plugin.getCommand("fly").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.fly")) {
			MSG.noPerm(sender);
			return true;
		}
		Player target = null;
		if (sender instanceof Player) {
			target = (Player) sender;
		} else if (args.length == 0) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		
		if(args.length>0) {
			List<Player> results = Bukkit.matchPlayer(args[0]);
			if(results.size()==1) {
				target = results.get(0);
			}else if(results.size()==0){
				MSG.tell(sender, MSG.getString("Unkown.Player", "Unknown player"));
				return true;
			}else {
				MSG.tell(sender, MSG.getString("Unknown.ListPlayer", "%size% possible results")
						.replace("%size%", results.size()+""));
				return true;
			}
		}
		
		if(target==null) {
			MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown Player"));
			return true;
		}

		if(target!=sender&&!sender.hasPermission("basic.fly.others")) {
			MSG.noPerm(sender);
			return true;
		}
		
		if (target.getAllowFlight())
			target.setFlying(false);
		target.setAllowFlight(!target.getAllowFlight());

		if (sender != target) {
			MSG.tell(sender,
					MSG.getString("Command.Flight.Sender", "%player%%s% %status% %prefix%")
							.replace("%prefix%", MSG.getString("Command.Flight.Prefix", "Flight"))
							.replace("%player%", target.getName())
							.replace("%s%", target.getName().toLowerCase().endsWith("s") ? "" : "s")
							.replace("%status%", target.getAllowFlight() ? MSG.getString("Command.Enable", "enabled")
									: MSG.getString("Command.Disable", "disabled")));

			MSG.tell(target,
					MSG.getString("Command.Flight.Receiver", "%status% %prefix%")
							.replace("%prefix%", MSG.getString("Command.Flight.Prefix", "Flight"))
							.replace("%sender%", sender.getName())
							.replace("%status%", target.getAllowFlight() ? MSG.getString("Command.Enable", "enabled")
									: MSG.getString("Command.Disable", "disabled")));
		} else {
			MSG.tell(sender,
					MSG.getString("Command.Flight.Self", "%prefix% %status%")
							.replace("%prefix%", MSG.getString("Command.Flight.Prefix", "Flight"))
							.replace("%status%", target.getAllowFlight() ? MSG.getString("Command.Enable", "enabled")
									: MSG.getString("Command.Disable", "disabled")));
		}

		return true;
	}
}
