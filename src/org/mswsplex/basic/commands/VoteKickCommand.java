package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
		
		if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		
		
		
		Player player = (Player) sender, target = null;
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
		
		
		ConfigurationSection voteKicks = Main.plugin.data.getConfigurationSection("Players."+player.getUniqueId()+".votekicks");
		int id = 0;
		if(voteKicks!=null) {
			int recentVotes = 0;
			for(String res:voteKicks.getKeys(false)) {
				if(System.currentTimeMillis()-voteKicks.getDouble(res+".time")<3.6e+6) {
					recentVotes++;
				}
			}
			if(recentVotes>2) {
				MSG.tell(sender, MSG.getString("Command.VoteKick.Limit", "you've met the limit for voting"));
				return true;
			}
			while(voteKicks.contains(id+""))
				id++;
		}
		id++;
		voteKicks.set(id+".uuid", target.getUniqueId()+"");
		MSG.tell(sender, MSG.getString("Command.VoteKick.Voting", "you're voting to kick %player%")
				.replace("%prefix%", MSG.getString("Command.VoteKick.Prefix", "VoteKick"))
				.replace("%player%", target.getName()));
		return true;
	}
}
