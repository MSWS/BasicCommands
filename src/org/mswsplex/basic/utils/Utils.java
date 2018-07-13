package org.mswsplex.basic.utils;

public class Utils {
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
}
