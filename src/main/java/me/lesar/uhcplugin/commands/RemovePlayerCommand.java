package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.AddedPlayerTabCompleter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemovePlayerCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public RemovePlayerCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("removeplayer").setExecutor(this);
		new AddedPlayerTabCompleter(plugin, "removeplayer");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(args.length < 1) return false;

		Player player = plugin.getServer().getPlayer(args[0]);
		if(player == null) {

			sender.sendMessage(ChatColor.RED + "Couldn't find player " + args[0]);
			return true;

		}

		if(plugin.allPlayers.remove(player)) sender.sendMessage(ChatColor.YELLOW + "Removed " + player.getName() + " from the UHC list");
		else sender.sendMessage(ChatColor.RED + "Player " + player.getName() + " isn't in the UHC list");

		plugin.alivePlayers.remove(player);

		return true;

	}

}
