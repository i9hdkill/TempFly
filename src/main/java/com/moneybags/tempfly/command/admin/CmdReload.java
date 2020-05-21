package com.moneybags.tempfly.command.admin;

import org.bukkit.command.CommandSender;

import com.moneybags.tempfly.TempFly;
import com.moneybags.tempfly.util.FileHandler;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdReload {

	public CmdReload(CommandSender s) {
		if (!U.hasPermission(s, "tempfly.reload")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		U.m(s, ConfigValues.reload);
		FileHandler.createFiles(TempFly.plugin);
		ConfigValues.loadValues();
	}
}
