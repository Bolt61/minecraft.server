package ch.bolt61.minecraft.server.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ch.bolt61.minecraft.server.Main;

public class PlayerQuit implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		e.setQuitMessage(Main.getInstance().getConfig().getString("Messages.quit").replace('&', '§').replace("[player]", p.getDisplayName()));
	}
}
