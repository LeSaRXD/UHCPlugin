package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.NotAddedPlayerTabCompleter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddPlayerCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public AddPlayerCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("addplayer").setExecutor(this);
		new NotAddedPlayerTabCompleter(plugin, "addplayer");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(args.length < 1) return false;

		Player player = plugin.getServer().getPlayer(args[0]);
		if(player == null) {

			sender.sendMessage(ChatColor.RED + "Couldn't find player " + args[0]);
			return true;

		}

		if(plugin.allPlayers.contains(player)) {

			sender.sendMessage(ChatColor.YELLOW + player.getName() + " is already in the UHC list");
			return true;

		}

		plugin.allPlayers.add(player);
		sender.sendMessage(ChatColor.GREEN + "Added " + player.getName() + " to the UHC list");

		return true;

	}

}
