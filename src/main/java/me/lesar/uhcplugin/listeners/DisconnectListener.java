package me.lesar.uhcplugin.listeners;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisconnectListener implements Listener {

	private final UHCPlugin plugin;

	public DisconnectListener(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {

		Player player = e.getPlayer();

		plugin.allPlayers.remove(player);
		if(plugin.alivePlayers.remove(player)) {

			if(plugin.alivePlayers.size() == 1) {

				plugin.finishUHC();
				return;

			}

			player.setGameMode(GameMode.SPECTATOR);
			e.setQuitMessage(ChatColor.RED + player.getName() + " has left! " + plugin.alivePlayers.size() + " players remain!");

		}

	}

}
