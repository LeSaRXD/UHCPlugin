package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.NullCompleter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetUHCWorldCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public SetUHCWorldCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("setuhcworld").setExecutor(this);
		new NullCompleter(plugin, "setuhcworld");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(args.length == 0) {

			plugin.uhcWorldName = null;
			return true;

		}

		if(plugin.getServer().getWorld(args[0]) != null) {

			sender.sendMessage(ChatColor.RED + "A world called " + args[0] + " already exists!");
			return true;

		}

		plugin.uhcWorldName = args[0];
		sender.sendMessage(ChatColor.GREEN + "Set the UHC world to " + args[0]);

		return true;

	}

}
