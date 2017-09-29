package ch.bolt61.minecraft.server;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().log(Level.INFO, "Server Plugin started");
	}
	
	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "Server Plugin stopped");
	}
}
