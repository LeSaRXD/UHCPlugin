package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.NullCompleter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListPlayersCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public ListPlayersCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("listplayers").setExecutor(this);
		new NullCompleter(plugin, "listplayers");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(plugin.allPlayers.size() == 0) {

			sender.sendMessage(ChatColor.YELLOW + "There are no players in the UHC");
			return true;

		}

		List<String> allNames = new ArrayList<>();
		for (Player player : plugin.allPlayers) allNames.add(player.getName());
		sender.sendMessage("Players in the UHC: " + String.join(", ", allNames));

		if(plugin.alivePlayers.size() == 0) {

			sender.sendMessage(ChatColor.YELLOW + "There are no alive players in the UHC");
			return true;

		} else {

			List<String> aliveNames = new ArrayList<>();
			for (Player player : plugin.alivePlayers) aliveNames.add(player.getName());
			sender.sendMessage("Alive players: " + String.join(", ", aliveNames));

		}

		return true;

	}
}
