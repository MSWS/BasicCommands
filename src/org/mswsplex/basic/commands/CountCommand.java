package org.mswsplex.basic.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class CountCommand implements CommandExecutor {
	public CountCommand() {
		Main.plugin.getCommand("count").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.count")) {
			MSG.noPerm(sender);
			return true;
		}
		for(OfflinePlayer target:Bukkit.getOfflinePlayers()) {
			MSG.tell(sender, target.getName()+" last joined "+ TimeManager.getTime((double) (System.currentTimeMillis()-target.getLastPlayed())));
		}
		MSG.tell(sender, MSG.getString("Command.Count.Count", "%prefix% %total%")
				.replace("%prefix%", MSG.getString("Command.Count.Prefix", "count"))
				.replace("%total%", Bukkit.getOfflinePlayers().length+""));
		return true;
	}
}
