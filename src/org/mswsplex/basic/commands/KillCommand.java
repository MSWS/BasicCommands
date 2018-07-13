package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class KillCommand implements CommandExecutor {
	public KillCommand() {
		Main.plugin.getCommand("kill").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player target = null;
		if (!sender.hasPermission("basic.kill")) {
			MSG.noPerm(sender);
			return true;
		}
		if (args.length == 0) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
		} else if (!sender.hasPermission("basic.kill.others")) {
			MSG.noPerm(sender);
			return true;
		} else {
			switch (args[0].toLowerCase()) {
			case "entities":
				if (!sender.hasPermission("basic.kill.entities")) {
					MSG.noPerm(sender);
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
					return true;
				}
				for (Entity ent : ((Player) sender).getWorld().getEntities())
					if (!(ent instanceof LivingEntity)) {
						ent.remove();
					}
				MSG.tell(sender, MSG.getString("Command.Kill.Misc", "You killed all %type%")
						.replace("%type%", "entities")
						.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager")));
				return true;
			case "players":
				if (!sender.hasPermission("basic.kill.players")) {
					MSG.noPerm(sender);
					return true;
				}
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.setHealth(0);
					MSG.tell(player, MSG.getString("Command.Kill.Receiver", "%prefix% %sender% killed you")
							.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager"))
							.replace("%sender%", sender.getName()));
				}
				MSG.tell(sender, MSG.getString("Command.Kill.Misc", "You killed all %type%")
						.replace("%type%", "players")
						.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager")));
				return true;
			case "items":
				if (!sender.hasPermission("basic.kill.items")) {
					MSG.noPerm(sender);
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
					return true;
				}
				for (Entity ent : ((Player) sender).getWorld().getEntities())
					if (ent instanceof Item)
						ent.remove();

				MSG.tell(sender, MSG.getString("Command.Kill.Misc", "You killed all %type%")
						.replace("%type%", "items")
						.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager")));
				return true;
			case "mobs":
				if (!sender.hasPermission("basic.kill.mobs")) {
					MSG.noPerm(sender);
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
					return true;
				}
				for (Entity ent : ((Player) sender).getWorld().getEntities())
					if (ent instanceof LivingEntity && !(ent instanceof Player))
						((LivingEntity) ent).setHealth(0);
				MSG.tell(sender, MSG.getString("Command.Kill.Misc", "You killed all %type%")
						.replace("%type%", "mobs")
						.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager")));
				return true;
			default:
				try {
					EntityType type = EntityType.valueOf(args[0].toUpperCase());
					if (!sender.hasPermission("basic.kill.entitytype")) {
						MSG.noPerm(sender);
						return true;
					}
					if (!(sender instanceof Player)) {
						MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
						return true;
					}
					for (Entity ent : ((Player) sender).getWorld().getEntities())
						if (ent.getType() == type)
							if (ent instanceof LivingEntity && !(ent instanceof Player))
								((LivingEntity) ent).setHealth(0);
					MSG.tell(sender, MSG.getString("Command.Kill.Misc", "You killed all %type%")
							.replace("%type%", MSG.camelCase(type.name())+(type.name().toLowerCase().endsWith("s")?"":"s"))
							.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager")));
					return true;
				} catch (Exception e) {
				}
				break;
			}
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
		}

		if (target == null) {
			MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown Player"));
			return true;
		}

		if (target != sender && !sender.hasPermission("basic.kill.others")) {
			MSG.noPerm(sender);
			return true;
		}

		if (sender != target) {
			MSG.tell(sender,
					MSG.getString("Command.Kill.Sender", "%prefix% %sender% killed you")
							.replace("%sender%", sender.getName())
							.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager")));
			MSG.tell(sender,
					MSG.getString("Command.Kill.Receiver", "%prefix% you killed %player%%s%")
							.replace("%prefix%", MSG.getString("Command.Kill.Prefix", "Player Manager"))
							.replace("%player%", target.getName())
							.replace("%s%", target.getName().toLowerCase().endsWith("s") ? "" : "s"));
		} else {
			MSG.tell(sender, MSG.getString("Command.Kill.Self", "You killed yourself").replace("%prefix%",
					MSG.getString("Command.Kill.Prefix", "Player Manager")));
		}

		target.setHealth(0);

		return true;
	}
}
