package com.moneybags.tempfly.hook.askyblock;

import java.util.ArrayList;
import java.util.List;

import com.moneybags.tempfly.util.FileHandler;

public class IslandRequirements {

	private List<String> challenges = new ArrayList<>();
	private long islandLevel;
	private String name;
	
	public IslandRequirements(String path, String name) {
		challenges = FileHandler.config.getStringList(path + ".challenges");
		islandLevel = FileHandler.config.getLong(path + ".island_level", 0);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public long getRequiredLevel() {
		return islandLevel;
	}
	
	public List<String> getRequiredChallenges() {
		return challenges;
	}
	
}
