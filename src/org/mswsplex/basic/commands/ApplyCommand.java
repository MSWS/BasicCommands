package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class ApplyCommand implements CommandExecutor {
	public ApplyCommand() {
		Main.plugin.getCommand("apply").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.apply")) {
			MSG.noPerm(sender);
			return true;
		}
		MSG.tell(sender, Main.plugin.config.getString("Links.Apply"));
		return true;
	}
}
