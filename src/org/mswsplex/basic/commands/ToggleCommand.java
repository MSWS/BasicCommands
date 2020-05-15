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

public class ToggleCommand implements CommandExecutor,TabCompleter {
	public ToggleCommand() {
		Main.plugin.getCommand("toggle").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.toggle")) {
			MSG.noPerm(sender);
			return true;
		}
		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be player"));
			return true;
		}
		
		
		Player player = (Player) sender;
		if(args.length==0)
			return false;
		
		switch(args[0].toLowerCase()) {
		case"scoreboard":
			if(pManager.getInfo(player, "scoreboard")==null) {
				pManager.setInfo(player, "scoreboard", true);
			}
			pManager.setInfo(player, "scoreboard", !pManager.getBoolean(player, "scoreboard"));
			if(!pManager.getBoolean(player, "scoreboard")) {
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
			MSG.tell(sender, MSG.getString("Command.Toggle.Scoreboard", "you %status% your scoreboard")
					.replace("%status%", pManager.getBoolean(player, "scoreboard")?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled"))
					.replace("%prefix%", MSG.getString("Command.Toggle.Prefix", "Toggle")));
			break;
		case"mention":
			if(pManager.getInfo(player, "mention")==null) {
				pManager.setInfo(player, "mention", true);
			}
			pManager.setInfo(player, "mention", !pManager.getBoolean(player, "mention"));
			MSG.tell(sender, MSG.getString("Command.Toggle.Mention", "you %status% being mentioned")
					.replace("%status%", pManager.getBoolean(player, "mention")?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled"))
					.replace("%prefix%", MSG.getString("Command.Toggle.Prefix", "Toggle")));
			break;
		case"notify":
			if(pManager.getInfo(player, "notify")==null) {
				pManager.setInfo(player, "notify", true);
			}
			pManager.setInfo(player, "notify", !pManager.getBoolean(player, "notify"));
			MSG.tell(sender, MSG.getString("Command.Toggle.Notify", "you %status% join notificaitons")
					.replace("%status%", pManager.getBoolean(player, "notify")?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled"))
					.replace("%prefix%", MSG.getString("Command.Toggle.Prefix", "Notify")));
			break;
		case"announcements":
			if(pManager.getInfo(player, "announcements")==null) {
				pManager.setInfo(player, "announcements", true);
			}
			pManager.setInfo(player, "announcements", !pManager.getBoolean(player, "announcements"));
			MSG.tell(sender, MSG.getString("Command.Toggle.Announcements", "you %status% automated announcements")
					.replace("%status%", pManager.getBoolean(player, "announcements")?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled"))
					.replace("%prefix%", MSG.getString("Command.Toggle.Prefix", "Toggle")));
			break;
		default:
			return false;
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		for(String res:new String[] {"Scoreboard","Mention","Notify", "Announcements"}) {
			if(res.toLowerCase().startsWith(args[args.length-1].toLowerCase())) {
				result.add(res);
			}
		}
		return result;
	}
}
