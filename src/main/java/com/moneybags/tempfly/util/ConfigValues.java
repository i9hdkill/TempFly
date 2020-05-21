package com.moneybags.tempfly.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.moneybags.tempfly.util.FileHandler.FileType;

public class ConfigValues {

	public static String
	prefix,
	reload,
	infinity,
	
	invalidPermission,
	invalidPlayer,
	invalidNumber,
	invalidTimeSelf,
	invalidTimeOther,
	invalidSender,
	invalidCommand,
	invalidReciever,
	invalidFlyerSelf,
	invalidZoneOther,
	invalidZoneSelf,
	invalidIsland,
	invalidFunds,
	
	timeGivenOther,
	timeGivenSelf,
	timeRemovedOther,
	timeRemovedSelf,
	timeSentOther,
	timeSentSelf,
	timeSetOther,
	timeSetSelf,
	timeMaxOther,
	timeMaxSelf,
	timeDecayLost,
	timeFormat,
	timePurchased,
	firstJoin,
	dailyLogin,
	
	unitSeconds,
	unitMinutes,
	unitHours,
	unitDays,
	
	infoHeader,
	infoPlayer,
	infoDays,
	infoHours,
	infoMinutes,
	infoSeconds,
	infoFooter,
	infoInfinite,
	
	flyEnabledOther,
	flyEnabledSelf,
	flyDisabledOther,
	flyDisabledSelf,
	flySpeedOther,
	flySpeedSelf,
	flyCooldownDeny,
	flyCooldownOver,
	flyRequirementFail,
	
	particleType,
	listName,
	tagName,
	
	fbDays,
	fbHours,
	fbMinutes,
	fbSeconds,
	
	warningTitle,
	warningSubtitle,
	
	actionText;

	public static boolean
	groundTimer,
	idleTimer,
	idleDrop,
	payable,
	particles,
	list,
	tag,
	attackP,
	attackM,
	attackedP,
	attackedM,
	protCommand,
	protTime,
	protCombat,
	actionBar,
	actionProgress,
	permaTimer,
	timeDecay,
	flightToggle,
	hideVanish,
	shop;
	
	public static int
	idleThreshold,
	save,
	cooldownPvp,
	cooldownPve,
	maxY,
	decayThresh;
	
	public static double
	maxTime,
	firstJoinTime,
	legacyBonus,
	decayAmount;
	
	public static List<String>
	help = new ArrayList<>(),
	helpExtended = new ArrayList<>(),
	disabledWorlds = new ArrayList<>(),
	disabledRegions = new ArrayList<>(),
	overrideFlightPermissions = new ArrayList<>();
	
	public static List<Long> 
	warningTimes;
	
	public static Map<String, Double>
	dailyBonus = new HashMap<>();

