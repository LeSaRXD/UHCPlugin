package me.lesar.uhcplugin.listeners;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DisconnectListener implements Listener {

	private final UHCPlugin plugin;

	public DisconnectListener(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {

		playerLeave(e.getPlayer());

	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {

		if(e.getTo().getWorld() == plugin.uhcWorld) return;

		playerLeave(e.getPlayer());

	}

	private void playerLeave(Player player) {

		plugin.cancelStartTimer();

		plugin.allPlayers.remove(player);
		if(plugin.alivePlayers.remove(player)) {

			if(plugin.alivePlayers.size() <= 1) plugin.finishUHC();
			else for(Player other : plugin.allPlayers) other.sendMessage(ChatColor.RED + player.getName() + " has left! " + plugin.alivePlayers.size() + " players remain!");

			player.sendMessage(ChatColor.RED + "You have left the UHC");

		}

	}

}
