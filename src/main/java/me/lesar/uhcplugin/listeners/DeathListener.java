package me.lesar.uhcplugin.listeners;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

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
				plugin.finishUHC();
				return;

			}

			Player killer = player.getKiller();
			if(killer != null) {

				e.setDeathMessage(ChatColor.RED + player.getName() + " has been eliminated by " + killer.getName() + "! " + plugin.alivePlayers.size() + " players remain!");

//				killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 0));
				killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));

			} else e.setDeathMessage(ChatColor.RED + player.getName() + " has been eliminated! " + plugin.alivePlayers.size() + " players remain!");

		}

	}

}