	public static void loadValues() {
		FileConfiguration config = FileHandler.config;
		
		prefix 				= st(FileType.LANG, "system.prefix");
		reload 				= st(FileType.LANG, "system.reload");
		
		invalidPermission	= st(FileType.LANG, "general.invalid.permission");
		invalidPlayer		= st(FileType.LANG, "general.invalid.player");
		invalidNumber		= st(FileType.LANG, "general.invalid.number");
		invalidSender		= st(FileType.LANG, "general.invalid.sender");
		invalidCommand		= st(FileType.LANG, "general.invalid.command");
		invalidTimeOther	= st(FileType.LANG, "general.invalid.time_other");
		invalidTimeSelf		= st(FileType.LANG, "general.invalid.time_self");
		invalidReciever		= st(FileType.LANG, "general.invalid.reciever");
		invalidFlyerSelf	= st(FileType.LANG, "general.invalid.flyer_self");
		invalidZoneOther	= st(FileType.LANG, "general.invalid.zone_other");
		invalidZoneSelf		= st(FileType.LANG, "general.invalid.zone_self");
		invalidIsland		= st(FileType.LANG, "general.invalid.island");
		invalidFunds		= st(FileType.LANG, "general.invalid.funds");
		
		timeGivenOther		= st(FileType.LANG, "general.time.given_other");
		timeGivenSelf		= st(FileType.LANG, "general.time.given_self");
		timeRemovedOther	= st(FileType.LANG, "general.time.removed_other");
		timeRemovedSelf		= st(FileType.LANG, "general.time.removed_self");
		timeSentOther		= st(FileType.LANG, "general.time.sent_other");
		timeSentSelf		= st(FileType.LANG, "general.time.sent_self");
		timeSetOther		= st(FileType.LANG, "general.time.set_other");
		timeSetSelf			= st(FileType.LANG, "general.time.set_self");
		timeMaxOther		= st(FileType.LANG, "general.time.max_other");
		timeMaxSelf			= st(FileType.LANG, "general.time.max_self");
		timeDecayLost		= st(FileType.LANG, "general.time.decay");
		timeFormat			= st(FileType.LANG, "general.time.format");
		timePurchased		= st(FileType.LANG, "general.time.purchased");
		firstJoin			= st(FileType.LANG, "general.time.first_join");
		dailyLogin			= st(FileType.LANG, "general.time.daily_login");
		
		unitSeconds			= st(FileType.LANG, "general.unit.seconds", "s");
		unitMinutes			= st(FileType.LANG, "general.unit.minutes", "m");
		unitHours			= st(FileType.LANG, "general.unit.hours", "h");
		unitDays			= st(FileType.LANG, "general.unit.days", "d");
		
		infoHeader			= st(FileType.LANG, "general.info.header");
		infoPlayer			= st(FileType.LANG, "general.info.player");
		infoDays			= st(FileType.LANG, "general.info.days");
		infoHours			= st(FileType.LANG, "general.info.hours");
		infoMinutes			= st(FileType.LANG, "general.info.minutes");
		infoSeconds			= st(FileType.LANG, "general.info.seconds");
		infoFooter			= st(FileType.LANG, "general.info.footer");
		infoInfinite		= st(FileType.LANG, "general.info.infinite");
		
		flyEnabledOther	    = st(FileType.LANG, "general.fly.enabled_other");
		flyEnabledSelf	    = st(FileType.LANG, "general.fly.enabled_self");
		flyDisabledOther	= st(FileType.LANG, "general.fly.disabled_other");
		flyDisabledSelf 	= st(FileType.LANG, "general.fly.disabled_self");
		flySpeedOther		= st(FileType.LANG, "general.fly.speed_other");
		flySpeedSelf		= st(FileType.LANG, "general.fly.speed_self");
		flyCooldownDeny		= st(FileType.LANG, "general.fly.cooldown_deny");
		flyCooldownOver		= st(FileType.LANG, "general.fly.cooldown_over");
		flyRequirementFail  = st(FileType.LANG, "general.fly.requirement_fail");
		
		fbDays				= st(FileType.LANG, "aesthetic.featherboard.days");
		fbHours				= st(FileType.LANG, "aesthetic.featherboard.hours");
		fbMinutes			= st(FileType.LANG, "aesthetic.featherboard.minutes");
		fbSeconds			= st(FileType.LANG, "aesthetic.featherboard.seconds");
		infinity			= st(FileType.LANG, "aesthetic.symbols.infinity");
		
		warningTitle		= st(FileType.CONFIG, "aesthetic.warning.title");
		warningSubtitle		= st(FileType.CONFIG, "aesthetic.warning.subtitle");
		
		actionText			= st(FileType.CONFIG, "aesthetic.action_bar.text");
		
		List<String> h 		= FileHandler.lang.getStringList("system.help");
		if (h != null) {
			for (String s: h) {
				help.add(U.cc(s));
			}
		}
		
		List<String> he 	= FileHandler.lang.getStringList("system.help_extended");
		if (he != null) {
			for (String s: he) {
				helpExtended.add(U.cc(s));
			}
		}
		
		try {
			warningTimes    = FileHandler.config.getLongList("aesthetic.warning.seconds");
		} catch (Exception e) {
			warningTimes = new ArrayList<>();
			U.logW("You can only set numbers under (aesthetic.warning.seconds) in the config!");
		}
		
		
		disabledWorlds	 	= FileHandler.config.getStringList("general.disabled.worlds");
		if (disabledWorlds == null) {
			disabledWorlds = new ArrayList<>();
		}
		
		disabledRegions	 	= FileHandler.config.getStringList("general.disabled.regions");
		if (disabledRegions == null) {
			disabledRegions = new ArrayList<>();
		}
		
		overrideFlightPermissions = FileHandler.config.getStringList("general.fly_override_permissions");
		if (overrideFlightPermissions == null) {
			overrideFlightPermissions = new ArrayList<>();
		}
		
		
		groundTimer			= config.getBoolean("general.timer.ground");
		idleTimer 			= config.getBoolean("general.timer.idle");
		idleDrop			= config.getBoolean("general.idle.drop_player");
		idleThreshold 		= config.getInt("general.idle.threshold");
		payable				= config.getBoolean("general.time.payable");
		maxTime				= config.getDouble("general.time.max_time");
		save 				= config.getInt("system.backup", 5);
		particles			= config.getBoolean("aesthetic.identifier.particles.enabled");
		particleType		= config.getString("aesthetic.identifier.particles.type", "VILLAGER_HAPPY");
		hideVanish			= config.getBoolean("aesthetic.identifier.particles.hide_vanish");
		list				= config.getBoolean("aesthetic.identifier.tab_list.enabled");
		listName			= st(FileType.CONFIG, "aesthetic.identifier.tab_list.name");
		tag					= config.getBoolean("aesthetic.identifier.name_tag.enabled");
		tagName				= st(FileType.CONFIG, "aesthetic.identifier.name_tag.name");
		attackP				= config.getBoolean("general.disable_flight.attack_player");
		attackM				= config.getBoolean("general.disable_flight.attack_mob");
		attackedP			= config.getBoolean("general.disable_flight.attacked_by_player");
		attackedM			= config.getBoolean("general.disable_flight.attacked_by_mob");
		cooldownPvp			= config.getInt("general.disable_flight.cooldown_pvp", 1) * 20;
		cooldownPve			= config.getInt("general.disable_flight.cooldown_pve", 1) * 20;
		timeDecay			= config.getBoolean("general.time_decay.enabled");
		decayThresh			= config.getInt("general.time_decay.threshold", 3600);
		decayAmount			= config.getDouble("general.time_decay.seconds_lost", 15);
		firstJoinTime		= config.getLong("general.bonus.first_join", 0);
		legacyBonus			= config.getLong("general.bonus.daily_login", 0);
		shop				= config.getBoolean("shop.general.enabled", false);
		
		protCommand			= config.getBoolean("general.damage.flight_disabled");
		protTime			= config.getBoolean("general.damage.out_of_time");
		protCombat			= config.getBoolean("general.damage.combat");
		//permaTimer			= config.getBoolean("general.damage.combat");
		
		actionBar			= config.getBoolean("aesthetic.action_bar.enabled");
		actionProgress		= config.getBoolean("aesthetic.action_bar.progress_bar");
		
		maxY				= config.getInt("general.maximum_height");
		
		double legacyBonus 	= config.getDouble("general.bonus.daily_login");
		if (legacyBonus == 0) {
			
			ConfigurationSection csPerms = config.getConfigurationSection("general.bonus.daily_login");
			if (csPerms != null) {
				for (String key: csPerms.getKeys(false)) {
					double value = config.getDouble("general.bonus.daily_login." + key);
					if (value > 0 && !dailyBonus.containsKey(key)) {
						dailyBonus.put(key, value);
					}
				}
			}	
		}
	}
	
	private static String st(FileType file, String key){
		try{
			switch (file)
			{
			case CONFIG:
				return U.cc(FileHandler.config.getString(key)).replaceAll("\\{PREFIX}", prefix);
			case LANG:
				return U.cc(FileHandler.lang.getString(key)).replaceAll("\\{PREFIX}", prefix);
			default:
				return "";
			}
		} catch (Exception e) {
			U.logW("There is a missing message in the file: (" + file.toString().toLowerCase() + ") | Path: (" + key + ")");
			return U.cc("&cThis message is broken! :(");
		}
	}
	
	
	private static String st(FileType file, String key, String def){
		try{
			switch (file)
			{
			case CONFIG:
				return U.cc(FileHandler.config.getString(key)).replaceAll("\\{PREFIX}", prefix);
			case LANG:
				return U.cc(FileHandler.lang.getString(key)).replaceAll("\\{PREFIX}", prefix);
			default:
				return "";
			}
		} catch (Exception e) {
			U.logW("There is a missing message in the file: (" + file.toString().toLowerCase() + ") | Path: (" + key + ")");
			return U.cc(def);
		}
	}
}
