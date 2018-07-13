package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class FreezeCommand implements CommandExecutor {
	public FreezeCommand() {
		Main.plugin.getCommand("freeze").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.freeze")) {
			MSG.noPerm(sender);
			return true;
		}

		if (args.length < 1) {
			return false;
		}

		Player target;

		List<Player> results = Bukkit.matchPlayer(args[0]);
		if (results.size() == 1) {
			target = results.get(0);
		} else if (results.size() == 0) {
			MSG.tell(sender, MSG.getString("Unkown.Player", "Unknown player"));
			return true;
		} else {
			MSG.tell(sender, MSG.getString("Unknown.ListPlayer", "%size% possible results").replace("%size%",
					results.size() + ""));
			return true;
		}

		if (target.hasPermission("basic.freeze.bypass")) {
			MSG.tell(sender, MSG.getString("Command.Freeze.Unable", "unable to freeze").replace("%prefix%",
					MSG.getString("Command.Freeze.Prefix", "Freeze")));
			return true;
		}

		pManager.setInfo(target, "frozen", !pManager.isFrozen(target));

		if (sender != target) {
			MSG.tell(sender,
					MSG.getString("Command.Freeze.Sender", "%prefix% %player% %status%")
							.replace("%prefix%", MSG.getString("Command.Freeze.Prefix", "Freeze"))
							.replace("%player%", target.getName())
							.replace("%status%",
									pManager.getBoolean(target, "frozen") ? MSG.getString("Command.Enable", "enabled")
											: MSG.getString("Command.Disable", "disabled"))
							.replace("%s%", target.getName().toLowerCase().endsWith("s") ? "" : "s"));
			MSG.tell(target,
					MSG.getString("Command.Freeze.Receiver", "%prefix% %sender% %status%")
							.replace("%prefix%", MSG.getString("Command.Freeze.Prefix", "Freeze"))
							.replace("%sender%", sender.getName()).replace("%status%",
									pManager.getBoolean(target, "frozen") ? MSG.getString("Command.Enable", "enabled")
											: MSG.getString("Command.Disable", "disabled")));
		} else {
			MSG.tell(sender,
					MSG.getString("Command.Freeze.Self", "%prefix% %sender% %status%")
							.replace("%prefix%", MSG.getString("Command.Freeze.Prefix", "Freeze")).replace("%status%",
									pManager.getBoolean(target, "frozen") ? MSG.getString("Command.Enable", "enabled")
											: MSG.getString("Command.Disable", "disabled")));
		}
		if (pManager.isFrozen(target))
			Main.plugin.lang.getStringList("Command.Freeze.Message").forEach((line) -> {
				MSG.tell(sender, line);
			});
		return true;
	}
}
