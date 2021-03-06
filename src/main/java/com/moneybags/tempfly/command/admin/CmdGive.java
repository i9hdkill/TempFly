package com.moneybags.tempfly.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.command.CommandHandle;
import com.moneybags.tempfly.time.TimeHandle;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdGive {

	@SuppressWarnings("deprecation")
	public CmdGive(CommandSender s, String[] args) {
		if (!U.hasPermission(s, "tempfly.give")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		if (args.length < 3) {
			U.m(s, U.cc("&c/tf give [player] [amount-> {args=[-s][-m][-h][-d]}]"));
			return;
		}
		
		OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
		if (p == null || (p != null && !p.isOnline() && !p.hasPlayedBefore())) {
			U.m(s, ConfigValues.invalidPlayer.replaceAll("\\{PLAYER}", args[1]));
			return;
		}
		
		double amount = CommandHandle.quantifyArguments(s, args);
		if (amount == 0) {
			return;
		}
		amount = Math.floor(amount);
		if ((ConfigValues.maxTime > -1) && (TimeHandle.getTime(p.getUniqueId()) + amount >= ConfigValues.maxTime)) {
			U.m(s, TimeHandle.regexString(ConfigValues.timeMaxOther, amount)
					.replaceAll("\\{PLAYER}", p.getName()));
			return;
		}
		TimeHandle.addTime(p.getUniqueId(), amount);
		U.m(s, TimeHandle.regexString(ConfigValues.timeGivenOther, amount)
				.replaceAll("\\{PLAYER}", p.getName()));
		if (p.isOnline()) {
			U.m((Player)p, TimeHandle.regexString(ConfigValues.timeGivenSelf, amount));	
		}
	}
	
	

}
