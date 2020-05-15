package org.mswsplex.basic.commands;

import java.security.GeneralSecurityException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.TOTP;
import org.mswsplex.msws.basic.Main;

public class TwoFACommand implements CommandExecutor {
	public TwoFACommand() {
		Main.plugin.getCommand("2fa").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.2fa")) {
			MSG.noPerm(sender);
			return true;
		}

		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;
		String key = null;
		if (player != null)
			if (pManager.getInfo(player, "2fakey") != null) {
				try {
					key = TOTP.generateCurrentNumberString(pManager.getString(player, "2fakey")) + "";
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}
			}
		if (args.length > 0) {
			if (player != null) {
				if (args[0].equalsIgnoreCase("reset") && pManager.getInfo(player, "login") == null && args.length==1) {
					pManager.setup2fa(player);
					return true;
				}
				if (args[0].equalsIgnoreCase("logout") && pManager.getInfo(player, "login") == null) {
					pManager.setInfo(player, "login", true);
					return true;
				}
			}
			if (player != null && pManager.getInfo(player, "2fakey") != null
					&& pManager.getInfo(player, "login") != null) {
				if (args.length > 1) {
					if ((args[0] + args[1]).equals(key)) {
						pManager.twoSuccess(player);
						return true;
					} else {
						MSG.tell(player, MSG.getString("Command.2FA.Invalid", "invalid").replace("%prefix%",
								MSG.getString("Command.2FA.Prefix", "2fa")));
						return true;
					}
				} else {
					if (args[0].equals(key)) {
						pManager.twoSuccess(player);
						return true;
					} else {
						MSG.tell(player, MSG.getString("Command.2FA.Invalid", "invalid").replace("%prefix%",
								MSG.getString("Command.2FA.Prefix", "2fa")));
						return true;
					}
				}
			}
			if (args.length > 1 && sender.hasPermission("basic.2fa.others")
					&& (player == null || pManager.getInfo(player, "login") == null)) {
				if (args[0].equalsIgnoreCase("reset")) {
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
					if (pManager.getInfo(target, "2fakey") == null) {
						MSG.tell(sender,
								MSG.getString("Command.2FA.Unknown", "%player% doens't have 2fa")
										.replace("%player%", target.getName())
										.replace("%prefix%", MSG.getString("Command.2FA.Prefix", "2fa")));
						return true;
					} else {
						pManager.removeInfo(target, "2fakey");
						pManager.setInfo(target, "login", true);
						MSG.tell(sender,
								MSG.getString("Command.2FA.Reset", "You reset %player%'%s% 2fa")
										.replace("%player%", target.getName())
										.replace("%s%", target.getName().toLowerCase().endsWith("s") ? "" : "s")
										.replace("%prefix%", MSG.getString("Command.2FA.Prefix", "2fa")));
						if(target.isOnline())
							pManager.setup2fa((Player)target);
						return true;
					}
				}
			}
		}

		if (player != null && pManager.getInfo(player, "2fakey") == null) {
			pManager.setup2fa(player);
		} else {
			return false;
		}

		return true;
	}
}
