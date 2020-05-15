package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class IPCommand implements CommandExecutor {
	public IPCommand() {
		Main.plugin.getCommand("ip").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.ip")) {
			MSG.noPerm(sender);
			return true;
		}

		OfflinePlayer target = null;

		if (args.length == 0||(!sender.hasPermission("basic.ip.others"))) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
		} else {
			target = Bukkit.getOfflinePlayer(args[0]);
		}

		if (!target.hasPlayedBefore()&&!target.isOnline()) {
			MSG.tell(sender, MSG.getString("Command.IP.NotJoined", "%player% hasn't joined").replace("%player%",
					target.getName()));
			return true;
		}
		List<String> ips = new ArrayList<String>();
		List<String> alts = new ArrayList<String>();

		for (String res : Main.plugin.data.getStringList("IPs." + target.getUniqueId())) {
			if (!ips.contains(res))
				ips.add(res);

			for (String altip : Main.plugin.data.getStringList("IPs." + res.replace(".", ","))) {
				String name = Bukkit.getOfflinePlayer(UUID.fromString(altip)).getName();
				if (!alts.contains(name)) {
					alts.add(name);
				}
			}
		}
		
		String ipString = "&a", altString = "&e";
		for (String ip : ips)
			ipString = ipString + ip + "&7, &a";
		for (String alt : alts)
			altString = altString + alt + "&7, &e";
		ipString = ipString.substring(0, Math.max(0, ipString.length()-4));
		altString = altString.substring(0, Math.max(0, altString.length()-4));
		for (String res : Main.plugin.lang.getStringList("Command.IP.Format")) {
			MSG.tell(sender, res.replace("%player%", target.getName()).replace("%last%", pManager.getString(target, "lastIP"))
					.replace("%alts%", altString)
					.replace("%ips%", ipString)
					.replace("%altamo%", alts.size()+"")
					.replace("%ipamo%", ips.size()+""));
		}
		return true;
	}
}
