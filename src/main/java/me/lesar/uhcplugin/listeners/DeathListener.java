package me.lesar.uhcplugin.listeners;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

	private final UHCPlugin plugin;

	public DeathListener(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {

		if(!plugin.getActive()) return;

		Player player = e.getPlayer();
		if(plugin.alivePlayers.remove(player)) {

			player.setGameMode(GameMode.SPECTATOR);

			Location lightningLocation = player.getLocation();
			lightningLocation.setY(lightningLocation.getY() + 7f);
			player.getWorld().spawnEntity(lightningLocation, EntityType.LIGHTNING);



			if(plugin.alivePlayers.size() == 1) {

				e.setCancelled(true);

				Player lastPlayer = (Player) plugin.alivePlayers.toArray()[0];
				for(Player uhcPlayer : plugin.allPlayers)
					uhcPlayer.sendTitle(ChatColor.GOLD + lastPlayer.getName(), ChatColor.GOLD + "has won the UHC!");

				lastPlayer.setGameMode(GameMode.CREATIVE);

				plugin.stopUHC();

			}

			e.setDeathMessage(player.getName() + " has been eliminated! " + plugin.alivePlayers.size() + " players remain!");

		}

	}

}