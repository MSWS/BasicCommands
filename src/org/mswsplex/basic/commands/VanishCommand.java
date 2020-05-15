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

public class VanishCommand implements CommandExecutor {
	public VanishCommand() {
		Main.plugin.getCommand("vanish").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.vanish")) {
			MSG.noPerm(sender);
			return true;
		}
		Player player = null;
		if(args.length>0) {
			List<Player> results = Bukkit.matchPlayer(args[0]);
			if (results.size() == 1) {
				player = results.get(0);
			}

			for (Player t : Bukkit.getOnlinePlayers()) {
				if (!t.getDisplayName().equals(t.getName())) {
					if (args[0].equals(t.getDisplayName())) {
						player = t;
						break;
					}
				}
			}
		}else if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
		}else {
			player = (Player) sender;
		}
		
		if(player!=null&&player!=sender&&!sender.hasPermission("basic.vanish.others")) {
			MSG.noPerm(sender);
			return true;
		}
		
		if(sender!=player&&player.hasPermission("basic.vanish.bypass")) {
			MSG.noPerm(sender);
			pManager.setInfo(player, "vanished", !pManager.isVanished(player));
			return true;
		}
		
		pManager.setInfo(player, "vanished", !pManager.isVanished(player));
		
		if(sender==player) {
			MSG.tell(sender, MSG.getString("Command.Vanish.Self", "%prefix% %status%")
					.replace("%prefix%", MSG.getString("Command.Vanish.Prefix", "Vanish"))
					.replace("%status%", pManager.isVanished(player)?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled")));
		}else {
			MSG.tell(sender, MSG.getString("Command.Vanish.Sender", "%prefix% You %status% &e%player%&7''%s% vanish.")
					.replace("%prefix%", MSG.getString("Command.Vanish.Prefix", "Vanish"))
					.replace("%status%", pManager.isVanished(player)?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled"))
					.replace("%player%", player.getDisplayName())
					.replace("%s%", player.getDisplayName().toLowerCase().endsWith("s")?"":"s"));
			MSG.tell(player, MSG.getString("Command.Vanish.Receiver", "%prefix% &e%sender%&7 %status% &7your vanish.")
					.replace("%prefix%", MSG.getString("Command.Vanish.Prefix", "Vanish"))
					.replace("%status%", pManager.isVanished(player)?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled"))
					.replace("%sender%", (sender instanceof Player)?((Player)sender).getDisplayName():sender.getName()));
		}
		int pRank = pManager.getVanishRank(player);
		if(pManager.isVanished(player)) {
			for(Player target:Bukkit.getOnlinePlayers()) {
				if(target==player)
					continue;
				if(pRank>pManager.getVanishRank(target))
					target.hidePlayer(player);
			}
		}else {
			for(Player target:Bukkit.getOnlinePlayers()) {
				target.showPlayer(player);
			}
		}
		return true;
	}
}
