package org.mswsplex.basic.commands;

import java.io.File;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.msws.basic.AutoAnnouncer;
import org.mswsplex.msws.basic.Main;

public class RefreshCommand implements CommandExecutor {
	public RefreshCommand() {
		Main.plugin.getCommand("refresh").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("basic.refresh")) {
			MSG.noPerm(sender);
			return true;
		}
		Main.plugin.configYml = new File(Main.plugin.getDataFolder(), "config.yml");
		Main.plugin.config = YamlConfiguration.loadConfiguration(Main.plugin.configYml);
		Main.plugin.langYml = new File(Main.plugin.getDataFolder(), "lang.yml");
		Main.plugin.lang = YamlConfiguration.loadConfiguration(Main.plugin.langYml);
		Main.plugin.guiYml = new File(Main.plugin.getDataFolder(), "guis.yml");
		Main.plugin.gui = YamlConfiguration.loadConfiguration(Main.plugin.guiYml);
		
		boolean save = false;
		for (String section : Main.plugin.config.getConfigurationSection("Scoreboard").getKeys(false)) {
			List<String> lines = Main.plugin.config.getStringList("Scoreboard." + section);
			if (lines == null || lines.isEmpty())
				continue;
			for (int i = 0; i < lines.size(); i++) {
				String tmp = lines.get(i);
				lines.remove(i);
				while (lines.contains(tmp) || tmp.equals("")) {
					tmp = tmp + " ";
					save = true;
				}
				lines.add(i, tmp);
			}
			if (save) {
				Main.plugin.config.set("Scoreboard." + section, lines);
				Main.plugin.saveConfig();
			}
		}
		
		AutoAnnouncer.refresh();
		MSG.tell(sender, MSG.getString("Command.Refresh.Refreshed", "Successfully reloaded.")
				.replace("%prefix%", MSG.getString("Command.Refresh.Prefix", "Refresh")));
		return true;
	}
}
