package ch.bolt61.minecraft.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ch.bolt61.minecraft.server.commands.SetLobby;
import ch.bolt61.minecraft.server.events.PlayerJoin;
import ch.bolt61.minecraft.server.events.PlayerQuit;
import ch.bolt61.minecraft.server.mysql.MySQL;
import ch.bolt61.minecraft.server.permissions.Rank;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private MySQL mysql;
	
	private List<Rank> ranks;
	
	@Override
	public void onEnable() {
		getLogger().log(Level.INFO, "Server Plugin started");
		Main.instance = this;
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		
		getCommand("setlobby").setExecutor(new SetLobby());
		
		loadConfig();
		
		loadMysql();
		
		loadRanks();
	}
	
	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "Server Plugin stopped");
		mysql.closeConnection();
	}
	
	private void loadRanks() {
		this.ranks = new ArrayList<>();
		
		mysql.query("SELECT * FROM ranks", rs -> {
			try {
				while(rs.next()) {
					int id = rs.getInt("id");
					String name = rs.getString("name");
					
					ranks.add(new Rank(id, name));
					
					mysql.query("SELECT * FROM permissions WHERE id='" + id + "'", result -> {
						try {
							while(result.next()) {
								String perm = result.getString("permission");
								Rank r = getByID(id);
								r.addPermission(perm);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private void loadMysql() {
		this.mysql = new MySQL(this, getConfig().getString("MySQL.host"), getConfig().getString("MySQL.database"), 
				getConfig().getString("MySQL.user"), getConfig().getString("MySQL.password"), getConfig().getInt("MySQL.port"));
		mysql.connect();
		
		String tLocations = "CREATE TABLE IF NOT EXISTS locations (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(20), world VARCHAR(20), "
				+ "x DECIMAL (10, 4), y DECIMAL (10, 4), z DECIMAL (10, 4), yaw DECIMAL (10, 4), pitch DECIMAL (10, 4))";
		mysql.update(tLocations);
		
		String tRanks = "CREATE TABLE IF NOT EXISTS ranks (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(20))";
		mysql.update(tRanks);
		
		String tPermissions = "CREATE TABLE IF NOT EXISTS permissions (id INT, permission VARCHAR(20), PRIMARY KEY(id, permission))";
		mysql.update(tPermissions);
	}
	
	private void loadConfig() {
		saveDefaultConfig();
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public MySQL getMysql() {
		return mysql;
	}
	
	public Rank getByID(int id) {
		for(Rank r : ranks) {
			if(r.getId() == id) {
				return r;
			}
		}
		return null;
	}
}
