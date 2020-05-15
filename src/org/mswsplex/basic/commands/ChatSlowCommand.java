package org.mswsplex.basic.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class ChatSlowCommand implements CommandExecutor {
	public ChatSlowCommand() {
		Main.plugin.getCommand("chatslow").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.chatslow")) {
			MSG.noPerm(sender);
			return true;
		}
		double slow = 10000;
		if(args.length>0) {
			switch(args[0].toLowerCase()) {
			case"off":
			case"disable":
			case"false":
				slow = 0;
				break;
			case"forever":
			case"silence":
				slow = -1;
				break;
			default:
				try {
					slow = Double.parseDouble(args[0])*1000;
				}catch(Exception e) {
				}
				break;
			}
		}else {
			MSG.tell(sender, MSG.getString("Command.ChatSlow.Default", "defaulting to %time%")
					.replace("%time%", TimeManager.getTime(slow)));
		}
		
		String msg = "";
		
		if(slow==-1) {
			msg = MSG.getString("Command.ChatSlow.Mute", "%player% muted chat");
		}else if(slow==0) {
			if(Main.plugin.data.getDouble("ChatSlow")==-1) {
				msg = MSG.getString("Command.ChatSlow.UnMute", "%player% unmuted chat");
			}else {
				msg = MSG.getString("Command.ChatSlow.Disabled", "%player% disabled chat slow");
			}
		}else {
			msg = MSG.getString("Command.ChatSlow.Enabled", "%player% enabled chat slow for %time%");
		}
		
		for(Player target:Bukkit.getOnlinePlayers())
			MSG.tell(target, msg.replace("%player%", (sender instanceof Player)?((Player)sender).getDisplayName():sender.getName())
					.replace("%status%", slow!=0?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled"))
					.replace("%time%", TimeManager.getTime(slow))
					.replace("%prefix%", MSG.getString("Command.ChatSlow.Prefix", "ChatSlow")));
		Main.plugin.data.set("ChatSlow", slow);
		return true;
	}
}
