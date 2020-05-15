package org.mswsplex.basic.commands;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class PingCommand implements CommandExecutor {
	public PingCommand() {
		Main.plugin.getCommand("ping").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		Player player = (Player) sender;
		if (!sender.hasPermission("basic.ping")) {
			MSG.noPerm(sender);
			return true;
		}
		int ping = 0;
		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		String color = "&a";

		if (ping > 1000) {
			color = "&4";
		} else if (ping > 500) {
			color = "&c";
		} else if (ping > 300) {
			color = "&e";
		}
		MSG.tell(sender,
				MSG.getString("Command.Ping.Response", "your ping: %ping%").replace("%ping%", color + ping + "")
						.replace("%prefix%", MSG.getString("Command.Ping.Prefix", "Ping")));
		return true;
	}
}
