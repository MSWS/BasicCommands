package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class SetwarpCommand implements CommandExecutor {
	public SetwarpCommand() {
		Main.plugin.getCommand("setwarp").setExecutor(this);

	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}

		Player player = (Player) sender;

		if (!sender.hasPermission("basic.setwarp")) {
			MSG.noPerm(sender);
			return true;
		}

		if (args.length == 0)
			return false;

		if (Main.plugin.data.contains("Warps." + args[0])) {
			MSG.tell(sender, MSG.getString("Command.Setwarp.Exists", "Warp exists").replace("%prefix%",
					MSG.getString("Command.Setwarp.Prefix", "Setwarp")));
		}

		Main.plugin.data.set("Warps." + args[0], player.getLocation());
		MSG.tell(sender, MSG.getString("Command.Setwarp.Set", "warp %warp% set").replace("%warp%", args[0])
				.replace("%prefix%", MSG.getString("Command.Setwarp.Prefix", "Setwarp")));
		return true;
	}


}
