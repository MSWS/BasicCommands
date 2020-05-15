package org.mswsplex.basic.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class SpawnCommand implements CommandExecutor {
	public SpawnCommand() {
		Main.plugin.getCommand("spawn").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("basic.spawn")) {
			MSG.noPerm(sender);
			return true;
		}
		if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		Player player = (Player) sender;

		if(pManager.getInfo(player, "lastHit")!=null) {
			if(pManager.getDouble(player, "lastHit")+10000>System.currentTimeMillis()) {
				MSG.tell(sender, MSG.getString("Command.Spawn.InCombat", "in combat for %time%")
						.replace("%time%", TimeManager.getTime(pManager.getDouble(player, "lastHit")+10000 - System.currentTimeMillis()))
						.replace("%prefix%", MSG.getString("Command.Spawn.Prefix", "Spawn")));
				return true;
			}
		}
		player.teleport(player.getWorld().getSpawnLocation());
		return true;
	}
}
