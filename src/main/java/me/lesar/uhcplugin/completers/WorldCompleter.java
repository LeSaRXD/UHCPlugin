package me.lesar.uhcplugin.completers;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldCompleter implements TabCompleter {

	private final UHCPlugin plugin;

	public WorldCompleter(UHCPlugin plugin, String name) {

		this.plugin = plugin;
		plugin.getCommand(name).setTabCompleter(this);

	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(args.length == 1) {

			List<String> worlds = new ArrayList<>();
			for(World world : plugin.getServer().getWorlds()) if(world.getName().toLowerCase().startsWith(args[0].toLowerCase())) worlds.add(world.getName());

			return worlds;

		}

		return new ArrayList<>();

	}
}
