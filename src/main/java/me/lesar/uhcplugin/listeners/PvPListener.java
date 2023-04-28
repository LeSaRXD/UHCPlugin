package me.lesar.uhcplugin.listeners;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPListener implements Listener {

	private final UHCPlugin plugin;

	public PvPListener(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

		if(!plugin.getPvPDisabled()) return;

		Bukkit.getLogger().info(e.getDamager().getName());

		if(e.getDamager() instanceof Player attacker && e.getEntity() instanceof Player receiver) {

			Bukkit.getLogger().info("Cancelled damage from " + attacker.getName() + " to " + receiver.getName());
			if(plugin.alivePlayers.contains(attacker) && plugin.alivePlayers.contains(receiver)) e.setCancelled(true);

		}

	}

}
