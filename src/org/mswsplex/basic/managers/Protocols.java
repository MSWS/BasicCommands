package org.mswsplex.basic.managers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.mswsplex.msws.basic.Main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public class Protocols {
	static Map<UUID, Location> angles = new HashMap<>();

	@SuppressWarnings("deprecation")
	public Protocols() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketAdapter
				.params(Main.plugin, new PacketType[] { PacketType.Status.Server.OUT_SERVER_INFO }).optionAsync()) {
			public void onPacketSending(PacketEvent event) {
				WrappedServerPing ping = (WrappedServerPing) event.getPacket().getServerPings().read(0);
				ping.setPlayers(null);
			}
		});

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.plugin, ListenerPriority.NORMAL,
				new PacketType[] { PacketType.Play.Client.TAB_COMPLETE }) {
			public void onPacketReceiving(final PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
					try {
						final PacketContainer packet = event.getPacket();
						final String message = ((String) packet.getSpecificModifier((Class<?>) String.class).read(0))
								.toLowerCase();
						if (message.startsWith("/") && !message.contains(" ")) {
							event.setCancelled(true);
							PacketContainer tabComplete = manager.createPacket(PacketType.Play.Server.TAB_COMPLETE);
							String[] allList = {"a", "ah", "apply", "bal", "discord", "f", "kit", "message", "money", "pay", "playtime", "r", "rtp", "rules", "spawn", "tell", "toggle", "tpa", "tpno", "tpyes", "w", "warp", "wild" };
							String[] partyList = {"shrug","tableflip"};
							String[] radicalList = {"nick"};
							String[] traineeList = {"ma", "p", "ra", "tp", "vanish", "2fa"};
							String[] srmodList = {"chatslow"};
							String[] adminList = {"crate", "e", "g", "give", "holo", "mv", "ness", "nte", "pex", "plugman", "refresh"};
							List<String> toAdd = new ArrayList<String>();
							for(String res:allList)
								if(("/"+res).toLowerCase().startsWith(message))
									toAdd.add(res);
							if(event.getPlayer().hasPermission("rank.partier")) {
								for(String res:partyList)
									if(("/"+res).toLowerCase().startsWith(message))
										toAdd.add(res);
							}
							if(event.getPlayer().hasPermission("rank.radical")) {
								for(String res:radicalList)
									if(("/"+res).toLowerCase().startsWith(message))
										toAdd.add(res);
							}
							if(event.getPlayer().hasPermission("rank.trainee")) {
								for(String res:traineeList)
									if(("/"+res).toLowerCase().startsWith(message))
										toAdd.add(res);
							}
							if(event.getPlayer().hasPermission("rank.srmod")) {
								for(String res:srmodList)
									if(("/"+res).toLowerCase().startsWith(message))
										toAdd.add(res);
							}
							if(event.getPlayer().hasPermission("rank.admin")) {
								for(String res:adminList)
									if(("/"+res).toLowerCase().startsWith(message))
										toAdd.add(res);
							}
							String[] result = new String[toAdd.size()];
							for(int i=0;i<toAdd.size();i++) {
								result[i] = "/"+toAdd.get(i);
							}
							tabComplete.getStringArrays().write(0, result);
							try {
								 ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(),
								 tabComplete);
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					} catch (FieldAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		);

	}
}
