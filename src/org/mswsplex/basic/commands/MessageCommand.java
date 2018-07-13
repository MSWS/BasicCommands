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

public class MessageCommand implements CommandExecutor {
	public MessageCommand() {
		Main.plugin.getCommand("message").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.message")) {
			MSG.noPerm(sender);
			return true;
		}
		if (args.length < 2) {
			return false;
		}

		Player target;

		List<Player> results = Bukkit.matchPlayer(args[0]);
		if (results.size() == 1) {
			target = results.get(0);
		} else if (results.size() == 0) {
			MSG.tell(sender, MSG.getString("Unkown.Player", "Unknown player"));
			return true;
		} else {
			MSG.tell(sender, MSG.getString("Unknown.ListPlayer", "%size% possible results").replace("%size%",
					results.size() + ""));
			return true;
		}

		String msg = "";

		for (String res : args)
			if (!res.equals(args[0]))
				msg = msg + res + " ";

		msg = msg.trim();
		
		
		
		sender.sendMessage(MSG.color(MSG.getString("Command.Message.Sender", "%sender% > %receiver% %message%")
				.replace("%sender%", sender.getName())
				.replace("%receiver%", target.getName()))
				.replace("%message%", msg));
		
		target.sendMessage(MSG.color(MSG.getString("Command.Message.Receiver", "%sender% > %receiver% %message%")
				.replace("%sender%", sender.getName())
				.replace("%receiver%", target.getName()))
				.replace("%message%", msg));
		
		if(sender instanceof Player)
			pManager.setInfo((Player)sender, "lastMessage", target.getUniqueId()+"");
		return true;
	}
}
