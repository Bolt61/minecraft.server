package ch.bolt61.minecraft.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MySQL {
	private String host, database, user, password;
	private int port;
	private Connection conn;
	
	private ExecutorService executor;
	private Plugin plugin;
	
	public MySQL(Plugin plugin, String host, String database, String user, String password, int port) {
		this.plugin = plugin;
		this.executor = Executors.newCachedThreadPool();
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		this.port = port;
	}
	
	public void connect() {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
			plugin.getLogger().log(Level.INFO, "MySQL connected!");
		} catch (SQLException e) {
			plugin.getLogger().log(Level.WARNING, "MySQL konnte keine Verbindung aufbauen!");
		}
	}
	
	public void closeConnection() {
		if(isConnected()) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				conn = null;
			}
		}
	}

	public void update(PreparedStatement stmt) {
		if(isConnected()) {
			executor.execute(() -> queryUpdate(stmt));
		}
	}
	
	public void update(String statement) {
		if(isConnected()) {
			executor.execute(() -> queryUpdate(statement));
		}
	}
	
	public void query(PreparedStatement stmt, Consumer<ResultSet> consumer) {
		if(isConnected()) {
			executor.execute(() -> {
				ResultSet rs = query(stmt);
				Bukkit.getScheduler().runTask(plugin, () -> {
					consumer.accept(rs);
				});
			});
		}
	}
	
	public void query(String statement, Consumer<ResultSet> consumer) {
		if(isConnected()) {
			executor.execute(() -> {
				ResultSet rs = query(statement);
				Bukkit.getScheduler().runTask(plugin, () -> {
					consumer.accept(rs);
				});
			});
		}
	}
	
	private void queryUpdate(String query) {
		if(isConnected()) {
			try (PreparedStatement statement = conn.prepareStatement(query)){
				queryUpdate(statement);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void queryUpdate(PreparedStatement stmt) {
		if(isConnected()) {
			try {
				stmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private ResultSet query(String query) {
		if(isConnected()) {
			try {
				return query(conn.prepareStatement(query));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private ResultSet query(PreparedStatement stmt) {
		if(isConnected()) {
			try {
				return stmt.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private boolean isConnected() {
		try {
			if(conn == null || !conn.isValid(10) || conn.isClosed()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
