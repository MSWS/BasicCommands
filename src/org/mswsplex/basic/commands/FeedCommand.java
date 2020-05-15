package org.mswsplex.basic.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class FeedCommand implements CommandExecutor {
	public FeedCommand() {
		Main.plugin.getCommand("feed").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.feed")) {
			MSG.noPerm(sender);
			return true;
		}

		Player target = null;

		if (args.length == 0) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
				return true;
			}
		} else if (args[0].equalsIgnoreCase("all")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.setSaturation(2);
				player.setFoodLevel(20);
			}
			MSG.tell(sender, MSG.getString("Command.Feed.Sender", "you fed %player%")
					.replace("%prefix%", MSG.getString("Command.Feed.Prefix", "Feed")).replace("%player%", "everyone"));
			return true;
		} else {
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
		}

		if (target == null) {
			MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
			return true;
		}

		if (target != sender && !sender.hasPermission("basic.feed.others")) {
			MSG.noPerm(sender);
			return true;
		}

		target.setHealth(20);
		target.setSaturation(2);
		target.setFoodLevel(20);
		target.setFireTicks(0);
		for (Player t : target.getWorld().getPlayers())
			t.showPlayer(target);
		target.setFallDistance(0);
		target.setWalkSpeed(.2f);
		for (PotionEffect effect : target.getActivePotionEffects())
			target.removePotionEffect(effect.getType());
		if (target != sender) {
			MSG.tell(sender,
					MSG.getString("Command.Feed.Sender", "you fed %player%")
							.replace("%prefix%", MSG.getString("Command.Feed.Prefix", "Feed"))
							.replace("%player%", target.getDisplayName()));
			MSG.tell(target, MSG.getString("Command.Feed.Receiver", "%sender% fed you")
					.replace("%prefix%", MSG.getString("Command.Feed.Prefix", "Feed")).replace("%sender%",
							(sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName()));
		} else {
			MSG.tell(sender, MSG.getString("Command.Feed.Self", "you cleared your inventory").replace("%prefix%",
					MSG.getString("Command.Feed.Prefix", "Heal")));
		}
		return true;
	}
}
