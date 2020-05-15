package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class GodCommand implements CommandExecutor {
		public GodCommand() {
			Main.plugin.getCommand("god").setExecutor(this);
		}

		PlayerManager pManager = new PlayerManager();
		
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (!sender.hasPermission("basic.god")) {
				MSG.noPerm(sender);
				return true;
			}
			Player target = null;

			if (args.length == 0) {
				if (sender instanceof Player) {
					target = (Player) sender;
				} else {
					MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
					return true;
				}
			} else {
				List<Player> results = Bukkit.matchPlayer(args[0]);
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
			
			if(target!=sender&&!sender.hasPermission("basic.god.others")) {
				MSG.noPerm(sender);
				return true;
			}
			
			pManager.setInfo(target, "god", !pManager.isGod(target));
			
			if(sender==target) {
				MSG.tell(sender, MSG.getString("Command.God.Self", "%prefix% you %status% %player%'%s% god status")
						.replace("%prefix%", MSG.getString("Command.God.Prefix", "God"))
						.replace("%status%", pManager.isGod(target)?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled")));
			}else {
				MSG.tell(sender, MSG.getString("Command.God.Sender", "%prefix% you %status% %player%'%s% god status")
						.replace("%prefix%", MSG.getString("Command.God.Prefix", "God"))
						.replace("%player%", target.getDisplayName())
						.replace("%s%", target.getDisplayName().toLowerCase().endsWith("s")?"":"s")
						.replace("%status%", pManager.isGod(target)?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled")));
				MSG.tell(target, MSG.getString("Command.God.Receiver", "%prefix% you %status% %player%'%s% god status")
						.replace("%prefix%", MSG.getString("Command.God.Prefix", "God"))
						.replace("%sender%", (sender instanceof Player)?((Player)sender).getDisplayName():sender.getName())
						.replace("%status%", pManager.isGod(target)?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled")));
			}
			
			return true;
		}
}
