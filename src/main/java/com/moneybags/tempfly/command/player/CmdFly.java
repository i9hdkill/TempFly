package com.moneybags.tempfly.command.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.event.FlightEnabledEvent;
import com.moneybags.tempfly.fly.FlyHandle;
import com.moneybags.tempfly.fly.Flyer;
import com.moneybags.tempfly.time.TimeHandle;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdFly {

	public CmdFly(CommandSender s, String[] args) {
		Player p = null;
		boolean
			toggle = false,
			toggleVal = false;
		
		
		
		if (args.length > 1) {
			
			p = Bukkit.getPlayer(args[1]);
			if (p != null) {
				if (!U.hasPermission(s, "tempfly.toggle.other")) {
					U.m(s, ConfigValues.invalidPermission);
					return;
				}
				
				if (args.length > 2) {
					toggle = true;
					if (args[2].equalsIgnoreCase("on")) {
						toggleVal = true;
					} else if (args[2].equalsIgnoreCase("off")) {
						toggleVal = false;
					} else {
						U.m(s, U.cc("&c/tf toggle [player] [on / off]"));
						return;
					}
				}
				
			} else if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
				if (!U.isPlayer(s)) {
					U.m(s, ConfigValues.invalidSender);
					return;
				}
				if (!U.hasPermission(s, "tempfly.toggle.self")) {
					U.m(s, ConfigValues.invalidPermission);
					return;
				}
				
				toggle = true;
				if (args[1].equalsIgnoreCase("on")) {
					toggleVal = true;
				} else if (args[1].equalsIgnoreCase("off")) {
					toggleVal = false;
				}
				
			} else if (args.length > 2) {
				U.m(s, ConfigValues.invalidPlayer.replaceAll("\\{PLAYER}", args[1]));
				return;
			} else {
				U.m(s, U.cc("&c/tf toggle [on / off]"));
				return;
			}
		
			
		} else {
			if (!U.isPlayer(s)) {
				U.m(s, ConfigValues.invalidSender);
				return;
			}
			if (!U.hasPermission(s, "tempfly.toggle.self")) {
				U.m(s, ConfigValues.invalidPermission);
				return;
			}
		}
		if (p == null) {
			p = (Player)s;
		}
		
		
		Flyer f = FlyHandle.getFlyer(p);
		if (toggle && toggleVal || !toggle && !toggleVal && f == null) {
			if (f != null) {
				return;
			}
			double time = TimeHandle.getTime(p.getUniqueId());
			if ((time <= 0) && (!p.hasPermission("tempfly.time.infinite"))) {
				if (!s.equals(p)) {
					U.m(s, ConfigValues.invalidTimeOther.replaceAll("\\{PLAYER}", p.getName()));	
				} else {
					U.m(s, ConfigValues.invalidTimeSelf);
				}
				return;
			}
			if (!FlyHandle.flyAllowed(p.getLocation())) {
				if (!s.equals(p)) {
					U.m(s, ConfigValues.invalidZoneOther.replaceAll("\\{PLAYER}", p.getName()));
					return;
				} else {
					U.m(s, ConfigValues.invalidZoneSelf);
					return;
				}	
			}
			if ((FlyHandle.onCooldown(p)) && (args.length < 1)) {
				U.m(s, ConfigValues.flyCooldownDeny);
				return;
			}
			FlightEnabledEvent e = new FlightEnabledEvent(p);
			Bukkit.getPluginManager().callEvent(e);
			if (e.isCancelled()) {
				return;
			}
			FlyHandle.removeDamageProtction(p);
			FlyHandle.addFlyer(p);
			U.m(p, ConfigValues.flyEnabledSelf);
			if (!s.equals(p)) {
				U.m(s, ConfigValues.flyEnabledOther.replaceAll("\\{PLAYER}", p.getName()));	
			}
		} else if (toggle && !toggleVal && f != null || !toggle && f != null) {
			if (!ConfigValues.protCommand) {
				FlyHandle.addDamageProtection(p);	
			}
			FlyHandle.removeFlyer(p);
			U.m(p, ConfigValues.flyDisabledSelf);
			if (!s.equals(p)) {
				U.m(s, ConfigValues.flyDisabledOther.replaceAll("\\{PLAYER}", p.getName()));	
			}
		}
	}
}
