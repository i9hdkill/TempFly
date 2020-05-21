package com.moneybags.tempfly.time;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.moneybags.tempfly.fly.FlyHandle;
import com.moneybags.tempfly.fly.Flyer;
import com.moneybags.tempfly.util.FileHandler;
import com.moneybags.tempfly.util.ConfigValues;

public class TimeHandle {

	public static double getTime(UUID u) {
		FileConfiguration data = FileHandler.data;
		String path = "players." + u.toString() + ".time";
		
		Flyer f = FlyHandle.getFlyer(Bukkit.getPlayer(u));
		
		if (f != null) {
			return f.getTime();
		} else {
			return data.getInt(path);
		}
	}

	public static long getLastDailyTimeBonusTime(UUID u) {
        FileConfiguration data = FileHandler.data;
        String path = "players." + u.toString() + ".got-daily-time";
        
        return data.getLong(path);
    }
	
	public static void removeTime(UUID u, double seconds) {
		FileConfiguration data = FileHandler.data;
		String path = "players." + u.toString() + ".time";
		Flyer f = FlyHandle.getFlyer(Bukkit.getPlayer(u));
		
		double bal = f == null ? data.getDouble(path) : f.getTime();
		double remaining = (((bal-seconds) >= 0) ? (bal-seconds) : 0);
		
		if (f != null) {
			f.setTime(remaining);
		} 
		data.set(path, remaining);
		FileHandler.saveData();
	}
	
	public static void addDailyTime(UUID uuid, double seconds) {
	    LocalDateTime.now();
	    FileConfiguration data = FileHandler.data;
        String path = "players." + uuid.toString() + ".got-daily-time";
        Flyer f = FlyHandle.getFlyer(Bukkit.getPlayer(uuid));
        long currentDate = System.currentTimeMillis();
        data.set(path, currentDate);
        FileHandler.saveData();
        
	    addTime(uuid, seconds);
	}
	
	public static void addTime(UUID u, double seconds) {
		FileConfiguration data = FileHandler.data;
		String path = "players." + u.toString() + ".time";
		Flyer f = FlyHandle.getFlyer(Bukkit.getPlayer(u));
		
		double bal = f == null ? data.getDouble(path) : f.getTime();
		// This line prevents an overflow to -Double.MAX_VALUE.
		double remaining = (((bal+seconds) >= bal) ? (bal+seconds) : Double.MAX_VALUE);
		
		
		if (f != null) {
			f.setTime(remaining);
		}
		data.set(path, remaining);
		FileHandler.saveData();
	}
	
	public static void setTime(UUID u, double seconds) {
		FileConfiguration data = FileHandler.data;
		String path = "players." + u.toString() + ".time";
		Flyer f = FlyHandle.getFlyer(Bukkit.getPlayer(u));
		
		if (f != null) {
			f.setTime(seconds);
			
		}
		data.set(path, seconds);
		FileHandler.saveData();
	}
	
	public static String regexString(String s, double seconds) {
		long days = formatTime(TimeUnit.DAYS, Math.ceil(seconds));
		long hours = formatTime(TimeUnit.HOURS, Math.ceil(seconds));
		long minutes = formatTime(TimeUnit.MINUTES, Math.ceil(seconds));
		long secs = formatTime(TimeUnit.SECONDS, Math.ceil(seconds));
		
		String fin = "";
		if (days > 0) fin = fin.concat(ConfigValues.timeFormat
				.replaceAll("\\{QUANTITY}", String.valueOf(days))
				.replaceAll("\\{UNIT}", ConfigValues.unitDays) + " ");
		if (hours > 0) fin = fin.concat(ConfigValues.timeFormat
				.replaceAll("\\{QUANTITY}", String.valueOf(hours))
				.replaceAll("\\{UNIT}", ConfigValues.unitHours) + " ");
		if (minutes > 0) fin = fin.concat(ConfigValues.timeFormat
				.replaceAll("\\{QUANTITY}", String.valueOf(minutes))
				.replaceAll("\\{UNIT}", ConfigValues.unitMinutes) + " ");
		if (secs > 0 || fin.length() == 0) fin = fin.concat(ConfigValues.timeFormat
				.replaceAll("\\{QUANTITY}", String.valueOf(secs))
				.replaceAll("\\{UNIT}", ConfigValues.unitSeconds) + " ");
		if ((fin.length() > 0) && (String.valueOf(fin.charAt(fin.length() - 1)).equals(" "))) {
			fin = fin.substring(0, fin.length()-1);
		}
		return s.replaceAll("\\{FORMATTED_TIME}", fin)
				.replaceAll("\\{DAYS}", String.valueOf(formatTime(TimeUnit.DAYS, seconds)))
				.replaceAll("\\{HOURS}", String.valueOf(formatTime(TimeUnit.HOURS, seconds)))
				.replaceAll("\\{MINUTES}", String.valueOf(formatTime(TimeUnit.MINUTES, seconds)))
				.replaceAll("\\{SECONDS}", String.valueOf(formatTime(TimeUnit.SECONDS, seconds)));
	}
	
	public static long formatTime(TimeUnit unit, double seconds) {
		switch (unit) {
		case DAYS:
			return (long)seconds / 86400;
		case HOURS:
			return (long)seconds % 86400 / 3600;
		case MINUTES:
			return (long)seconds % 3600 / 60;
		case SECONDS:
			return (long)seconds % 60;
		default:
			return 0;
		}
	}
}
