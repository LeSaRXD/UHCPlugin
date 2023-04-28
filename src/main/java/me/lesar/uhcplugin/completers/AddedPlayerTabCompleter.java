package me.lesar.uhcplugin.completers;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddedPlayerTabCompleter implements TabCompleter {

	private final UHCPlugin plugin;

	public AddedPlayerTabCompleter(UHCPlugin plugin, String command) {

		this.plugin = plugin;
		plugin.getCommand(command).setTabCompleter(this);

	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(args.length == 1) {

			List<String> names = new ArrayList<>();
			for(Player player : plugin.alivePlayers) {

				if(!player.getName().toLowerCase().startsWith(args[0].toLowerCase())) continue;
				names.add(player.getName());

			}
			return names;

		}

		return new ArrayList<String>();

	}
}
