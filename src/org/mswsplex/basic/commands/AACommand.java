package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class AACommand implements CommandExecutor, TabCompleter {
	public AACommand() {
		Main.plugin.getCommand("aa").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String msg;
		if (!sender.hasPermission("basic.aa")) {
			MSG.noPerm(sender);
			return true;
		}
		if (args.length == 0)
			return false;
		msg = "";
		for (String res : args)
			msg = msg + res + " ";
		msg = msg.trim();
		for (Player target : Bukkit.getOnlinePlayers()) {
			if (target == sender || target.hasPermission("basic.aa.receive")) {
				MSG.tell(target, MSG.getString("Command.AA.Regular", "%prefix% %player-prefix%%player% %message%")
						.replace("%prefix%", MSG.getString("Command.AA.Prefix", "Advanced"))
						.replace("%player-prefix%",
								(sender instanceof Player) ? (pManager.getPrefix((Player) sender)) : (""))
						.replace("%player%",
								((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName()))
						.replace("%message%", msg));
				target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 1);
			}
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
