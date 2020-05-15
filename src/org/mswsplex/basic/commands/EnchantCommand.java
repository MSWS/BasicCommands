package org.mswsplex.basic.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.Utils;
import org.mswsplex.msws.basic.Main;

public class EnchantCommand implements CommandExecutor {
	public EnchantCommand() {
		Main.plugin.getCommand("enchant").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.enchant")) {
			MSG.noPerm(sender);
			return true;
		}

		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}

		Player player = (Player) sender;

		ItemStack hand = player.getItemInHand();

		if (hand == null || hand.getType() == Material.AIR) {
			MSG.tell(sender, MSG.getString("Command.Enchant.InvalidHand", "You must have an item in your hand")
					.replace("%prefix%", MSG.getString("Command.Enchant.Prefix", "")));
			return true;
		}

		int level = 1;
		Enchantment enchant = null;
		enchant = Enchantment.getByName(Utils.getEnchant(args[0]));

		if (args[0].equalsIgnoreCase("clear")) {
			for (Enchantment ench : hand.getEnchantments().keySet())
				hand.removeEnchantment(ench);

			MSG.tell(sender, MSG.getString("Command.Enchant.Clear", "cleared enchants").replace("%prefix%",
					MSG.getString("Command.Enchant.Prefix", "")));
			return true;
		}

		if (enchant == null) {
			MSG.tell(sender, MSG.getString("Command.Enchant.Unknown", "unknown enchantment").replace("%prefix%",
					MSG.getString("Command.Enchant.Prefix", "")));
			return true;
		}

		if (args.length > 1) {
			try {
				level = Integer.parseInt(args[1]);
			} catch (Exception e) {
			}
		}

		if (!enchant.canEnchantItem(hand) && !sender.hasPermission("basic.enchant.all")) {
			MSG.noPerm(sender);
			return true;
		}

		if (level == 0) {
			hand.removeEnchantment(enchant);
			MSG.tell(sender, MSG.getString("Command.Enchant.Remove", "removed enchant").replace("%prefix%",
					MSG.getString("Command.Enchant.Prefix", "")));
			return true;
		}

		try {
			hand.addEnchantment(enchant, level);
		} catch (Exception e) {
			if (!sender.hasPermission("basic.enchant.unsafe")) {
				MSG.noPerm(sender);
				return true;
			}
			hand.addUnsafeEnchantment(enchant, level);
		}

		MSG.tell(sender, MSG.getString("Command.Enchant.Self", "enchanted item").replace("%prefix%",
				MSG.getString("Command.Enchant.Prefix", "")));

		return true;
	}
}
