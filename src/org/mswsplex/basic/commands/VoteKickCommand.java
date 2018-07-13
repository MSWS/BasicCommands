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

public class VoteKickCommand implements CommandExecutor {
	public VoteKickCommand() {
		Main.plugin.getCommand("votekick").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.votekick")) {
			MSG.noPerm(sender);
			return true;
		}
		
		Player target = null;
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
		
		if(target.hasPermission("basic.votekick.bypass")) {
			MSG.noPerm(sender);
			return true;
		}
		return true;
	}
}
