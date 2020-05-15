package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class ClearCommand implements CommandExecutor, TabCompleter {
	public ClearCommand() {
		Main.plugin.getCommand("clear").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.clear")) {
			MSG.noPerm(sender);
			return true;
		}

		Player target = null;
		Material type = null;
		if (args.length > 1) {
			try {
				type = Material.valueOf(args[1].toUpperCase());
				if(!args[0].equalsIgnoreCase("all")) {
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
					
					if (target == null) {
						MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
						return true;
					}
				}
			} catch (Exception e) {
				MSG.tell(sender, MSG.getString("Command.Clear.Unknown", "unknown material"));
				return true;
			}
		} else if (args.length == 1) {
			try {
				type = Material.valueOf(args[0].toUpperCase());
				if (sender instanceof Player) {
					target = (Player) sender;
				} else {
					MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
					return true;
				}
			} catch (Exception e) {
			}
		}

		if (args.length == 0) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
		} else if (args[0].equalsIgnoreCase("all")) {
			for (Player player : Bukkit.getOnlinePlayers())
				if (type == null) {
					player.getInventory().clear();
					player.getInventory().setArmorContents(new ItemStack[4]);
				} else {
					player.getInventory().remove(type);
					ItemStack armorContents[] = new ItemStack[4];
					int i = 3;
					for (ItemStack armor : player.getInventory().getArmorContents()) {
						if (armor.getType() == type) {
							armorContents[i] = new ItemStack(Material.AIR);
						} else {
							armorContents[i] = armor;
						}
						i--;
					}
					player.getInventory().setArmorContents(armorContents);
				}
			MSG.tell(sender,
					MSG.getString("Command.Clear.Sender", "you cleared %player%'s inventory")
							.replace("%prefix%", MSG.getString("Command.Clear.Prefix", "Inventory Manager"))
							.replace("%player%", "everyone")
							.replace("%s%", "s"));
			return true;
		} else if (type == null) {
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
			MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
			return true;
		}

		if (target != sender && !sender.hasPermission("basic.clear.others")) {
			MSG.noPerm(sender);
			return true;
		}

		if (type == null) {
			target.getInventory().clear();
			target.getInventory().setArmorContents(new ItemStack[4]);
		} else {
			target.getInventory().remove(type);
			ItemStack armorContents[] = new ItemStack[4];
			int i = 3;
			for (ItemStack armor : target.getInventory().getArmorContents()) {
				if (armor.getType() == type) {
					armorContents[i] = new ItemStack(Material.AIR);
				} else {
					armorContents[i] = armor;
				}
				i--;
			}
			target.getInventory().setArmorContents(armorContents);
		}

		if (target != sender) {
			MSG.tell(sender,
					MSG.getString("Command.Clear.Sender", "you cleared %player%'s inventory")
							.replace("%prefix%", MSG.getString("Command.Clear.Prefix", "Inventory Manager"))
							.replace("%player%", target.getDisplayName())
							.replace("%s%", target.getDisplayName().toLowerCase().endsWith("s") ? "" : "s"));
			MSG.tell(target,
					MSG.getString("Command.Clear.Receiver", "%sender% cleared your inventory")
							.replace("%prefix%", MSG.getString("Command.Clear.Prefix", "Inventory Manager"))
							.replace("%sender%", (sender instanceof Player)?((Player)sender).getDisplayName():sender.getName()));
		} else {
			MSG.tell(sender, MSG.getString("Command.Clear.Self", "you cleared your inventory").replace("%prefix%",
					MSG.getString("Command.Clear.Prefix", "Inventory Manager")));
		}
		return true;
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		for (Player t : Bukkit.getOnlinePlayers())
			if (t.getDisplayName().toLowerCase().startsWith(args[0].toLowerCase()))
				result.add(t.getDisplayName());
		return result;
	}
}
