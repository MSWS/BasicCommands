package org.mswsplex.basic.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class BalanceCommand implements CommandExecutor {
	public BalanceCommand() {
		Main.plugin.getCommand("bal").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.balance")) {
			MSG.noPerm(sender);
			return true;
		}
		if (Main.plugin.getEcononomy() == null) {
			return true;
		}
		OfflinePlayer target = null;

		if (!(sender instanceof Player) && args.length == 0) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player!"));
			return true;
		} else if (sender instanceof Player) {
			target = (Player) sender;
		}

		if (args.length > 0 && sender.hasPermission("basic.balance.others")) {
			target = Bukkit.getOfflinePlayer(args[0]);
		}
		if (target == sender) {
			MSG.tell(sender,
					MSG.getString("Command.Balance.Self", "you have %bal%")
							.replace("%bal%", MSG.parseDecimal(Main.plugin.getEcononomy().getBalance(target) + "", 2))
							.replace("%prefix%", MSG.getString("Command.Balance.Prefix", "Balance")));
		} else {
			MSG.tell(sender,
					MSG.getString("Command.Balance.Other", "%player% has %bal%")
							.replace("%bal%", MSG.parseDecimal(Main.plugin.getEcononomy().getBalance(target) + "", 2))
							.replace("%player%", target.getName())
							.replace("%prefix%", MSG.getString("Command.Balance.Prefix", "Balance")));
		}
		return true;
	}

}
