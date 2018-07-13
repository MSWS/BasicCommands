package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.StaffGUI;
import org.mswsplex.msws.basic.Main;

public class StaffCommand implements CommandExecutor {
	public StaffCommand() {
		Main.plugin.getCommand("staff").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.staff")) {
			MSG.noPerm(sender);
			return true;
		}

		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		
		if(args.length==1) {
			List<String> staff = Main.plugin.data.getStringList("Staff");
			staff.add(Bukkit.getOfflinePlayer(args[0]).getUniqueId()+"");
			Main.plugin.data.set("Staff", staff);
			Main.plugin.saveData();
			return true;
		}

		Player player = (Player) sender;

		pManager.setInfo(player, "page", 0);
		pManager.setInfo(player, "openStaff", true);
		player.openInventory(StaffGUI.getStaffInventory(0));
		return true;
	}
}
