package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class MessageCommand implements CommandExecutor, TabCompleter {
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

		Player target = null;

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
		
		if (target == null) {
			MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
			return true;
		}

		String msg = "";

		for (String res : args)
			if (!res.equals(args[0]))
				msg = msg + res + " ";

		msg = msg.trim();
		
		sender.sendMessage(MSG.color(MSG.getString("Command.Message.Sender", "%sender% > %receiver% %message%")
				.replace("%sender%", (sender instanceof Player)?((Player)sender).getDisplayName():sender.getName())
				.replace("%receiver%", target.getDisplayName()))
				.replace("%message%", msg));
		
		target.sendMessage(MSG.color(MSG.getString("Command.Message.Receiver", "%sender% > %receiver% %message%")
				.replace("%sender%", (sender instanceof Player)?((Player)sender).getDisplayName():sender.getName())
				.replace("%receiver%", target.getDisplayName()))
				.replace("%message%", msg));
		
		if(sender instanceof Player)
			pManager.setInfo((Player)sender, "lastMessage", target.getUniqueId()+"");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		for (Player t : Bukkit.getOnlinePlayers())
			if (t.getDisplayName().toLowerCase().startsWith(args[0].toLowerCase()))
				result.add(t.getDisplayName());
		return result;
	}
}
