package me.lesar.uhcplugin.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

	private final UHCPlugin plugin;
	private final MultiverseCore mvCore;

	public ConnectionListener(UHCPlugin plugin, MultiverseCore mvCore) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		this.mvCore = mvCore;

	}



	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {

		plugin.playerLeaveAutomatedUHC(e.getPlayer());

	}

	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {

		String uhcWorldName = plugin.getConfig().getString("auto.world_name");

		World world = e.getPlayer().getWorld();

		if(world.getName().equals(uhcWorldName))
			plugin.playerJoinAutomatedUHC(e.getPlayer());
		else plugin.playerLeaveAutomatedUHC(e.getPlayer());

	}

}
