package com.moneybags.tempfly.command.player;

import org.bukkit.command.CommandSender;

import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdHelp {

	public CmdHelp(CommandSender s, String[] args) {
		if (!U.hasPermission(s, "tempfly.help")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		for (String line: ConfigValues.help) {
			U.m(s, line);
		}
		if (U.hasPermission(s, "tempfly.help.admin")) {
			for (String line: ConfigValues.helpExtended) {
				U.m(s, line);
			}
		}
	}

}
