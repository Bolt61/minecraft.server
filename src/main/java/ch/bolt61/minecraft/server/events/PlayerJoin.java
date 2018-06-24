package ch.bolt61.minecraft.server.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ch.bolt61.minecraft.server.Main;
import ch.bolt61.minecraft.server.tools.LocationUtilities;

public class PlayerJoin implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage(Main.getInstance().getConfig().getString("Messages.join").replace('&', '§').replace("[player]", p.getDisplayName()));
		
		LocationUtilities.teleport("lobby", p);
	}
}
