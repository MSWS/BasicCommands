package org.mswsplex.basic.events;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.StaffGUI;
import org.mswsplex.basic.utils.TOTP;
import org.mswsplex.msws.basic.Main;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Events implements Listener {
	public Events() {
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}

	PlayerManager pManager = new PlayerManager();

	@EventHandler
	public void onInventorySwap(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "2fakey") != null && pManager.getInfo(player, "login") != null) {
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("static-access")
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "2fakey") != null && pManager.getInfo(player, "login") != null) {
			try {
				String key = TOTP.generateCurrentNumberString(pManager.getString(player, "2fakey")) + "";
				if (event.getMessage().equals(key) || event.getMessage().replace(" ", "").equals(key)) {
					MSG.tell(player, MSG.getString("Command.2FA.Success", "success").replace("%prefix%",
							MSG.getString("Command.2FA.Prefix", "2fa")));
					pManager.removeInfo(player, "login");
					pManager.setInfo(player, "setup", null);
					pManager.setInfo(player, "lastVerify", System.currentTimeMillis());
					player.setItemInHand(new ItemStack(Material.AIR));
				} else {
					MSG.tell(player, MSG.getString("Command.2FA.Invalid", "invalid").replace("%prefix%",
							MSG.getString("Command.2FA.Prefix", "2fa")));
				}
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			event.setCancelled(true);
			return;
		}

		if (event.isCancelled())
			return;

		if (pManager.getBoolean(player, "fchat") && Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			MPlayer mp = MPlayer.get(player);
			for (Player target : mp.getFaction().getOnlinePlayers()) {
				MSG.tell(target,
						MSG.getString("Command.FC.Format", "%faction% %faction_role% %prefix%%player% %message%")
								.replace("%faction%", mp.getFactionName())
								.replace("%faction_role%", mp.getRole().getName())
								.replace("%prefix%", pManager.getPrefix(player))
								.replace("%player%", player.getDisplayName()).replace("%message%", event.getMessage()));
			}
			event.setCancelled(true);
			return;
		}
		for (Player target : Bukkit.getOnlinePlayers()) {
			if (event.getMessage().contains(target.getDisplayName())) {
				if (pManager.getBoolean(target, "mention"))
					target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 2);
			}
		}
		double slow = Main.plugin.data.getDouble("ChatSlow");
		if (slow != 0 && !player.hasPermission("basic.chatslow.bypass")) {
			if (pManager.getInfo(player, "lastChat") == null)
				pManager.setInfo(player, "lastChat", (double) System.currentTimeMillis());
			double lastMsg = pManager.getDouble(player, "lastChat"), since = System.currentTimeMillis() - lastMsg;
			if (slow == -1) {
				MSG.tell(player, MSG.getString("Command.ChatSlow.Muted", "chat is disabled").replace("%prefix%",
						MSG.getString("Command.ChatSlow.Prefix", "ChatSlow")));
				event.setCancelled(true);
				return;
			}

			if (since < slow) {
				MSG.tell(player,
						MSG.getString("Command.ChatSlow.Slow", "%prefix% please wait %time% to chat")
								.replace("%prefix%", MSG.getString("Command.ChatSlow.Prefix", "ChatSlow"))
								.replace("%time%", TimeManager.getTime(slow - since)));
				event.setCancelled(true);
				return;
			}
		}

		String group = "default";
		String format = Main.plugin.config.getString("Format." + group);
		if (Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
			group = PermissionsEx.getUser(player).getParentIdentifiers().get(0);
			if (!Main.plugin.config.contains("Format." + group)) {
				group = "default";
			}
			format = Main.plugin.config.getString("Format." + group);
			format = format.replace("%prefix%", PermissionsEx.getUser(player).getPrefix());
		}

		if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			MPlayer mp = MPlayer.get(player);
			format = format.replace("%faction_name%",
					mp.getFactionName().replace("%faction_power%", mp.getFaction().getPower() + ""));
		}

		format = format.replace("%display_name%", player.getDisplayName()).replace("%name%", player.getName()
				.replace("%custom_name%", player.getCustomName() == null ? "" : player.getCustomName()));
		if (Bukkit.getPluginManager().isPluginEnabled("Cardinal")) {
			org.mswsplex.cardinal.msws.Main cardinal = (org.mswsplex.cardinal.msws.Main) Bukkit.getPluginManager()
					.getPlugin("Cardinal");
			format = format.replace("%cardinalRank%",
					MSG.color(pManager.getRankColor(cardinal.getPlayerManager().getRank(player))
							+ cardinal.getPlayerManager().getRank(player)));
		}
		for (Player p : Bukkit.getOnlinePlayers())
			p.sendMessage(MSG.color(format).replace("%message%",
					player.hasPermission("basic.chat.color") ? MSG.color(event.getMessage()) : event.getMessage()));
		MSG.log(MSG.color(format).replace("%message%",
				player.hasPermission("basic.chat.color") ? MSG.color(event.getMessage()) : event.getMessage()));

		event.setCancelled(true);

		pManager.setInfo(player, "lastChat", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (event.getBlock() == null)
			return;
		if (player.hasPermission("basic.sign.color")) {
			int pos = 0;
			for (String line : event.getLines()) {
				event.setLine(pos, MSG.color(line));
				pos++;
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "login") != null
				&& (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())) {
			event.setTo(event.getFrom());
			MSG.nonSpam(player, MSG.getString("Command.2FA.Reverify", "enter 2FA code in chat or with /2fa [code]")
					.replace("%prefix%", MSG.getString("Command.2FA.Prefix", "2FA")));
		}
		if (pManager.isFrozen(player) && event.getFrom().distanceSquared(event.getTo()) > 0)
			event.setTo(event.getFrom());
		if (pManager.getDouble(player, "lastMove") != 0)
			if (System.currentTimeMillis() - pManager.getDouble(player, "lastMove") > 120000) {
				double time = 0;
				if (pManager.getInfo(player, "playtime") != null)
					time += pManager.getDouble(player, "playtime");
				time += (pManager.getDouble(player, "lastMove") - pManager.getDouble(player, "lastJoin"));
				pManager.setInfo(player, "playtime", time);
				pManager.setInfo(player, "lastJoin", System.currentTimeMillis());
			}

		pManager.setInfo(player, "lastMove", System.currentTimeMillis());

		if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			MPlayer mp = MPlayer.get(player);
			Faction f = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
			if (mp.getFaction().getId().equals(f.getId()) && player.hasPermission("basic.factionfly.own")) {
				player.setAllowFlight(true);
			} else if (!mp.getFaction().getId().equals(f.getId()) && !player.hasPermission("basic.fly")) {
				player.setFlying(false);
				player.setAllowFlight(false);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player) || pManager.isGod(player))
				event.setCancelled(true);
			if (event.getCause() == DamageCause.FALL && Bukkit.getPluginManager().isPluginEnabled("Factions")) {
				Faction f = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
				if (f.getId().equals(Factions.ID_SAFEZONE)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player) || pManager.isGod(player))
				event.setCancelled(true);
			if (event.getFoodLevel() > player.getFoodLevel())
				return;
			if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
				Faction f = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
				if (f.getId().equals(Factions.ID_SAFEZONE)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() == null)
			return;
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(player, "login") != null || pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (pManager.getInfo(player, "openStaff") != null) {
			event.setCancelled(true);
			if (event.getRawSlot() == event.getInventory().getSize() - 9) {
				player.openInventory(StaffGUI.getStaffInventory(pManager.getDouble(player, "page").intValue() - 1));
				pManager.setInfo(player, "page", pManager.getDouble(player, "page") - 1);
				pManager.setInfo(player, "openStaff", true);
			}
			if (event.getRawSlot() == event.getInventory().getSize() - 1) {
				player.openInventory(StaffGUI.getStaffInventory(pManager.getDouble(player, "page").intValue() + 1));
				pManager.setInfo(player, "page", pManager.getDouble(player, "page") + 1);
				pManager.setInfo(player, "openStaff", true);
			}
		}

		String inv = pManager.getString(player, "openInventory");
		if (inv == null)
			return;
		Player target = Bukkit.getPlayer(UUID.fromString(inv));
		if (target == null || !target.isOnline()) {
			event.setCancelled(true);
			player.closeInventory();
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String ip = player.getAddress().getHostString();
		for (Player target : Bukkit.getOnlinePlayers()) {
			if (pManager.getBoolean(target, "notify"))
				target.playSound(target.getLocation(), Sound.NOTE_PLING, 2, 2);
		}
		if (!player.hasPermission("basic.staff")) {
			if (Main.plugin.data.getStringList("Staff").contains(player.getUniqueId() + "")) {
				List<String> staff = Main.plugin.data.getStringList("Staff");
				staff.remove(player.getUniqueId() + "");
				Main.plugin.data.set("Staff", staff);
			}
		}

		List<String> ips = Main.plugin.data.getStringList("IPs." + player.getUniqueId());
		if (ips == null)
			ips = new ArrayList<String>();
		if (!ips.contains(ip))
			ips.add(ip);
		Main.plugin.data.set("IPs." + player.getUniqueId(), ips);
		ips = Main.plugin.data.getStringList("IPs." + ip.replace(".", ","));
		if (ips == null)
			ips = new ArrayList<String>();
		if (!ips.contains(player.getUniqueId() + ""))
			ips.add(player.getUniqueId() + "");
		Main.plugin.data.set("IPs." + ip.replace(".", ","), ips);
		if (!player.hasPlayedBefore()) {
			for (String res : Main.plugin.config.getStringList("FirstJoin")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), MSG.parse(player, res));
			}
		}
		if (player.hasPermission("basic.2fa")) {
			if (pManager.getInfo(player, "2fakey") == null || pManager.getInfo(player, "setup") != null) {
				pManager.setup2fa(player);
			} else {
				if ((pManager.getInfo(player, "lastIP") != null && !pManager.getString(player, "lastIP").equals(ip))
						|| System.currentTimeMillis() - pManager.getDouble(player, "lastVerify") > 8.64e+7) {
					pManager.setInfo(player, "login", true);
					MSG.tell(player, MSG.getString("Command.2FA.Reverify", "sign back in").replace("%prefix%",
							MSG.getString("Command.2FA.Prefix", "2FA")));
				} else {
					pManager.setInfo(player, "login", null);
					MSG.tell(player, MSG.getString("Command.2FA.Time", "verified for %time%")
							.replace("%prefix%", MSG.getString("Command.2FA.Prefix", "2FA"))
							.replace("%time%", TimeManager.getTime(
									8.64e+7 + pManager.getDouble(player, "lastVerify") - System.currentTimeMillis())));
				}
			}
		} else {
			pManager.removeInfo(player, "setup");
			pManager.removeInfo(player, "2fakey");
			pManager.removeInfo(player, "login");
			pManager.removeInfo(player, "lastVerify");
		}
		event.setJoinMessage(
				MSG.color(Main.plugin.config.getString("JoinMessage").replace("%player%", player.getName())));
		if (pManager.getInfo(player, "firstJoined") == null)
			pManager.setInfo(player, "firstJoined", player.getFirstPlayed());
		pManager.setInfo(player, "lastJoin", System.currentTimeMillis());
		pManager.setInfo(player, "lastMove", System.currentTimeMillis());
		pManager.setInfo(player, "lastIP", ip);
		Main.plugin.saveData();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.setQuitMessage(
				MSG.color(Main.plugin.config.getString("QuitMessage").replace("%player%", player.getName())));
		double time = 0;
		if (pManager.getInfo(player, "playtime") != null)
			time += pManager.getDouble(player, "playtime");
		if (pManager.getDouble(player, "lastMove") != 0
				&& System.currentTimeMillis() - pManager.getDouble(player, "lastMove") > 120000) {
			time += 120000;
		} else {
			time += (System.currentTimeMillis() - pManager.getDouble(player, "lastJoin"));
		}
		pManager.setInfo(player, "playtime", time);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (pManager.getInfo(event.getPlayer(), "login") != null
				&& !event.getMessage().toLowerCase().startsWith("/2fa"))
			event.setCancelled(true);
		String[] block = { "holo", "nte", "nametagedit", "mv", "pex", "permissionsex", "gal", "e", "/", "info",
				"protocol", "filter", "crazyauctions", "ca", "crate", "silk", "co" };
		String[] partier = { "shrug", "tableflip" };
		String[] radical = { "nick" };
		String[] trainee = { "p", "punish", "tp", "v", "freeze" };
		String msg = event.getMessage().toLowerCase();
		if (msg.contains(" "))
			msg = msg.split(" ")[0];
		if (!player.hasPermission("rank.partier"))
			for (String res : partier) {
				if (msg.equals("/" + res.toLowerCase())) {
					MSG.tell(player, MSG.prefix()
							+ " This requires permission rank &8[&6&lPARTIER&8]&7. Purchase ranks at PartyMCGames.enjin.com/shop");
					event.setCancelled(true);
				}
			}
		if (!player.hasPermission("rank.radical"))
			for (String res : radical) {
				if (msg.equals("/" + res.toLowerCase())) {
					MSG.tell(player, MSG.prefix()
							+ " This requires permission rank &8[&d&lRADICAL&8]&7. Purchase ranks at PartyMCGames.enjin.com/shop");
					event.setCancelled(true);
				}
			}
		if (!player.hasPermission("rank.trainee"))
			for (String res : trainee) {
				if (msg.equals("/" + res.toLowerCase())) {
					MSG.tell(player, MSG.prefix() + " This requires permission rank &8[&3&lTRAINEE&8]&7.");
					event.setCancelled(true);
				}
			}
		if (!player.hasPermission("rank.srmod")) {
			if (event.getMessage().toLowerCase().startsWith("/chatslow")) {
				MSG.tell(player, MSG.prefix() + " This requires permission rank &8[&d&lSR. MOD&8]&7.");
				event.setCancelled(true);
			}
		}
		if (!player.hasPermission("rank.admin"))
			for (String res : block) {
				if (event.getMessage().toLowerCase().startsWith("/" + res.toLowerCase())) {
					MSG.tell(event.getPlayer(), "Unknown command. Type \"/help\" for help.");
					event.setCancelled(true);
					break;
				}
			}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		pManager.setInfo(player, "openInventory", null);
		pManager.setInfo(player, "openStaff", null);
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		event.setMotd(MSG.color(Main.plugin.config.getString("MOTD")).replace("|", "\n"));
		event.setMaxPlayers(Bukkit.getOnlinePlayers().size() + 1);
	}

	@EventHandler
	public void onDamaged(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if (!event.isCancelled())
			pManager.setInfo(player, "lastHit", System.currentTimeMillis());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		String msg = MSG.color("&7Death &8>>&7 " + event.getDeathMessage().replace(event.getEntity().getName(),
				"&e" + event.getEntity().getDisplayName() + "&7"));
		String[] args = msg.split(" ");
		for (String res : args) {
			for (Player t : Bukkit.getOnlinePlayers()) {
				if (res.equals(t.getName())) {
					msg = msg.replace(res, "&e" + t.getDisplayName() + "&7");
					break;
				}
			}
		}
		event.setDeathMessage(MSG.color(msg + "."));
	}

	@EventHandler
	public void onChatComplete(PlayerChatTabCompleteEvent event) {
		event.getTabCompletions().clear();
		for (Player t : Bukkit.getOnlinePlayers()) {
			if (t.getDisplayName().toLowerCase().startsWith(event.getLastToken().toLowerCase())) {
				event.getTabCompletions().add(t.getDisplayName());
			}
		}
	}
}
