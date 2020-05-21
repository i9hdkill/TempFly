package com.moneybags.tempfly;

import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.moneybags.tempfly.aesthetic.ActionBarAPI;
import com.moneybags.tempfly.aesthetic.ClipAPI;
import com.moneybags.tempfly.aesthetic.MvdWAPI;
import com.moneybags.tempfly.command.CommandHandle;
import com.moneybags.tempfly.fly.FlyHandle;
import com.moneybags.tempfly.fly.Flyer;
import com.moneybags.tempfly.gui.GuiSession;
import com.moneybags.tempfly.gui.pages.PageShop;
import com.moneybags.tempfly.gui.pages.PageTrails;
import com.moneybags.tempfly.hook.WorldGuardAPI;
import com.moneybags.tempfly.hook.askyblock.AskyblockHook;
import com.moneybags.tempfly.tab.TabHandle;
import com.moneybags.tempfly.util.AutoSave;
import com.moneybags.tempfly.util.FileHandler;
import com.moneybags.tempfly.util.ParticleTask;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

import net.milkbowl.vault.economy.Economy;

public class TempFly extends JavaPlugin {
	
	public static TempFly plugin;
	public static TempFlyAPI tfApi;
	public static AskyblockHook askyblockHook = null;
	public static Economy eco = null;
	
	public static double version;
	
	public static TempFlyAPI getAPI() {
		return tfApi;
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		tfApi = new TempFlyAPI();
		
		FileHandler.createFiles(this);
		ConfigValues.loadValues();
		PageTrails.initialize();
		PageShop.initialize();
		
		WorldGuardAPI.initialize();
		ActionBarAPI.initialize();
		setupEconomy();
		
		FlyHandle.initialize();
		registerListeners();
		registerCommands();
		initializeAesthetics();
		initializeHooks();
		
		new AutoSave().runTaskTimer(this, 0, ConfigValues.save * 20 * 60);
		//Check for bonus time every 30 min
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
               DailyBonusCheck();
            }
        }, 0, 20 * 60 * 30);
		if (ConfigValues.particles) {
			new ParticleTask().runTaskTimer(this, 0, 5);	
		}
		
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (FlyHandle.getFlyer(p) == null) {
				GameMode m = p.getGameMode();
				if ((m.equals(GameMode.CREATIVE)) || (m.equals(GameMode.SPECTATOR))) {
					continue;
				}
				
				p.setFlying(false);
				p.setAllowFlight(false);
				new BukkitRunnable() {
					@Override
					public void run() {
						p.setAllowFlight(false);
						p.setFlying(false);
					}
				}.runTaskLater(TempFly.plugin, 1);
			}
			FlyHandle.regainFlightDisconnect(p);	
		}
	}
	
	private void DailyBonusCheck() {
	    for (Player p : Bukkit.getOnlinePlayers()) {
	        FlyHandle.handleDailyBonus(p);
	    }
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }
	
	private static void initializeHooks() {
		if (FileHandler.config.getBoolean("hooks.askyblock.enable_hook", false)) {
			new AskyblockHook();	
		}
	}
	
	private static void initializeAesthetics() {
		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
			U.logI("Initizlizing MvdwAPI");
			MvdWAPI.initialize();	
		}
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			U.logI("Initizlizing ClipAPI");
			ClipAPI.initialize();	
		}
	}
	
	@Override
	public void onDisable() {
		save();
		for (Player p : Bukkit.getOnlinePlayers()) {
			FlyHandle.addFlightDisconnect(p);
			GuiSession.endAllSessions();
		}
		for (Flyer f: FlyHandle.getFlyers()) {
			f.removeFlyer();
		}
	}
	
	public static boolean oldParticles() {
		String version = Bukkit.getVersion();
		return (version.contains("1.6")) || (version.contains("1.7")) || (version.contains("1.8")) || version.contains("1.9");
	}
	
	private void registerListeners() {
		PluginManager m = getServer().getPluginManager();
		m.registerEvents(new FlyHandle(), this);
		m.registerEvents(new CommandHandle(), this);
		m.registerEvents(new GuiSession.GuiListener(), this);
	}
	
	private void registerCommands() {
		CommandExecutor c = new CommandHandle();
		TabCompleter t = new TabHandle();
		getCommand("tempfly").setExecutor(c);
		getCommand("tempfly").setTabCompleter(t);
	}
	
	public static void save() {
		FlyHandle.save();
	}
	
	public static AskyblockHook getAskyblockHook() {
		return askyblockHook;
	}
	
	public void enableAskyblock(AskyblockHook hook) {
		askyblockHook = hook;
		getServer().getPluginManager().registerEvents(hook, this);
	}
	
}
