package org.mswsplex.basic.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.Main;

import com.massivecraft.factions.entity.MPlayer;

public class FactionsChatCommand implements CommandExecutor {
	public FactionsChatCommand() {
		Main.plugin.getCommand("fc").setExecutor(this);
	}
	
	PlayerManager pManager = new PlayerManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.fchat")) {
			MSG.noPerm(sender);
			return true;
		}

		if(!Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			MSG.tell(sender, MSG.getString("Command.FC.NotEnabled", "no faction").replace("%prefix%", MSG.getString("Command.FC.Prefix", "FC")));
			return true;		}

		if(!(sender instanceof Player)) {
			MSG.tell(sender, MSG.getString("MustBePlayer", "You must be a player"));
			return true;
		}
		Player player = (Player) sender;
		MPlayer mp = MPlayer.get((Player) sender);
		if(!mp.hasFaction()) {
			MSG.tell(sender, MSG.getString("Command.FC.NoFaction", "no faction").replace("%prefix%", MSG.getString("Command.FC.Prefix", "FC")));
			return true;
		}
		if(args.length==0) {
			boolean chat = !pManager.getBoolean(player, "fchat");
			MSG.tell(sender, MSG.getString("Command.FC.Toggled", "you %status% faction chat")
					.replace("%status%", chat?MSG.getString("Command.Enable", "enabled"):MSG.getString("Command.Disable", "disabled")));
			pManager.setInfo(player, "fchat", chat);
			return true;
		}
		String msg = "";
		for(String res:args)
			msg = msg + res +" ";
		msg = msg.trim();
		for(Player target:mp.getFaction().getOnlinePlayers()) {
			MSG.tell(target, MSG.getString("Command.FC.Format", "%faction% %faction_role% %prefix%%player% %message%")
					.replace("%faction%", mp.getFactionName())
					.replace("%faction_role%", mp.getRole().getName())
					.replace("%prefix%", pManager.getPrefix((Player)sender))
					.replace("%player%", ((Player)sender).getDisplayName())
					.replace("%message%", msg));
		}
		return true;
	}
}
