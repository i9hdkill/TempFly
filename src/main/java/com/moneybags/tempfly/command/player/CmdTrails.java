package com.moneybags.tempfly.command.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.gui.GuiSession;
import com.moneybags.tempfly.gui.pages.PageTrails;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdTrails {

	public CmdTrails(CommandSender s) {
		if (!U.isPlayer(s)) {
			U.m(s, ConfigValues.invalidSender);
			return;
		}
		if (!U.hasPermission(s, "tempfly.trails")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		new PageTrails(GuiSession.newGuiSession((Player)s), 0);
	}
}
