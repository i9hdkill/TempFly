package com.moneybags.tempfly.command.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.TempFly;
import com.moneybags.tempfly.gui.GuiSession;
import com.moneybags.tempfly.gui.pages.PageShop;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdShop {

	public CmdShop(CommandSender s) {
		if (!ConfigValues.shop || TempFly.eco == null) {
			U.m(s, ConfigValues.invalidCommand);
			return;
		}
		if (!U.isPlayer(s)) {
			U.m(s, ConfigValues.invalidSender);
			return;
		}
		if (!U.hasPermission(s, "tempfly.shop")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		new PageShop(GuiSession.newGuiSession((Player)s), 0);
	}
}
