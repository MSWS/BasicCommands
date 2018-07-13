package org.mswsplex.basic.events;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.basic.managers.PlayerManager;
import org.mswsplex.basic.managers.TimeManager;
import org.mswsplex.basic.utils.MSG;
import org.mswsplex.basic.utils.StaffGUI;
import org.mswsplex.msws.basic.Main;

public class Events implements Listener {
	public Events() {
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}

	PlayerManager pManager = new PlayerManager();

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		double slow = Main.plugin.data.getDouble("ChatSlow");
		if(slow!=0&&!player.hasPermission("basic.chatslow.bypass")) {
			if(pManager.getInfo(player, "lastChat")==null)
				pManager.setInfo(player, "lastChat",  (double) System.currentTimeMillis());
			double lastMsg = pManager.getDouble(player, "lastChat"), since = System.currentTimeMillis()-lastMsg;
			if(slow==-1) {
				MSG.tell(player, MSG.getString("Command.ChatSlow.Muted", "chat is disabled")
						.replace("%prefix%", MSG.getString("Command.ChatSlow.Prefix", "ChatSlow")));
				event.setCancelled(true);
				return;
			}
			
			if(since<slow) {
				MSG.tell(player, MSG.getString("Command.ChatSlow.Slow", "%prefix% please wait %time% to chat")
						.replace("%prefix%", MSG.getString("Command.ChatSlow.Prefix", "ChatSlow"))
						.replace("%time%", TimeManager.getTime(slow-since)));
				event.setCancelled(true);
				return;
			}
		}
		pManager.setInfo(player, "lastChat",  (double) System.currentTimeMillis());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (pManager.isFrozen(player) && event.getFrom().distanceSquared(event.getTo()) > 0)
			event.setTo(event.getFrom());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		if (pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (pManager.isFrozen(player)||pManager.isGod(player))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() == null)
			return;
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (pManager.isFrozen(player))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (pManager.isFrozen(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(pManager.getInfo(player, "openStaff")!=null) {
			event.setCancelled(true);
			if(event.getRawSlot()==event.getInventory().getSize()-9) {
				player.openInventory(StaffGUI.getStaffInventory(pManager.getDouble(player, "page").intValue()-1));
				pManager.setInfo(player, "page", pManager.getDouble(player, "page")-1);
				pManager.setInfo(player, "openStaff", true);
			}
			if(event.getRawSlot()==event.getInventory().getSize()-1) {
				player.openInventory(StaffGUI.getStaffInventory(pManager.getDouble(player, "page").intValue()+1));
				pManager.setInfo(player, "page", pManager.getDouble(player, "page")+1);
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

		if (!player.hasPermission("basic.invsee.interact"))
			event.setCancelled(true);

		ItemStack armor[] = new ItemStack[4];
		ItemStack[] newInv = new ItemStack[36];
		for (int i = 0; i < newInv.length; i++) {
			newInv[i] = event.getInventory().getContents()[i];
		}

		target.getInventory().setContents(newInv);
		target.updateInventory();

		for (int i = 0; i < 4; i++) {
			ItemStack item = event.getInventory().getItem(i + 36 - 9);
			if (item == null || item.getType() == Material.AIR)
				continue;
			armor[i] = item;
		}

		target.getInventory().setArmorContents(armor);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("basic.staff")) {
			if (Main.plugin.data.getStringList("Staff").contains(player.getUniqueId() + "")) {
				List<String> staff = Main.plugin.data.getStringList("Staff");
				staff.remove(player.getUniqueId()+"");
				Main.plugin.data.set("Staff",staff);
			}
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		List<String> staff = Main.plugin.data.getStringList("Staff");
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("basic.staff") && !staff.contains(player.getUniqueId() + "")) {
				staff.add(player.getUniqueId() + "");
			}
		}
		Main.plugin.data.set("Staff", staff);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		pManager.setInfo(player, "openInventory", null);
		pManager.setInfo(player, "openStaff", null);
	}
}
