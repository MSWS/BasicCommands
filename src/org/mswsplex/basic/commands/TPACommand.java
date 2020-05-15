package org.mswsplex.basic.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

public class TPACommand implements CommandExecutor,TabCompleter {
	public TPACommand() {
		Main.plugin.getCommand("tpa").setExecutor(this);
		Main.plugin.getCommand("tpaccept").setExecutor(this);
		Main.plugin.getCommand("tpdeny").setExecutor(this);

	}

	PlayerManager pManager = new PlayerManager();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		
		Player player = (Player) sender, tpme = player;

		switch(command.getName().toLowerCase()) {
		case"tpa":
			if (!sender.hasPermission("basic.tpa")) {
				MSG.noPerm(sender);
				return true;
			}

			if(args.length==0)
				return false;
			
			if(pManager.getInfo(tpme, "lastHit")!=null) {
				if(pManager.getDouble(tpme, "lastHit")+10000>System.currentTimeMillis()) {
					MSG.tell(sender, MSG.getString("Command.TPA.InCombat", "in combat for %time%")
							.replace("%time%", TimeManager.getTime(pManager.getDouble(tpme, "lastHit")+10000 - System.currentTimeMillis()))
							.replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")));
					return true;
				}
			}
			
			if(args.length==1) {
				Player tpto = Bukkit.getPlayer(args[0]);
				for (Player t : Bukkit.getOnlinePlayers()) {
					if (args[0].equals(t.getDisplayName())) {
						tpto = t;
						break;
					}
				}
				if (tpto == null) {
					MSG.tell(sender, MSG.getString("Unknown.Player", "Unknown player"));
				} else {
					MSG.tell(tpme, MSG.getString("Command.TPA.Sent", "you requested to tp to %target%").replace("%target%", tpto.getDisplayName())
							.replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")));
					MSG.tell(tpto, MSG.getString("Command.TPA.Received", "%sender% has requested to tp to you")
							.replace("%sender%", tpme.getDisplayName()).replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")));
					pManager.setInfo(tpto, "request", tpme.getUniqueId()+"");
				}
			}
			break;
		case"tpaccept":
			if (!sender.hasPermission("basic.tpaccept")) {
				MSG.noPerm(sender);
				return true;
			}
			if(pManager.getInfo(player, "request")==null) {
				MSG.tell(sender, MSG.getString("Command.TPA.NoRequest", "you do not have a pending tp request"));
				return true;
			}
			tpme = Bukkit.getPlayer(UUID.fromString(pManager.getString(player, "request")));
			if(!tpme.isOnline()) {
				MSG.tell(sender, MSG.getString("Command.TPA.Offline", "they're offline now").replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")));
				pManager.setInfo(player, "request", null);
				return true;
			}
			MSG.tell(tpme, MSG.getString("Command.TPA.Accepted", "'%prefix% &e%target%&7 has accepted your teleport request.").replace("%target%", player.getDisplayName()).replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")));
			MSG.tell(player, MSG.getString("Command.TPA.Accepted-Self", "%prefix% You accepted &e%sender%&7''%s% teleport request&7.").replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")).replace("%sender%", tpme.getDisplayName())
					.replace("%s%", tpme.getDisplayName().toLowerCase().endsWith("s")?"":"s"));
			tpme.teleport(player);
			pManager.setInfo(player, "request", null);
			break;
		case"tpdeny":
			if (!sender.hasPermission("basic.tpdeny")) {
				MSG.noPerm(sender);
				return true;
			}
			if(pManager.getInfo(player, "request")==null) {
				MSG.tell(sender, MSG.getString("Command.TPA.NoRequest", "you do not have a pending tp request"));
				return true;
			}
			tpme = Bukkit.getPlayer(UUID.fromString(pManager.getString(player, "request")));
			if(!tpme.isOnline()) {
				MSG.tell(sender, MSG.getString("Command.TPA.Offline", "they're offline now").replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")));
				pManager.setInfo(player, "request", null);
				return true;
			}
			MSG.tell(tpme, MSG.getString("Command.TPA.Denied", "'%prefix% &e%target%&7 has denied your teleport request.").replace("%target%", player.getDisplayName()).replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")));
			MSG.tell(player, MSG.getString("Command.TPA.Denied-Self", "%prefix% You denied &e%sender%&7''%s% teleport request&7.").replace("%prefix%", MSG.getString("Command.TPA.Prefix", "Teleport")).replace("%sender%", tpme.getDisplayName())
					.replace("%s%", tpme.getDisplayName().toLowerCase().endsWith("s")?"":"s"));
			pManager.setInfo(player, "request", null);
			break;
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if (!sender.hasPermission("basic.tpa"))
			return result;
		for (Player t : Bukkit.getOnlinePlayers())
			if (t.getDisplayName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				result.add(t.getDisplayName());
		return result;
	}
}
