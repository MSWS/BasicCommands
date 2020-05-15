package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class HelpCommand implements CommandExecutor {
	public HelpCommand() {
		Main.plugin.getCommand("help").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.help")) {
			MSG.noPerm(sender);
			return true;
		}
		if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "you must be a player"));
			return true;
		}
		Player player = (Player) sender;
		
		for(String res:Main.plugin.config.getStringList("HelpMessage")) {
			MSG.tell(sender, MSG.parse(player, res));
		}
		return true;
	}
}
