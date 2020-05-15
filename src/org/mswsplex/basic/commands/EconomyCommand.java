package org.mswsplex.basic.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class EconomyCommand implements CommandExecutor {
	public EconomyCommand() {
		Main.plugin.getCommand("economy").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.eco")) {
			MSG.noPerm(sender);
			return true;
		}
		if (Main.plugin.getEcononomy() == null) {
			return true;
		}

		if(args.length<3) {
			MSG.tell(sender, "&c/eco &7[&eset/give/take&7] [&cPlayer&7] [&aAmount&7]");
			return true;
		}
		if(!sender.hasPermission("basic.eco."+args[0])) {
			MSG.noPerm(sender);
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
		double val = 0;
		try {
			val = Double.parseDouble(args[2]);
		}catch(Exception e) {
			MSG.tell(sender, "&cInvalid number");
			return true;
		}
		switch(args[0].toLowerCase()) {
		case"set":
			if(Main.plugin.getEcononomy().getBalance(target)>val) {
				Main.plugin.getEcononomy().withdrawPlayer(target, Main.plugin.getEcononomy().getBalance(target)-val);
			}else {
				Main.plugin.getEcononomy().depositPlayer(target, val-Main.plugin.getEcononomy().getBalance(target));
			}
			MSG.tell(sender, MSG.getString("Command.Economy.SetBalance", "set %player%'%s% balance to %bal%")
					.replace("%player%", target.getName())
					.replace("%bal%", Main.plugin.getEcononomy().getBalance(target)+"")
					.replace("%s%", target.getName().toLowerCase().endsWith("s")?"":"s")
					.replace("%prefix%", MSG.getString("Command.Economy.Prefix", "Economy")));
			break;
		case"give":
			Main.plugin.getEcononomy().depositPlayer(target, val);
			MSG.tell(sender, MSG.getString("Command.Economy.GiveAmount", "gave %player%to %bal%")
					.replace("%player%", target.getName())
					.replace("%bal%", Main.plugin.getEcononomy().getBalance(target)+"")
					.replace("%s%", target.getName().toLowerCase().endsWith("s")?"":"s")
					.replace("%amo%", val+"")
					.replace("%prefix%", MSG.getString("Command.Economy.Prefix", "Economy")));
			break;
		case"take":
			Main.plugin.getEcononomy().withdrawPlayer(target, val);
			MSG.tell(sender, MSG.getString("Command.Economy.TookAmount", "set %player%'%s% balance to %bal%")
					.replace("%player%", target.getName())
					.replace("%bal%", Main.plugin.getEcononomy().getBalance(target)+"")
					.replace("%s%", target.getName().toLowerCase().endsWith("s")?"":"s")
					.replace("%amo%", val+"")
					.replace("%prefix%", MSG.getString("Command.Economy.Prefix", "Economy")));
			break;
		default:
			return false;
		}
		return true;
	}

}
