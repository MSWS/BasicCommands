package org.mswsplex.msws.basic;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.utils.MSG;

public class AutoAnnouncer {
	static List<String> messages;
	int lastSent = -1;
	
	PlayerManager pManager = new PlayerManager();
	
	public static void refresh() {
		messages = Main.plugin.config.getStringList("Announcements.Messages");
	}
	
	public void register() {
		refresh();
		new BukkitRunnable() {
			public void run() {
				String msg = "";
				if (Main.plugin.config.getBoolean("Announcements.Randomize")) {
					int u;
					do {
						u = (int) Math.floor(Math.random() * messages.size());
					} while (u == lastSent||messages.size()==1);
					lastSent = u;
					msg = messages.get(u);
				} else {
					lastSent = (lastSent + 1) % messages.size();
					msg = messages.get(lastSent);
				}
				for(Player target:Bukkit.getOnlinePlayers()) {
					if(pManager.getInfo(target, "announcements")!=null&&!pManager.getBoolean(target, "announcements"))
						continue;
					for(String res:Main.plugin.config.getStringList("Announcements.Format")) {
						MSG.tell(target, MSG.parse(target, res.replace("%message%", msg)));
					}
				}
			}
		}.runTaskTimer(Main.plugin, 0, (long) (Main.plugin.config.getDouble("Announcements.Rate")));
	}
}
