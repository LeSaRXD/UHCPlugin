package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.NullCompleter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartUHCCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public StartUHCCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("startuhc").setExecutor(this);
		new NullCompleter(plugin, "startuhc");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(!(sender instanceof Player)) {

			sender.sendMessage(ChatColor.RED + "Must be a player to start");
			return true;

		}

		if(plugin.getActive()) {

			sender.sendMessage(ChatColor.RED + "The UHC has already started!");
			return true;

		}
		if(plugin.allPlayers.size() < 2) {

			sender.sendMessage(ChatColor.RED + "Can't start UHC with " + plugin.allPlayers.size() + " players");
			return true;

		}

		if(args.length < 4) return false;

		try {

			float timer = Float.parseFloat(args[0]);
			if(timer < 0.25f) {

				sender.sendMessage(ChatColor.RED + "Timer can't be less than 15 seconds");
				return true;

			}

			float borderShrinkTime = Float.parseFloat(args[1]);
			if(borderShrinkTime < 0.25f) {

				sender.sendMessage(ChatColor.RED + "The border has to shrink for at least 15 seconds");
				return true;

			}

			long startBorderRadius = Long.parseLong(args[2]);
			if(startBorderRadius < 10) {

				sender.sendMessage(ChatColor.RED + "The start border size has to be at least 10 blocks");
				return true;

			}
			long endBorderRadius = Long.parseLong(args[3]);
			if(endBorderRadius > startBorderRadius) {

				sender.sendMessage(ChatColor.RED + "The end border can't be bigger than the start size");
				return true;
				
			} else if(endBorderRadius < 10) {

				sender.sendMessage(ChatColor.RED + "The end border size has to be at least 10 blocks");
				return true;

			}

			plugin.startUHCDisablePvP(timer, borderShrinkTime, startBorderRadius, endBorderRadius, ((Player) sender).getWorld());

		} catch (NumberFormatException e) {

			sender.sendMessage(ChatColor.RED + args[0] + " is not a decimal number!");
			return true;

		}

		sender.sendMessage(ChatColor.GREEN + "Started the UHC!");

		return true;

	}

}
