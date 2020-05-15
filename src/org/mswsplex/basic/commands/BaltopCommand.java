package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class BaltopCommand implements CommandExecutor {
	public BaltopCommand() {
		Main.plugin.getCommand("baltop").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.baltop")) {
			MSG.noPerm(sender);
			return true;
		}
		if (Main.plugin.getEcononomy() == null) {
			return true;
		}

		int size = 5;
		if(args.length>0) {
			try {
				size = Integer.parseInt(args[0]);
			}catch(Exception e) {
				MSG.tell(sender, "&cInvalid number");
			}
		}
		Map<String, Double> balances = new HashMap<>();
		for (OfflinePlayer t : Bukkit.getOfflinePlayers()) {
			balances.put(t.getUniqueId().toString(), Main.plugin.getEcononomy().getBalance(t));
		}

		final List<Map.Entry<String, Double>> sorted = new ArrayList<Map.Entry<String, Double>>(balances.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<String, Double>>() {
			public int compare(final Entry<String, Double> entry1, final Entry<String, Double> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});
		MSG.tell(sender, "&7Listing top Balances");
		for (int i = 0; i < size; i++) {
			Entry<String, Double> entry = sorted.get(i);
			OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
			MSG.tell(sender, " &a" + t.getName() + " &7has &2$" +MSG.parseDecimal(entry.getValue()+"", 2));
		}
		return true;
	}

}
