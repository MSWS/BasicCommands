package org.mswsplex.basic.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;


public class Utils {
	private static boolean bungeeApiPresent;
	
	public static String worldTime(long time) {
		long gameTime = time, hours = gameTime / 1000 + 6, minutes = (gameTime % 1000) * 60 / 1000;
		String ampm = "AM";
		if (hours >= 12) {
			hours -= 12;
			ampm = "PM";
		}
		if (hours >= 12) {
			hours -= 12;
			ampm = "AM";
		}
		if (hours == 0)
			hours = 12;
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2, mm.length());
		return hours + ":" + mm + " " + ampm;
	}
	
	public static boolean isMaterial(String name) {
		for (Material mat : Material.values())
			if (mat.toString().equals(name))
				return true;
		return false;
	}
	
	public static boolean isArmor(Material mat) {
		return mat.name().contains("CHESTPLATE") || mat.name().contains("LEGGINGS") || mat.name().contains("HELMET")
				|| mat.name().contains("BOOTS");
	}
	
	public static String getEnchant(String name) {
		switch (name.toLowerCase().replace("_", "")) {
		case "power":
			return "ARROW_DAMAGE";
		case "flame":
			return "ARROW_FIRE";
		case "infinity":
		case "infinite":
			return "ARROW_INFINITE";
		case "punch":
		case "arrowkb":
			return "ARROW_KNOCKBACK";
		case "sharpness":
			return "DAMAGE_ALL";
		case "arthropods":
		case "spiderdamage":
		case "baneofarthropods":
			return "DAMAGE_ARTHORPODS";
		case "smite":
			return "DAMAGE_UNDEAD";
		case "depthstrider":
		case "waterwalk":
			return "DEPTH_STRIDER";
		case "efficiency":
			return "DIG_SPEED";
		case "unbreaking":
			return "DURABILITY";
		case "fireaspect":
		case "fire":
			return "FIRE_ASPECT";
		case "knockback":
		case "kb":
			return "KNOCKBACK";
		case "fortune":
			return "LOOT_BONUS_BLOCKS";
		case "looting":
			return "LOOT_BONUS_MOBS";
		case "luck":
			return "LUCK";
		case "lure":
			return "LURE";
		case "waterbreathing":
		case "respiration":
			return "OXYGEN";
		case "prot":
		case "protection":
			return "PROTECTION_ENVIRONMENTAL";
		case "blastprot":
		case "blastprotection":
			return "PROTECTION_EXPLOSIONS";
		case "feather":
		case "featherfalling":
			return "PROTECTION_FALL";
		case "fireprot":
		case "fireprotection":
			return "PROTECTION_FIRE";
		case "projectileprot":
		case "projectileprotection":
		case "projprot":
			return "PROTECTION_PROJECTILE";
		case "silktouch":
		case "silk":
			return "SILK_TOUCH";
		case "thorns":
			return "THORNS";
		case "aquaaffinity":
		case "aqua":
		case "waterworker":
			return "WATER_WORKER";
		}
		return name.toUpperCase();
	}
	
	public static String parseDecimal(String name, int length) {
		if (name.contains(".")) {
			if (name.split("\\.")[1].length() > 2) {
				name = name.split("\\.")[0] + "."
						+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), length));
			}
		}
		return name;
	}
	
    public static String unpackMessage(final String json, final boolean denyEvents){
        Validate.isTrue(bungeeApiPresent, "(Un)packing chat requires Spigot 1.7.10 or newer");
        String text = "";
        try {
            BaseComponent[] parse;
            for (int length = (parse = ComponentSerializer.parse(json)).length, i = 0; i < length; ++i) {
                final BaseComponent comp = parse[i];
                if ((comp.getHoverEvent() != null || comp.getClickEvent() != null) && denyEvents) {
                }
                text = String.valueOf(text) + comp.toLegacyText();
            }
        }
        catch (Throwable t) {
            MSG.log("Unable to parse JSON message. Got " + t.getMessage());
        }
        return text;
    }
    
    public static String packMessage(final String message) {
        Validate.isTrue(bungeeApiPresent, "(Un)packing chat requires Spigot 1.7.10 or newer");
        return ComponentSerializer.toString(TextComponent.fromLegacyText(message));
    }
}
