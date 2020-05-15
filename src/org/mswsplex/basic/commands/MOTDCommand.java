package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class MOTDCommand implements CommandExecutor {
	public MOTDCommand() {
		Main.plugin.getCommand("motd").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.motd")) {
			MSG.noPerm(sender);
			return true;
		}
		if(args.length==0) {
			return false;
		}
		
		String motd = "";
		for(String res:args)
			motd = motd + res +" ";
		motd = motd.trim();
		Main.plugin.config.set("MOTD", motd);
		MSG.tell(sender, MSG.getString("Command.MOTD.Set", "updated MOTD")
				.replace("%prefix%", MSG.getString("Command.MOTD.Prefix", "MOTD")));
		Main.plugin.saveConfig();
		return true;
	}
}
