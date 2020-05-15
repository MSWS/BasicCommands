package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class InvseeCommand implements CommandExecutor {
	public InvseeCommand() {
		Main.plugin.getCommand("invsee").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.invsee")) {
			MSG.noPerm(sender);
			return true;
		}

		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}

		if (args.length == 0)
			return false;
		Player target = null, player = (Player) sender;

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

		if (target.hasPermission("basic.invsee.bypass")) {
			MSG.tell(sender, MSG.getString("Command.Invsee.Unable", "can't invsee that player").replace("%prefix%",
					MSG.getString("Command.Invsee.Prefix", "Invsee")));
			return true;
		}

		if (target == sender) {
			MSG.tell(sender, MSG.getString("Command.Invsee.Self", "nurp").replace("%prefix%",
					MSG.getString("Command.Invsee.Prefix", "Invsee")));
			return true;
		}

		Inventory inv = Bukkit.createInventory(target, 36,
				target.getDisplayName() + "'" + (target.getDisplayName().toLowerCase().endsWith("s") ? "" : "s") + " Inventory");
		int slot = -1;
		for (ItemStack item : target.getInventory().getContents()) {
			slot++;
			if (item == null)
				continue;
			inv.setItem(slot, item);
		}
		pManager.setInfo(player, "openInventory", target.getUniqueId() + "");
		player.openInventory(inv);
		return true;
	}
}
