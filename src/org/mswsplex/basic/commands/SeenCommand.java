package org.mswsplex.basic.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class SeenCommand implements CommandExecutor {
	public SeenCommand() {
		Main.plugin.getCommand("seen").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.seen")) {
			MSG.noPerm(sender);
			return true;
		}
		if (args.length == 0)
			return false;
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if (!target.hasPlayedBefore()) {
			MSG.tell(sender, MSG.getString("Command.Seen.NotJoined", "they haven't joined").replace("%prefix%",
					MSG.getString("Command.Seen.Prefix", "Seen")));
			return true;
		}

		if (target == sender) {
			if (!target.isOnline()) {
				MSG.tell(sender, "Whoa there buddy");
			}
			MSG.tell(sender, MSG.getString("Command.Seen.Self", "%prefix% %player% has been on for %time%")
					.replace("%prefix%", MSG.getString("Command.Seen.Prefix", "Seen")).replace("%time%",
							TimeManager.getTime((double) (System.currentTimeMillis() - target.getLastPlayed()))));
			return true;
		}

		if (target.isOnline()) {
			MSG.tell(sender,
					MSG.getString("Command.Seen.Online", "%prefix% %player% has been on for %time%")
							.replace("%prefix%", MSG.getString("Command.Seen.Prefix", "Seen"))
							.replace("%time%",
									TimeManager.getTime((double) (System.currentTimeMillis() - target.getLastPlayed())))
							.replace("%player%", target.getName()));
		} else {
			MSG.tell(sender,
					MSG.getString("Command.Seen.Seen", "%prefix% %player% joined %time% ago")
							.replace("%prefix%", MSG.getString("Command.Seen.Prefix", "Seen"))
							.replace("%time%",
									TimeManager.getTime((double) (System.currentTimeMillis() - target.getLastPlayed())))
							.replace("%player%", target.getName()));
		}
		return true;
	}
}
