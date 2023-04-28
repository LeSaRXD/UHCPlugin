package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.NullCompleter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ClearPlayersCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public ClearPlayersCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("clearplayers").setExecutor(this);
		new NullCompleter(plugin, "clearplayers");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		plugin.allPlayers.clear();
		plugin.alivePlayers.clear();

		sender.sendMessage(ChatColor.GREEN + "Removed all players from the UHC list");

		return true;

	}
}
