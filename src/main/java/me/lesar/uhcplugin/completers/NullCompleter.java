package me.lesar.uhcplugin.completers;

import me.lesar.uhcplugin.UHCPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NullCompleter implements TabCompleter {

	public NullCompleter(UHCPlugin plugin, String command) {

		plugin.getCommand(command).setTabCompleter(this);

	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		return new ArrayList<String>();

	}

}
