package com.moneybags.tempfly.fly;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.moneybags.tempfly.TempFly;
import com.moneybags.tempfly.hook.WorldGuardAPI;
import com.moneybags.tempfly.time.RelativeTimeRegion;
import com.moneybags.tempfly.time.TimeHandle;
import com.moneybags.tempfly.util.FileHandler;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class FlyHandle implements Listener {

	private static Map<Player, Flyer> flyers = new HashMap<>();
	private static Map<UUID, Boolean> cooldown = new HashMap<>();
	private static Map<UUID, BukkitTask> prot = new HashMap<>();
	
	private static List<RelativeTimeRegion> rtRegions = new ArrayList<>();
	
	private static List<String> blackRegion = new ArrayList<>();
	
	public static void wipe() {
		flyers = new HashMap<>();
	}
	
	public static void initialize() {
		blackRegion = FileHandler.config.contains("general.disabled.regions") ? FileHandler.config.getStringList("general.disabled.regions") : new ArrayList<>();
		ConfigurationSection csRtW = FileHandler.config.getConfigurationSection("general.relative_time.worlds");
		if (csRtW != null) {
			for (String s : csRtW.getKeys(false)) {
				rtRegions.add(new RelativeTimeRegion(
						FileHandler.config.getDouble("general.relative_time.worlds." + s, 1), true, s));
			}
		}
		ConfigurationSection csRtR = FileHandler.config.getConfigurationSection("general.relative_time.regions");
		if (csRtW != null) {
			for (String s : csRtR.getKeys(false)) {
				rtRegions.add(new RelativeTimeRegion(
						FileHandler.config.getDouble("general.relative_time.regions." + s, 1), false, s));
			}
		}
	}
	
	public static List<RelativeTimeRegion> getRtRegions() {
		return rtRegions;
	}
	
	public static void save() {
		FileConfiguration data = FileHandler.data;
		for (Flyer f: flyers.values()) {
			String path = "players." + f.getPlayer().getUniqueId().toString() + ".time";
			double time = f.getTime();	
			data.set(path, time);
		}
		FileHandler.saveData();
	}
	
	public static void save(Flyer f) {
		FileConfiguration data = FileHandler.data;
		String path = "players." + f.getPlayer().getUniqueId().toString() + ".time";
		double time = f.getTime();
		data.set(path, time);
		FileHandler.saveData();
	}
	
	public static void addDamageProtection(Player p) {
		UUID u = p.getUniqueId();
		prot.put(u,
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (prot.containsKey(u)) {
					prot.remove(u);
				}
				
			}
		}.runTaskLater(TempFly.plugin, 120));
	}
	
	public static void removeDamageProtction(Player p) {
		UUID u = p.getUniqueId();
		if (prot.containsKey(u)) {
			prot.get(u).cancel();
			prot.remove(u);
		}
	}
	
	public static void addFlyer(Player p) {
		flyers.put(p, new Flyer(p));
	}
	
	public static void removeFlyer(Player p) {
		if (flyers.containsKey(p)) {
			Flyer f = flyers.get(p);
			save(f);
			f.removeFlyer();
			flyers.remove(p);
		}
	}
	
	public static Flyer getFlyer(Player p) {
		return flyers.containsKey(p) ? flyers.get(p) : null;
	}
	
	public static Flyer[] getFlyers() {
		return flyers.values().toArray(new Flyer[flyers.size()]);
	}
	
	public static boolean flyAllowed(Location loc) {
		String world = loc.getWorld().getName();
		for (String w: ConfigValues.disabledWorlds) {
			if (w.equals(world)) {
				return false;
			}
		}
		
		if (WorldGuardAPI.isEnabled()) {
			ApplicableRegionSet prot = WorldGuardAPI.getRegionSet(loc);
			if (prot != null) {
				for(ProtectedRegion r : prot) {
					if (blackRegion.contains(r.getId())) {
						return false;
					}
				}	
			}
		}			
		
		return true;
	}
	
	public static boolean onCooldown(Player p) {
		return cooldown.containsKey(p.getUniqueId());
	}
	
	public static void addCooldown(Player p, int time, boolean flying) {
		UUID u = p.getUniqueId();
		if (cooldown.containsKey(u)) {
			return;
		}
		cooldown.put(u, flying);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (cooldown.containsKey(u)) {
					if (cooldown.get(u).equals(true)) {
						Player n = Bukkit.getPlayer(u);
						if (n != null) {
							addFlyer(p);
							U.m(n, ConfigValues.flyCooldownOver);
						}
					}
					cooldown.remove(u);
				}
			}
		}.runTaskLater(TempFly.plugin, time);
	}
	
	public static void disableFlyer(Flyer f) {
		removeFlyer(f.getPlayer());
		f.removeFlyer();
		U.m(f.getPlayer(), ConfigValues.flyDisabledSelf);
	}
	
	public static String getPlaceHolder(Player p, Placeholder type) {
		Flyer f = getFlyer(p);
		double supply;
		if (f != null) {
			supply = f.getTime();
		} else {
			supply = TimeHandle.getTime(p.getUniqueId());
		}
		switch (type) {
		case TIME_FORMATTED:
		{
			long days = TimeHandle.formatTime(TimeUnit.DAYS, supply);
			long hours = TimeHandle.formatTime(TimeUnit.HOURS, supply);
			long minutes = TimeHandle.formatTime(TimeUnit.MINUTES, supply);
			long seconds = TimeHandle.formatTime(TimeUnit.SECONDS, supply);
			String s = "";
			boolean i = U.hasPermission(p, "tempfly.time.infinite");
			if (days > 0) {
				s = s.concat(ConfigValues.fbDays.replaceAll("\\{DAYS}", i ? String.valueOf(ConfigValues.infinity) : String.valueOf(days)));
			}
			if (hours > 0) {
				s = s.concat(ConfigValues.fbHours.replaceAll("\\{HOURS}", i ? String.valueOf(ConfigValues.infinity) : String.valueOf(hours)));
			}
			if (minutes > 0) {
				s = s.concat(ConfigValues.fbMinutes.replaceAll("\\{MINUTES}", i ? String.valueOf(ConfigValues.infinity) : String.valueOf(minutes)));
			}
			if (seconds > 0) {
				s = s.concat(ConfigValues.fbSeconds.replaceAll("\\{SECONDS}", i ? String.valueOf(ConfigValues.infinity) : String.valueOf(seconds)));
			}
			return s;
		}
		case TIME_DAYS:
		{
			long days = TimeHandle.formatTime(TimeUnit.DAYS, supply);
			return String.valueOf(days);
		}
		case TIME_HOURS:
		{
			long hours = TimeHandle.formatTime(TimeUnit.HOURS, supply);
			return String.valueOf(hours);
		}
		case TIME_MINUTES:
		{
			long minutes = TimeHandle.formatTime(TimeUnit.MINUTES, supply);
			return String.valueOf(minutes);
		}
		case TIME_SECONDS:
		{
			long seconds = TimeHandle.formatTime(TimeUnit.SECONDS, supply);
			return String.valueOf(seconds);
		}
		default:
			break;
		}
		return "broken message";
	}
	
	
	public static enum Placeholder {
		TIME_FORMATTED,
		TIME_DAYS,
		TIME_HOURS,
		TIME_MINUTES,
		TIME_SECONDS;
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
		if (from.getBlock().equals(to.getBlock())) {
			return;
		}
		Player p = e.getPlayer();
		Flyer f = getFlyer(p);
		if (f != null) {
			f.asessRtRegions();
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void on(PlayerTeleportEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
		if (from.getBlock().equals(to.getBlock())) {
			return;
		}
		Player p = e.getPlayer();
		Flyer f = getFlyer(p);
		if (f != null) {
			f.asessRtRegions();
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void on(PlayerRespawnEvent e) {
		Location from = e.getPlayer().getLocation();
		Location to = e.getRespawnLocation();
		Player p = e.getPlayer();
		Flyer f = getFlyer(p);
		if (f != null) {
			f.asessRtRegions();
			if (!from.getWorld().equals(to.getWorld())) {
				f.asessRtWorlds();
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		if (!flyers.containsKey(p)) {
			return;
		}
		
		Flyer f = getFlyer(p);
		if (!flyAllowed(p.getLocation())) {
			disableFlyer(f);
		}
		f.asessRtWorlds();
		
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(PlayerGameModeChangeEvent e) {
		Player p = e.getPlayer();
		if (!flyers.containsKey(p)) {
			return;
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				p.setAllowFlight(true);
				p.setFlying(true);
			}
		}.runTaskLater(TempFly.plugin, 1);
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		
		if (ConfigValues.timeDecay && p.hasPlayedBefore()) {
			long offline = (System.currentTimeMillis() - p.getLastPlayed()) / 1000;
			double lost = (offline / ConfigValues.decayThresh) * ConfigValues.decayAmount;
			double time = TimeHandle.getTime(p.getUniqueId());
			if (lost > time) {
				lost = time;
			}
			if (lost > 0) {
				TimeHandle.removeTime(p.getUniqueId(), lost);
				U.m(p, TimeHandle.regexString(ConfigValues.timeDecayLost, lost));	
			}
		}
		
		
		if (!p.hasPlayedBefore() && ConfigValues.firstJoinTime > 0) {
			TimeHandle.addTime(p.getUniqueId(), ConfigValues.firstJoinTime);
			U.m(p, TimeHandle.regexString(ConfigValues.firstJoin, ConfigValues.firstJoinTime));
		}

		handleDailyBonus(p);
		
		GameMode m = p.getGameMode();
		if (!(m.equals(GameMode.CREATIVE)) && !(m.equals(GameMode.SPECTATOR))) {
			p.setFlying(false);
			p.setAllowFlight(false);
		}
		
		DateFormat.getDateInstance().format(0);
		regainFlightDisconnect(p);
	}
	
	public static void handleDailyBonus(Player player) {
	    long currentTime = System.currentTimeMillis();
	    long lastDailyBonus = TimeHandle.getLastDailyTimeBonusTime(player.getUniqueId());
	    long elapsedTime = currentTime - lastDailyBonus;
	    final long dayInMs = 3600000;
        if (elapsedTime > dayInMs) {
            if (ConfigValues.legacyBonus > 0) {
                TimeHandle.addTime(player.getUniqueId(), ConfigValues.legacyBonus);
                U.m(player, TimeHandle.regexString(ConfigValues.dailyLogin, ConfigValues.legacyBonus));  
            } else if (ConfigValues.dailyBonus.size() > 0) {
                double time = 0;
                
                for (Entry<String, Double> entry: ConfigValues.dailyBonus.entrySet()) {
                    if (player.hasPermission("tempfly.bonus." + entry.getKey())) {
                        time += entry.getValue();
                    }
                }
                
                if (time > 0) {
                    TimeHandle.addDailyTime(player.getUniqueId(), time);
                    U.m(player, TimeHandle.regexString(ConfigValues.dailyLogin, time));  
                }
            }
        }
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (getFlyer(p) != null) {
			addFlightDisconnect(p);
			removeFlyer(p);
		}
	}
	
	public static void addFlightDisconnect(Player p) {
		if (!flyers.containsKey(p)) 
			return;
		FileHandler.data.set("players." + p.getUniqueId() + ".logged_in_flight", true);
		FileHandler.saveData();
	}
	
	public static void regainFlightDisconnect(Player p) {
		if (!flyers.containsKey(p)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					GameMode m = p.getGameMode();
					if (!(m.equals(GameMode.CREATIVE)) && !(m.equals(GameMode.SPECTATOR))) {
						p.setFlying(false);
						p.setAllowFlight(false);
					}
					if (FileHandler.data.getBoolean("players." + p.getUniqueId() + ".logged_in_flight")) {
						if ((TimeHandle.getTime(p.getUniqueId()) > 0) && (!p.isOnGround())) {
							addFlyer(p);	
						}
						FileHandler.data.set("players." + p.getUniqueId() + ".logged_in_flight", false);
						FileHandler.saveData();
					}
				}
			}.runTaskLater(TempFly.plugin, 1);
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(PlayerMoveEvent e) {
		if (e.getFrom().getBlock().equals(e.getTo().getBlock())) {
			return;
		}
		Player p = e.getPlayer();
		if (!flyers.containsKey(p)) {
			return;
		}
		Flyer f = getFlyer(p);
		f.resetIdleTimer();
		if (p.getLocation().getBlockY() > ConfigValues.maxY) {
			p.setFlying(false);
		}
		
		if (WorldGuardAPI.isEnabled()) {
			ApplicableRegionSet prot = WorldGuardAPI.getRegionSet(e.getTo());
			if (prot != null) {
				for(ProtectedRegion r : prot) {
					if (blackRegion.contains(r.getId())) {
						removeFlyer(p);
					}
					if (TempFly.getAskyblockHook() != null) {
						TempFly.getAskyblockHook().checkFlightRequirement(p, p.getLocation());
					}
				}	
			}
		}
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!flyers.containsKey(p)) {
			return;
		}
		flyers.get(p).resetIdleTimer();
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (!flyers.containsKey(p)) {
			return;
		}
		flyers.get(p).resetIdleTimer();
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player)e.getWhoClicked();
		if (!flyers.containsKey(p)) {
			return;
		}
		flyers.get(p).resetIdleTimer();
	}
	
	public static enum CombatType {
		PLAYER_ATTACKS_FLYER(true),
		MOB_ATTACKS_FLYER(false),
		FLYER_ATTACKS_PLAYER(true),
		FLYER_ATTACKS_MOB(false);
		
		private boolean pvp;
		
		private CombatType(boolean pvp) {
			this.pvp = pvp;
		}
		
		public boolean isPvp() {
			return pvp;
		}
	}
	
	public static void processCombat(Entity vic, Entity act) {
		if (act instanceof Arrow) {
			if (!(((Arrow)act).getShooter() instanceof Entity)) {
				return;
			}
			act = (Entity) ((Arrow)act).getShooter();
		}
		if (vic instanceof Player) {
			if (act instanceof Player) {
				onCombat(CombatType.PLAYER_ATTACKS_FLYER, vic, act);
				onCombat(CombatType.FLYER_ATTACKS_PLAYER, vic, act);
			} else if (act instanceof LivingEntity) {
				onCombat(CombatType.MOB_ATTACKS_FLYER, vic, act);
			}
		} else if (vic instanceof LivingEntity) {
			if (act instanceof Player) {
				onCombat(CombatType.FLYER_ATTACKS_MOB, vic, act);
			}
		}
	}		
	
	public static void onCombat(CombatType type, Entity vic, Entity act) {
		if (!combatDisable(type)) {
			return;
		}
		
		Player p = (type == CombatType.FLYER_ATTACKS_MOB || type == CombatType.FLYER_ATTACKS_PLAYER) ? (Player)act : (Player)vic;
		Flyer f = getFlyer(p);
		
		if (f != null) {
			if (!ConfigValues.protCombat) {
				FlyHandle.addDamageProtection(p);
			}
			removeFlyer(p);
			f.removeFlyer();
			U.m(p, ConfigValues.flyDisabledSelf);	
		}
		addCooldown(p, type.isPvp() ? ConfigValues.cooldownPvp : ConfigValues.cooldownPve, f != null);
	}
	
	public static boolean combatDisable(CombatType type) {
		switch (type) {
		case FLYER_ATTACKS_MOB:
			return ConfigValues.attackM;
		case FLYER_ATTACKS_PLAYER:
			return ConfigValues.attackP;
		case MOB_ATTACKS_FLYER:
			return ConfigValues.attackedM;
		case PLAYER_ATTACKS_FLYER:
			return ConfigValues.attackedP;
		}
		return false;
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent e) {
		Entity vic = e.getEntity();
		Entity act = e.getDamager();
		processCombat(vic, act);
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void on(EntityDamageEvent e) {
		Entity vic = e.getEntity();
		if (!(vic instanceof Player)) {
			return;
		}
		Player p = (Player)vic;
		UUID u = p.getUniqueId();
		if (!prot.containsKey(u)) {
			return;
		}
		DamageCause cause = e.getCause();
		if (!cause.equals(DamageCause.FALL)) {
			return;
		}
		e.setCancelled(true);
		removeDamageProtction(p);
	}
}
