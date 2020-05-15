package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class DelwarpCommand implements CommandExecutor, TabCompleter {
	public DelwarpCommand() {
		Main.plugin.getCommand("delwarp").setExecutor(this);
		Main.plugin.getCommand("delwarp").setTabCompleter(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}

		if (!sender.hasPermission("basic.delwarp")) {
			MSG.noPerm(sender);
			return true;
		}

		if (args.length == 0)
			return false;

		if (!Main.plugin.data.contains("Warps." + args[0])) {
			MSG.tell(sender, MSG.getString("Command.Delwarp.Unknown", "Warp doesn't exist").replace("%prefix%",
					MSG.getString("Command.Delwarp.Prefix", "Delwarp")));
			return true;
		}
		MSG.tell(sender, MSG.getString("Command.Delwarp.Deleted", "you deleted %warp%").replace("%warp%", args[0])
				.replace("%prefix%", MSG.getString("Command.Delwarp.Prefix", "Delwarp")));
		Main.plugin.data.set("Warps." + args[0], null);
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if (!sender.hasPermission("basic.delwarp"))
			return result;
		ConfigurationSection warps = Main.plugin.data.getConfigurationSection("Warps");
		if (warps == null)
			return result;
		for (String res : warps.getKeys(false)) {
			if (res.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				result.add(res);
		}
		return result;
	}
}
