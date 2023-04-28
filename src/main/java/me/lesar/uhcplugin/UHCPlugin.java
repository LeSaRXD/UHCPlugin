package me.lesar.uhcplugin;

import me.lesar.uhcplugin.commands.*;
import me.lesar.uhcplugin.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class UHCPlugin extends JavaPlugin {

	private boolean isActive = false;
	private boolean isPvPDisabled = false;
	public boolean getPvPDisabled() {
		return isPvPDisabled && isActive;
	}
	public boolean getActive() {
		return isActive;
	}

	public Set<Player> allPlayers = new HashSet<>(), alivePlayers = new HashSet<>();
	private World uhcWorld;

	private Set<BukkitTask> currentTasks = new HashSet<>();

	@Override
	public void onEnable() {

		registerCommands();
		registerListeners();

	}

	private void registerCommands() {

		new StartUHCCommand(this);
		new StopUHCCommand(this);

		new AddPlayerCommand(this);
		new RemovePlayerCommand(this);
		new ClearPlayersCommand(this);
		new ListPlayersCommand(this);

	}

	private void registerListeners() {

		new PvPListener(this);
		new DeathListener(this);

	}



	public void startUHCDisablePvP(float graceTimeInMinutes, float borderShrinkTimeInMinutes, long finalBorderRadius, World uhcWorld) {

		currentTasks.add(getServer().getScheduler().runTaskTimer(this, () -> Bukkit.getLogger().info(uhcWorld.getWorldBorder().getSize() + ""), 0L, 20L));

		isActive = true;
		isPvPDisabled = true;
		this.uhcWorld = uhcWorld;

		alivePlayers = new HashSet<>(allPlayers);
		for(Player player : allPlayers) {

			player.setGameMode(GameMode.SURVIVAL);
			player.sendTitle(ChatColor.GREEN + "UHC has started!", ChatColor.GREEN + "PvP has been disabled");

		}

		Bukkit.getLogger().info("UHC was started. PvP has been disabled for " + graceTimeInMinutes + " minutes");



		long timeInTicks = (long) (graceTimeInMinutes * 60 * 20);

		// warnings!

		class Warning {
			public String title;
			public String subtitle;
			public long delay;

			public Warning(String title, String subtitle, long delay) {
				this.title = title;
				this.subtitle = subtitle;
				this.delay = delay;
			}
		}

		String graceTimeString;
		if(graceTimeInMinutes == Math.floor(graceTimeInMinutes)) graceTimeString = String.valueOf((int) graceTimeInMinutes);
		else graceTimeString = String.valueOf(graceTimeInMinutes);

		String borderShrinkTimeString;
		if(borderShrinkTimeInMinutes == Math.floor(borderShrinkTimeInMinutes)) borderShrinkTimeString = String.valueOf((int) borderShrinkTimeInMinutes);
		else borderShrinkTimeString = String.valueOf(borderShrinkTimeInMinutes);

		List<Warning> warnings = new ArrayList<Warning>() {{
			add(new Warning(ChatColor.GREEN + "UHC has started!", "PvP will be enabled in " + graceTimeString + " minute" + (graceTimeString.equals("1") ? "" : "s"), 0L));
			add(new Warning(ChatColor.YELLOW + "PvP is on in 10 seconds!", "Border will start shrinking", timeInTicks - 200L));
			add(new Warning(ChatColor.RED + "PvP has been enabled!", "The border will be shrinking for " + borderShrinkTimeString + " minute" + (borderShrinkTimeString.equals("1") ? "" : "s"), timeInTicks));
		}};
		for (Warning warning : warnings)
			currentTasks.add(getServer().getScheduler().runTaskLater(this, () -> {
				for(Player player : allPlayers) {

					player.sendTitle(warning.title, warning.subtitle);
					player.sendMessage(warning.title + " " + ChatColor.RESET + warning.subtitle);

				}
			}, warning.delay));



		currentTasks.add(getServer().getScheduler().runTaskLater(this, () -> {

			isPvPDisabled = false;
			Bukkit.getLogger().info("PvP has been re-enabled");

			Bukkit.getLogger().info("Shrinking border to " + finalBorderRadius + " blocks in " + borderShrinkTimeInMinutes + " minutes");
			uhcWorld.getWorldBorder().setSize(finalBorderRadius, (long) (borderShrinkTimeInMinutes * 60));

		}, timeInTicks));

	}

	public void stopUHC() {

		isActive = false;
		isPvPDisabled = false;

		allPlayers.clear();
		alivePlayers.clear();

		this.uhcWorld.getWorldBorder().setSize(this.uhcWorld.getWorldBorder().getSize());

	}

	public void forceStopUHC() {

		Bukkit.getLogger().info("UHC has stopped. PvP has been re-enabled");

		for(BukkitTask task : currentTasks) task.cancel();

		for(Player player : allPlayers) player.sendTitle(ChatColor.RED + "UHC has ended!", "");

		stopUHC();

	}

}
