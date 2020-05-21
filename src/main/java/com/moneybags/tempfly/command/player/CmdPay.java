package com.moneybags.tempfly.command.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.command.CommandHandle;
import com.moneybags.tempfly.time.TimeHandle;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdPay {

	@SuppressWarnings("deprecation")
	public CmdPay(CommandSender s, String[] args) {
		if (!ConfigValues.payable) {
			U.m(s, ConfigValues.invalidCommand);
			return;
		}
		if (!U.isPlayer(s)) {
			U.m(s, ConfigValues.invalidSender);
			return;
		}
		if (!U.hasPermission(s, "tempfly.pay")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		if (args.length < 3) {
			U.m(s, U.cc("&c/tf pay [player] [amount-> {args=[-s][-m][-h][-d]}]"));
			return;
		}
		OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
		if (p == null || (p != null && !p.isOnline() && !p.hasPlayedBefore())) {
			U.m(s, ConfigValues.invalidPlayer.replaceAll("\\{PLAYER}", args[1]));
			return;
		}
		
		if ((Player)s == p) {
			U.m(s, ConfigValues.invalidReciever);
			return;
		}
		double amount = 0;
		amount = CommandHandle.quantifyArguments(s, args);
		if (amount <= 0) {
			return;
		}
		amount = Math.floor(amount);
		Player sender = (Player)s;
		double bal = TimeHandle.getTime(sender.getUniqueId());
		if (bal < amount) {
			U.m(s, ConfigValues.invalidTimeSelf);
			return;
		}
		if ((ConfigValues.maxTime > -1) && (TimeHandle.getTime(p.getUniqueId()) + amount >= ConfigValues.maxTime)) {
			U.m(s, TimeHandle.regexString(ConfigValues.timeMaxOther, amount)
					.replaceAll("\\{PLAYER}", p.getName()));
			return;
		}
		
		TimeHandle.removeTime(sender.getUniqueId(), amount);
		TimeHandle.addTime(p.getUniqueId(), amount);
		U.m(s, TimeHandle.regexString(ConfigValues.timeSentOther, amount)
				.replaceAll("\\{PLAYER}", p.getName()));
		if (p.isOnline()) {
			U.m((Player)p, TimeHandle.regexString(ConfigValues.timeSentSelf, amount)
					.replaceAll("\\{PLAYER}", s.getName()));	
		}
	}

}
