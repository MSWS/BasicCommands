package org.mswsplex.basic.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class ReplyCommand implements CommandExecutor {
	public ReplyCommand() {
		Main.plugin.getCommand("reply").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		if(args.length==0) {
			return false;
		}
		Player player = (Player) sender, target;
		if(pManager.getInfo(player, "lastMessage")==null) {
			MSG.tell(sender, MSG.getString("Command.Message.NoRecent", "You haven't messaged anyone"));
			return true;
		}
		
		target = Bukkit.getPlayer(UUID.fromString(pManager.getString(player, "lastMessage")));
		if(target==null) {
			MSG.tell(sender, MSG.getString("Command.Message.Offline", "That player is no longer online"));
			return true;
		}
		
		String msg = "";
		
		for(String res:args)
			msg = msg + res +" ";
		msg = msg.trim();

		sender.sendMessage(MSG.color(MSG.getString("Command.Message.Sender", "%sender% > %receiver% %message%")
				.replace("%sender%", sender.getName())
				.replace("%receiver%", target.getName()))
				.replace("%message%", msg));
		
		target.sendMessage(MSG.color(MSG.getString("Command.Message.Receiver", "%sender% > %receiver% %message%")
				.replace("%sender%", sender.getName())
				.replace("%receiver%", target.getName()))
				.replace("%message%", msg));
		
		return true;
	}
}
