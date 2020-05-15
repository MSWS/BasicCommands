package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class SummonCommand implements CommandExecutor, TabCompleter {
	public SummonCommand() {
		Main.plugin.getCommand("summon").setExecutor(this);
		Main.plugin.getCommand("summon").setTabCompleter(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0)
			return false;
		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		Player player = (Player) sender;
		EntityType ent = null;
		int amo = 1;
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("item")) {
				if (args.length > 2) {
					try {
						amo = Integer.parseInt(args[2]);
					} catch (Exception e) {
					}
				}
			} else {
				try {
					amo = Integer.parseInt(args[1]);
				} catch (Exception e) {
				}
			}
		}
		switch (args[0].toLowerCase()) {
		case "item":
			Material type = Material.STONE;
			if (args.length > 1)
				type = Material.valueOf(args[1].toUpperCase());
			player.getWorld().dropItem(player.getLocation(), new ItemStack(type, amo));
			MSG.tell(sender, MSG.getString("Command.Summon.Summon", "You spawned %amo% %type%")
					.replace("%prefix%", MSG.getString("Command.Summon.Prefix", "Summon")).replace("%amo%", amo + "")
					.replace("%type%", MSG.camelCase(type.toString())).replace("%s%", amo == 1 ? "" : "s"));
			return true;
		default:
			try {
				ent = EntityType.valueOf(args[0].toUpperCase());
			} catch (Exception e) {
				try {
					type = Material.valueOf(args[0].toUpperCase());
					player.getWorld().dropItem(player.getLocation(), new ItemStack(type, amo));
					MSG.tell(sender,
							MSG.getString("Command.Summon.Summon", "You spawned %amo% %type%")
									.replace("%prefix%", MSG.getString("Command.Summon.Prefix", "Summon"))
									.replace("%amo%", amo + "").replace("%type%", MSG.camelCase(type.toString()))
									.replace("%s%", amo == 1 ? "" : "s"));
					return true;
				} catch (Exception ee) {
					MSG.tell(sender, MSG.getString("Command.Summon.Unknown", "unknown entity type").replace("%prefix%",
							MSG.getString("Command.Summon.Prefix", "Summon")));
					return true;
				}
			}
			break;
		}
		if (!ent.isSpawnable()) {
			MSG.tell(sender, MSG.getString("Command.Summon.Unknown", "unknown entity type").replace("%prefix%",
					MSG.getString("Command.Summon.Prefix", "Summon")));
			return true;
		}

		int nearby = player.getNearbyEntities(20, 20, 20).size(), max = Main.plugin.config.getInt("MaxSummonLimit");
		if (max != -1) {
			if (amo + nearby > max) {
				amo = max - nearby;
				MSG.tell(sender,
						MSG.getString("Command.Summon.MetMax", "spawning %amo% instead")
								.replace("%prefix%", MSG.getString("Command.Summon.Prefix", "Summon"))
								.replace("%amo%", amo + ""));
			}
		}

		for (int i = 0; i < amo; i++)
			player.getWorld().spawnEntity(player.getLocation(), ent);
		MSG.tell(sender,
				MSG.getString("Command.Summon.Summon", "You spawned %amo% %type%")
						.replace("%prefix%", MSG.getString("Command.Summon.Prefix", "Summon")).replace("%amo%", amo + "")
						.replace("%type%", MSG.camelCase(ent.toString())).replace("%s%", amo == 1 ? "" : "s"));
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if (!sender.hasPermission("basic.spawn"))
			return result;
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("item")) {
				for (Material mat : Material.values())
					if (mat.toString().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(MSG.camelCase(mat.toString().replace("_", ".")).replace(".", "_"));
			}
		}

		for (EntityType type : EntityType.values()) {
			if (type.isSpawnable() && type.toString().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				result.add(MSG.camelCase(type.toString().replace("_", ".")).replace(".", "_"));
		}
		return result;
	}
}
