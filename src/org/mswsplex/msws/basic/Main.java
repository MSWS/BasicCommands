package org.mswsplex.msws.basic;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.basic.commands.ACommand;
import org.mswsplex.basic.commands.ChatSlowCommand;
import org.mswsplex.basic.commands.ClearCommand;
import org.mswsplex.basic.commands.CountCommand;
import org.mswsplex.basic.commands.EnchantCommand;
import org.mswsplex.basic.commands.FlyCommand;
import org.mswsplex.basic.commands.FreezeCommand;
import org.mswsplex.basic.commands.GamemodeCommand;
import org.mswsplex.basic.commands.GodCommand;
import org.mswsplex.basic.commands.HealCommand;
import org.mswsplex.basic.commands.InvseeCommand;
import org.mswsplex.basic.commands.KillCommand;
import org.mswsplex.basic.commands.MessageCommand;
import org.mswsplex.basic.commands.ReplyCommand;
import org.mswsplex.basic.commands.SeenCommand;
import org.mswsplex.basic.commands.SpawnCommand;
import org.mswsplex.basic.commands.StaffCommand;
import org.mswsplex.basic.commands.TeleportCommand;
import org.mswsplex.basic.commands.TimeCommand;
import org.mswsplex.basic.commands.VanishCommand;
import org.mswsplex.basic.events.Events;
import org.mswsplex.basic.utils.MSG;

public class Main extends JavaPlugin {
	public static Main plugin;

	public FileConfiguration config, data, lang, gui;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml");

	/*
	 * Permissions:
	 * basic.[command]
	 * basic.[command].bypass
	 * basic.[command].others
	 * basic.gamemode.[gamemode]
	 * basic.staff - Be listed in /staff
	 * basic.vanish.[rank]
	 * 
	 * 
	 * 
	 */
	
	
	public void onEnable() {
		plugin = this;
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		if (!guiYml.exists())
			saveResource("guis.yml", true);
		saveResource("lang.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);

		new FlyCommand();
		new KillCommand();
		new ClearCommand();
		new TeleportCommand();
		new MessageCommand();
		new ReplyCommand();
		new TimeCommand();
		new FreezeCommand();
		new InvseeCommand();
		new StaffCommand();
		new GamemodeCommand();
		new SpawnCommand();
		new HealCommand();
		new GodCommand();
		new EnchantCommand();
		new SeenCommand();
		new CountCommand();
		new ACommand();
		new VanishCommand();
		new ChatSlowCommand();
		
		new Events();
		MSG.log("&aSuccessfully Enabled!");
	}

	public void onDisable() {
		saveData();
		plugin = null;
	}

	public void saveData() {
		try {
			data.save(dataYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public void saveConfig() {
		try {
			config.save(configYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}
}
