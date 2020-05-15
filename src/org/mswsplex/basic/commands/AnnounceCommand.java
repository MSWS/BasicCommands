package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class AnnounceCommand implements CommandExecutor {
	public AnnounceCommand() {
		Main.plugin.getCommand("announce").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.announce")) {
			MSG.noPerm(sender);
			return true;
		}
		if(args.length==0)
			return false;
		String perm = "";
		if(args[0].startsWith("perm:")) {
			perm = args[0].substring("perm:".length());
			if(args.length==0) {
				
			}
		}
		String msg = "";
		for(String res:args) {
			if(res.equals(args[0])&&res.startsWith("perm:"))
				continue;
			msg = msg + res+" ";	
		}
		msg = msg.trim();
		if(perm.equals("")) {
			for(String res:Main.plugin.lang.getStringList("Command.Announce.Format")) {
				MSG.tell(res.replace("%message%", msg));
			}
		}else {
			for(String res:Main.plugin.lang.getStringList("Command.Announce.Format")) {
				MSG.tell(perm, res.replace("%message%", msg));
			}
			MSG.tell(sender, MSG.getString("Command.Announce.Special", "you announced to %subject%").replace("%subject%", perm).replace("%prefix%", MSG.getString("Command.Announce.Prefix", "Announce")));
		}
		return true;
	}
}
