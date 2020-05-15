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

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;

public class NickCommand implements CommandExecutor,TabCompleter {
	public NickCommand() {
		Main.plugin.getCommand("nick").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.nick")) {
			MSG.noPerm(sender);
			return true;
		}
		if (args.length == 0)
			return false;

		Player target = null;
		String name = args[0];

		if (args.length == 1) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
		} else {
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
			name = args[1];
		}

		for (char c : name.toCharArray()) {
			if (!(c+"").matches("[_a-zA-Z0-9]")) {
				MSG.tell(sender, MSG.getString("Command.Nick.Invalid", "invalid name").replace("%prefix%",
						MSG.getString("Command.Nick.Prefix", "Nickname")));
				return true;
			}
		}
		if (name.length()<3||name.length() > 16||Bukkit.getPlayerExact(name)!=null) {
			MSG.tell(sender, MSG.getString("Command.Nick.Invalid", "invalid name").replace("%prefix%",
					MSG.getString("Command.Nick.Prefix", "Nickname")));
			return true;
		}
		
		for(Player t:Bukkit.getOnlinePlayers()) {
			if(t.getDisplayName().equalsIgnoreCase(name)) {
				MSG.tell(sender, MSG.getString("Command.Nick.Invalid", "invalid name").replace("%prefix%",
						MSG.getString("Command.Nick.Prefix", "Nickname")));
				return true;
			}
		}
		
		if(Bukkit.getOfflinePlayer(name).hasPlayedBefore()) {
			MSG.tell(sender, MSG.getString("Command.Nick.Joined", "invalid name").replace("%prefix%",
					MSG.getString("Command.Nick.Prefix", "Nickname")));
			return true;
		}

		if (target != sender && !sender.hasPermission("basic.nick.others")) {
			MSG.noPerm(sender);
			return true;
		}
		INametagApi api = NametagEdit.getApi();
		if (name.equals("off")) {
			if (target == sender) {
				MSG.tell(sender, MSG.getString("Command.Nick.OffSelf", "your nickname is now %name%")
						.replace("%prefix%", MSG.getString("Command.Nick.Prefix", "Nickname")));
			} else {
				MSG.tell(sender,
						MSG.getString("Command.Nick.OffOther", "%player% is now %name%")
								.replace("%player%", target.getName())
								.replace("%prefix%", MSG.getString("Command.Nick.Prefix", "Nickname"))
								.replace("%s%", target.getName().toLowerCase().endsWith("s") ? "" : "s"));
			}
			target.setDisplayName(target.getName());
			api.reloadNametag(target);
		} else {
			if (target == sender) {
				MSG.tell(sender, MSG.getString("Command.Nick.Self", "your nickname is now %name%")
						.replace("%name%", name).replace("%prefix%", MSG.getString("Command.Nick.Prefix", "Nickname")));
			} else {
				MSG.tell(sender,
						MSG.getString("Command.Nick.Other", "%player% is now %name%")
								.replace("%player%", target.getName()).replace("%name%", name)
								.replace("%prefix%", MSG.getString("Command.Nick.Prefix", "Nickname"))
								.replace("%s%", target.getName().toLowerCase().endsWith("s") ? "" : "s"));
			}
			target.setDisplayName(name);
			target.setPlayerListName(MSG.color(pManager.getPrefix(target)+name));
		}
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
