package com.moneybags.tempfly.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.command.CommandHandle;
import com.moneybags.tempfly.time.TimeHandle;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdSet {

	@SuppressWarnings("deprecation")
	public CmdSet(CommandSender s, String[] args) {
		if (!U.hasPermission(s, "tempfly.set")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		if (args.length < 3) {
			U.m(s, U.cc("&c/time set [player] [amount-> {args=[-s][-m][-h][-d]}]"));
			return;
		}
		
		OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
		if (p == null || (p != null && !p.isOnline() && !p.hasPlayedBefore())) {
			U.m(s, ConfigValues.invalidPlayer.replaceAll("\\{PLAYER}", args[1]));
			return;
		}
		
		double amount = CommandHandle.quantifyArguments(s, args);
		if ((ConfigValues.maxTime > -1) && (amount > ConfigValues.maxTime)) {
			amount = ConfigValues.maxTime;
		}
		amount = Math.floor(amount);
		TimeHandle.setTime(p.getUniqueId(), amount);
		U.m(s, TimeHandle.regexString(ConfigValues.timeSetOther, amount)
				.replaceAll("\\{PLAYER}", p.getName()));
		if (p.isOnline()) {
			U.m((Player)p, TimeHandle.regexString(ConfigValues.timeSetSelf, amount));	
		}
	}
	
}
