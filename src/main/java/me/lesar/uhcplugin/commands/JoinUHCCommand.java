package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.NullCompleter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JoinUHCCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public JoinUHCCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("joinuhc").setExecutor(this);
		new NullCompleter(plugin, "joinuhc");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(!(sender instanceof Player)) {

			sender.sendMessage(ChatColor.RED + "You aren't a player!");
			return true;

		}

		Player player = (Player) sender;

		if(plugin.getActive()) {

			player.sendMessage(ChatColor.RED + "The UHC has already started!");
			return true;

		}

		if(plugin.uhcWorldName == null) {

			player.sendMessage(ChatColor.RED + "There's no automated UHC at the moment");
			return true;

		}

		if(plugin.allPlayers.size() == 0) plugin.createUHCWorld();
		plugin.allPlayers.add(player);

		plugin.resetStartTimer();

		player.teleport(plugin.uhcWorld.getSpawnLocation());
		player.setGameMode(GameMode.ADVENTURE);

		player.sendMessage(ChatColor.GREEN + "You joined the UHC");

		return true;

	}

}
