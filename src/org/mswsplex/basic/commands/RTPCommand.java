package org.mswsplex.basic.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.massivecore.ps.PS;

public class RTPCommand implements CommandExecutor {
	public RTPCommand() {
		Main.plugin.getCommand("rtp").setExecutor(this);
	}

	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.rtp")) {
			MSG.noPerm(sender);
			return true;
		}
		if (!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be player"));
			return true;
		}
		Player player = (Player) sender;

		if (pManager.getInfo(player, "rtp") != null) {
			if (pManager.getDouble(player, "rtp") + Main.plugin.config.getDouble("RTP.Delay") > System
					.currentTimeMillis() && !player.hasPermission("basic.bypass.rtpcooldown")) {
				MSG.tell(sender,
						MSG.getString("Command.RTP.Delay", "command is on cooldown for %time%")
								.replace("%time%", TimeManager.getTime(
										(pManager.getDouble(player, "rtp") + Main.plugin.config.getDouble("RTP.Delay"))
												- System.currentTimeMillis()))
								.replace("%prefix%", MSG.getString("Command.RTP.Prefix", "RTP")));
				return true;
			}
		}
		
		Location loc = new Location(player.getWorld(), 0, 0, 0);
		boolean fine = false;
		int amo = 0, maxAmo = 20;
		while(!fine) {
			loc.setX(player.getLocation().getX() + (Math.random() * Main.plugin.config.getDouble("RTP.Range"))
					- (Math.random() * Main.plugin.config.getDouble("RTP.Range")));
			loc.setZ(player.getLocation().getX() + (Math.random() * Main.plugin.config.getDouble("RTP.Range"))
					- (Math.random() * Main.plugin.config.getDouble("RTP.Range")));
			loc.setY(255);
			if(Bukkit.getServer().getPluginManager().isPluginEnabled("Factions")) {
				if(!BoardColl.get().getFactionAt(PS.valueOf(loc)).getId().equals(Factions.ID_NONE)) {
					fine = false;
					continue;
				}
			}
			for(int i=255;i>0;i--) {
				loc.subtract(0, 1, 0);
				if(loc.getBlock().isLiquid()) {
					fine = false;
					break;
				}
				if(loc.getBlock().getType().isSolid()) {
					loc.add(0, 1, 0);
					fine = true;
					break;
				}
			}
			if(loc.getY()>=250) {
				fine = false;
				continue;	
			}
			amo++;
			if(amo>maxAmo) {
				MSG.tell(player, "error");
				return true;
			}
		}
		MSG.tell(player, MSG.getString("Command.RTP.Teleported", "kit %kit% received").replace("%prefix%", MSG.getString("Command.RTP.Prefix", "RTP"))
				.replace("%x%", loc.getBlockX()+"")
				.replace("%y%", loc.getBlockY()+"")
				.replace("%z%", loc.getBlockZ()+""));
		pManager.setInfo(player, "rtp", System.currentTimeMillis());
		player.teleport(loc);
		Main.plugin.saveData();
		return true;
	}
}
