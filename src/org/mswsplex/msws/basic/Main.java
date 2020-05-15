package org.mswsplex.msws.basic;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.basic.commands.AACommand;
import org.mswsplex.basic.commands.ACommand;
import org.mswsplex.basic.commands.AnnounceCommand;
import org.mswsplex.basic.commands.ApplyCommand;
import org.mswsplex.basic.commands.BalanceCommand;
import org.mswsplex.basic.commands.BaltopCommand;
import org.mswsplex.basic.commands.BuyCommand;
import org.mswsplex.basic.commands.ChatSlowCommand;
import org.mswsplex.basic.commands.ClearCommand;
import org.mswsplex.basic.commands.CountCommand;
import org.mswsplex.basic.commands.DelwarpCommand;
import org.mswsplex.basic.commands.DiscordCommand;
import org.mswsplex.basic.commands.EconomyCommand;
import org.mswsplex.basic.commands.EnchantCommand;
import org.mswsplex.basic.commands.FactionsChatCommand;
import org.mswsplex.basic.commands.FeedCommand;
import org.mswsplex.basic.commands.FillCommand;
import org.mswsplex.basic.commands.FlyCommand;
import org.mswsplex.basic.commands.FreezeCommand;
import org.mswsplex.basic.commands.GamemodeCommand;
import org.mswsplex.basic.commands.GodCommand;
import org.mswsplex.basic.commands.HealCommand;
import org.mswsplex.basic.commands.IPCommand;
import org.mswsplex.basic.commands.InvseeCommand;
import org.mswsplex.basic.commands.KillCommand;
import org.mswsplex.basic.commands.KitCommand;
import org.mswsplex.basic.commands.MOTDCommand;
import org.mswsplex.basic.commands.MessageCommand;
import org.mswsplex.basic.commands.NickCommand;
import org.mswsplex.basic.commands.PingCommand;
import org.mswsplex.basic.commands.PlaytimeCommand;
import org.mswsplex.basic.commands.RTPCommand;
import org.mswsplex.basic.commands.RefreshCommand;
import org.mswsplex.basic.commands.ReplyCommand;
import org.mswsplex.basic.commands.RulesCommand;
import org.mswsplex.basic.commands.SeenCommand;
import org.mswsplex.basic.commands.SetwarpCommand;
import org.mswsplex.basic.commands.ShrugCommand;
import org.mswsplex.basic.commands.SpawnCommand;
import org.mswsplex.basic.commands.StaffCommand;
import org.mswsplex.basic.commands.SummonCommand;
import org.mswsplex.basic.commands.TPACommand;
import org.mswsplex.basic.commands.TableflipCommand;
import org.mswsplex.basic.commands.TeleportCommand;
import org.mswsplex.basic.commands.TimeCommand;
import org.mswsplex.basic.commands.ToggleCommand;
import org.mswsplex.basic.commands.TwoFACommand;
import org.mswsplex.basic.commands.VanishCommand;
import org.mswsplex.basic.commands.WarpCommand;
import org.mswsplex.basic.events.Events;
import org.mswsplex.basic.managers.Protocols;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.SBoard;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	public static Main plugin;

	public FileConfiguration config, data, lang, gui;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml");

	/*
	 * Permissions: basic.[command] basic.[command].bypass basic.[command].others
	 * basic.gamemode.[gamemode] basic.staff - Be listed in /staff
	 * basic.vanish.[rank]
	 * 
	 * 
	 * 
	 */

	private Economy econ = null;

	public static final String ACCOUNT_SID = "AC87a8755ee3aae258fb6a4152d5cc73e3";
	public static final String AUTH_TOKEN = "your_auth_token";

	public void onEnable() {
		plugin = this;
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		if (!guiYml.exists())
			saveResource("guis.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);

		setupEconomy();

		boolean save = false;
		for (String section : config.getConfigurationSection("Scoreboard").getKeys(false)) {
			List<String> lines = config.getStringList("Scoreboard." + section);
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
				config.set("Scoreboard." + section, lines);
				saveConfig();
			}
		}

		registerCommands();

		new Events();

		new SBoard().register();

		if (config.getBoolean("Announcements.Enabled"))
			new AutoAnnouncer().register();

		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			new Protocols();
		// if(Bukkit.getPluginManager().getPlugin("BarAPI")!=null)
		// new BarManager().register();;

		for (Player target : Bukkit.getOnlinePlayers()) {
			target.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}

		MSG.log("&aSuccessfully Enabled!");
	}

	void registerCommands() {
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
		new SummonCommand();
		new HealCommand();
		new GodCommand();
		new EnchantCommand();
		new SeenCommand();
		new CountCommand();
		new ACommand();
		new VanishCommand();
		new ChatSlowCommand();
		new PingCommand();
		new ShrugCommand();
		new KitCommand();
		new MOTDCommand();
		new RTPCommand();
		new SpawnCommand();
		new AnnounceCommand();
		new DiscordCommand();
		new PlaytimeCommand();
		new RulesCommand();
		new NickCommand();
		new ToggleCommand();
		new TableflipCommand();
		new TPACommand();
		new WarpCommand();
		new SetwarpCommand();
		new DelwarpCommand();
		new TwoFACommand();
		new ApplyCommand();
		new FeedCommand();
		new IPCommand();
		new FactionsChatCommand();
		new RefreshCommand();
		new BuyCommand();
		new HelpCommand();
		new AACommand();
		new BaltopCommand();
		new EconomyCommand();
		new BalanceCommand();
		new FillCommand(this);
	}

	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public Economy getEcononomy() {
		return econ;
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
