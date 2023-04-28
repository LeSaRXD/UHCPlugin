package me.lesar.uhcplugin.commands;

import me.lesar.uhcplugin.UHCPlugin;
import me.lesar.uhcplugin.completers.NullCompleter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StopUHCCommand implements CommandExecutor {

	private final UHCPlugin plugin;

	public StopUHCCommand(UHCPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("stopuhc").setExecutor(this);
		new NullCompleter(plugin, "stopuhc");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(!plugin.getActive()) {

			sender.sendMessage(ChatColor.RED + "The UHC hasn't started yet!");
			return true;

		}

		plugin.forceStopUHC();
		sender.sendMessage(ChatColor.GREEN + "Stopped the UHC!");

		return true;

	}

}
