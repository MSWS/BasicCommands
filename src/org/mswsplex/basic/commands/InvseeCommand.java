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
		Player target, player = (Player) sender;

		List<Player> results = Bukkit.matchPlayer(args[0]);
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

		Inventory inv = Bukkit.createInventory(target, 45,
				target.getName() + "'" + (target.getName().toLowerCase().endsWith("s") ? "" : "s") + " Inventory");
		int slot = -1;
		for (ItemStack item : target.getInventory().getContents()) {
			slot++;
			if (item == null)
				continue;
			inv.setItem(slot, item);
		}

		slot = 4;
		for (ItemStack item : target.getInventory().getArmorContents()) {
			slot--;
			if (item == null)
				continue;
			inv.setItem(inv.getSize() - 9 + slot, item);
		}
		pManager.setInfo(player, "openInventory", target.getUniqueId() + "");
		player.openInventory(inv);
		return true;
	}
}
