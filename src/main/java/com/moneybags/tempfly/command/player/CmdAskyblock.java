package com.moneybags.tempfly.command.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.TempFly;
import com.moneybags.tempfly.gui.GuiSession;
import com.moneybags.tempfly.hook.askyblock.PageAskyblock;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.ConfigValues;

public class CmdAskyblock {

	public CmdAskyblock(CommandSender s, String[] args) {
		if (!U.isPlayer(s)) {
			U.m(s, ConfigValues.invalidSender);
			return;
		}
		Player p = (Player) s;
		if (TempFly.getAskyblockHook() == null) {
			U.m(s, ConfigValues.invalidCommand);
			return;
		} else if (!p.hasPermission("tempfly.askyblock.panel")) {
			U.m(s, ConfigValues.invalidPermission);
			return;
		}
		new PageAskyblock(GuiSession.newGuiSession(p));
	}
	
}
