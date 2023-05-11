package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.WorldCompleter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InitUHCCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public InitUHCCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("inituhc").setExecutor(this);
		new WorldCompleter(plugin, "inituhc");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(args.length == 0 && !(sender instanceof Player)) {

			sender.sendMessage(ChatColor.RED + "Please enter a world name");
			return true;

		}

		String uhcWorldName;
		if(args.length > 0) uhcWorldName = args[0];
		else uhcWorldName = ((Player) sender).getWorld().getName();

		if(plugin.initializeUHCWorld(uhcWorldName)) sender.sendMessage(ChatColor.GREEN + "Initialized world " + uhcWorldName + " for UHC");
		else sender.sendMessage(ChatColor.RED + "Could not find world " + uhcWorldName);

		return true;

	}

}
