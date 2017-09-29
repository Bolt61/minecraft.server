package ch.bolt61.minecraft.server.tools;

import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ch.bolt61.minecraft.server.Main;

public class LocationUtilities {

	public static void teleport(String location, Player p, Consumer<Boolean> consumer) {
		getLocation(location, position -> {
			if(position == null) {
				consumer.accept(false);
			} else {
				p.teleport(position);
				consumer.accept(true);
			}
		});
	}
	
	public static void teleport(String location, Player p) {
		getLocation(location, position -> {
			if(position != null) {
				p.teleport(position);
			}
		});
	}

	public static void setLocation(String location, Location loc) {
		getLocation(location, position -> {
			if(position == null) {
				Main.getInstance().getMysql().update("INSERT INTO locations (name, world, x, y, z, yaw, pitch) VALUES ('" + location + "', '" + 
						loc.getWorld().getName() + "', '" + loc.getX() + "', '" + loc.getY() + "', '" + loc.getZ() + "', '" + 
						loc.getYaw() + "', '" + loc.getPitch() + "')");
			} else {
				Main.getInstance().getMysql().update("UPDATE locations SET world='" + loc.getWorld().getName() + "', x='" + loc.getX() + "', y='" + 
						loc.getY() + "', z='" + loc.getZ() + "', yaw='" + loc.getYaw() + "', pitch='" + loc.getPitch() + "'");
			}
		});
	}
	
	public static void delete(String location) {
		Main.getInstance().getMysql().update("DELETE * FROM locations WHERE name='" + location + "'");
	}
	
	public static void getLocation(String location, Consumer<Location> consumer) {
		Main.getInstance().getMysql().query("SELECT * FROM locations WHERE name='" + location + "'", rs -> {
			Location loc = null;
			try {
				if(rs.next()) {
					String world = rs.getString("world");
					double x = rs.getDouble("x");
					double y = rs.getDouble("y");
					double z = rs.getDouble("z");
					float yaw = rs.getFloat("yaw");
					float pitch = rs.getFloat("pitch");
					
					loc = new Location(Bukkit.getWorld(world), x, y, z);
					loc.setYaw(yaw);
					loc.setPitch(pitch);
				}
			} catch (Exception e) {
				Main.getInstance().getLogger().log(Level.WARNING, "Could not load location " + location);
			} finally {
				consumer.accept(loc);
			}
		});
	}
}
