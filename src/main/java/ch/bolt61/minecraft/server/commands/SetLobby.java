package ch.bolt61.minecraft.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.bolt61.minecraft.server.Main;
import ch.bolt61.minecraft.server.tools.LocationUtilities;

public class SetLobby implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) {
			sender.sendMessage(Main.getInstance().getConfig().getString("Messages.no_console").replace('&', '§'));
			return true;
		}
		
		Player p = (Player) sender;
		LocationUtilities.setLocation("lobby", p.getLocation());
		p.sendMessage(Main.getInstance().getConfig().getString("Messages.position.lobby").replace('&', '§'));
		
		return true;
	}
}
