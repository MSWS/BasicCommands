package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class GamemodeCommand implements CommandExecutor, TabCompleter {
	public GamemodeCommand() {
		Main.plugin.getCommand("gamemode").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0)
			return false;
		GameMode gm = null;
		switch (args[0].toLowerCase()) {
		case "creative":
		case "c":
		case "1":
			gm = GameMode.CREATIVE;
			break;
		case "survival":
		case "s":
		case "0":
			gm = GameMode.SURVIVAL;
			break;
		case "adventure":
		case "a":
		case "2":
			gm = GameMode.ADVENTURE;
			break;
		case "spectator":
		case "sp":
		case "3":
			gm = GameMode.SPECTATOR;
			break;
		default:
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
			MSG.tell(sender,
					MSG.getString("Command.Gamemode.Check", "%prefix% %player%'%s% mode is %mode%")
							.replace("%prefix%", MSG.getString("Command.Gamemode.Prefix", "Gamemode"))
							.replace("%player%", target.getDisplayName())
							.replace("%s%", target.getDisplayName().toLowerCase().endsWith("s") ? "" : "s")
							.replace("%mode%", MSG.camelCase(target.getGameMode().toString())));
			return true;
		}
		Player target = null;
		if (args.length > 1) {
			List<Player> results = Bukkit.matchPlayer(args[1]);
			if (results.size() == 1) {
				target = results.get(0);
			} else if (results.size() == 0) {
				MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
				return true;
			} else {
				MSG.tell(sender, MSG.getString("Unknown.ListPlayer", "%size% possible results").replace("%size%",
						results.size() + ""));
				return true;
			}
		} else if (sender instanceof Player) {
			target = (Player) sender;
		} else {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
		}
		
		if(!sender.hasPermission("basic.gamemode."+gm)) {
			MSG.noPerm(sender);
			return true;
		}
		
		if(target!=sender&&!sender.hasPermission("basic.gamemode.others")) {
			MSG.noPerm(sender);
			return true;
		}
		
		if (target == sender) {
			MSG.tell(sender,
					MSG.getString("Command.Gamemode.Self", "you set your gamemode to %mode%")
							.replace("%prefix%", MSG.getString("Command.Gamemode.Prefix", "Gamemode"))
							.replace("%mode%", MSG.camelCase(gm.toString())));
		} else {
			MSG.tell(sender,
					MSG.getString("Command.Gamemode.Sender", "you set %player%'%s% gamemode to %mode%")
							.replace("%prefix%", MSG.getString("Command.Gamemode.Prefix", "Gamemode"))
							.replace("%mode%", MSG.camelCase(gm.toString())).replace("%player%", target.getDisplayName())
							.replace("%s%", target.getDisplayName().toLowerCase().endsWith("s") ? "" : "s"));
		
			MSG.tell(target,
					MSG.getString("Command.Gamemode.Receiver", "%sender% set your gamemode to %mode%")
							.replace("%prefix%", MSG.getString("Command.Gamemode.Prefix", "Gamemode"))
							.replace("%mode%", MSG.camelCase(gm.toString())).replace("%player%", target.getDisplayName())
							.replace("%sender%", (sender instanceof Player)?((Player)sender).getDisplayName():sender.getName()));
		
		}

		target.setGameMode(gm);

		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if(!sender.hasPermission("basic.gamemode")) {
			
		}
		return result;
	}
}
