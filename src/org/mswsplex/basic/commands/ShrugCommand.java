package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class ShrugCommand implements CommandExecutor {
	public ShrugCommand() {
		Main.plugin.getCommand("shrug").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.shrug")) {
			MSG.noPerm(sender);
			return true;
		}
		if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		String msg = "";
		for(String res:args) {
			msg = msg + res +" ";
		}
		msg = msg+"¯\\_(ツ)_/¯";
		Player player = (Player) sender;
		player.chat(msg);
		return true;
	}
}
