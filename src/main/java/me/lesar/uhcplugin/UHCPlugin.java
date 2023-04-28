package me.lesar.uhcplugin;

import me.lesar.uhcplugin.commands.*;
import me.lesar.uhcplugin.listeners.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class UHCPlugin extends JavaPlugin {

	private static final long MIN_DISTANCE_BETWEEN_PLAYERS = 100L;

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
		new DisconnectListener(this);

	}



	public void startUHCDisablePvP(float graceTimeInMinutes, float borderShrinkTimeInMinutes, long startBorderRadius, long endBorderRadius, World uhcWorld) {

		isActive = true;
		isPvPDisabled = true;
		this.uhcWorld = uhcWorld;

		uhcWorld.getWorldBorder().setSize(startBorderRadius);

		initPlayers(startBorderRadius);

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

			Bukkit.getLogger().info("Shrinking border to " + endBorderRadius + " blocks in " + borderShrinkTimeInMinutes + " minutes");
			uhcWorld.getWorldBorder().setSize(endBorderRadius, (long) (borderShrinkTimeInMinutes * 60));

		}, timeInTicks));

	}

	private void initPlayers(long size) {

		float minDistanceBetweenPlayers = (float) Math.min(MIN_DISTANCE_BETWEEN_PLAYERS, size / 2);

		alivePlayers = new HashSet<>(allPlayers);

		List<Location> spawnLocations = new ArrayList<>();
		Random random = new Random();

		for(Player player : alivePlayers) {

			int x = 0, z = 0;

			boolean isGoodLocation = false;
			while(!isGoodLocation){

				isGoodLocation = true;
				x = (int) (random.nextLong(-size / 2, size / 2));
				z = (int) (random.nextLong(-size / 2, size / 2));

				for(Location location : spawnLocations) {

					double distX = (double) x - location.getX(), distZ = (double) z - location.getZ();
					double distance = Math.sqrt(distX * distX + distZ * distZ);
					System.out.println(x + " " + z + "   " + location.getX() + " " + location.getZ() + "   " + distance);
					if(distance < minDistanceBetweenPlayers) {

						isGoodLocation = false;
						break;

					}

				}

			}

			Location spawnLocation = new Location(uhcWorld, x, uhcWorld.getHighestBlockAt(x, z).getY() + 1, z);
			player.teleport(spawnLocation);
			spawnLocations.add(spawnLocation);

			player.setGameMode(GameMode.SURVIVAL);

			player.setHealth(20);
			player.setFoodLevel(20);
			player.setSaturation(0.6f);
			player.setExhaustion(0f);

			player.sendTitle(ChatColor.GREEN + "UHC has started!", ChatColor.GREEN + "PvP has been disabled");

		}

	}

	private void stopUHC() {

		isActive = false;
		isPvPDisabled = false;

		allPlayers.clear();
		alivePlayers.clear();

		for(BukkitTask task : currentTasks) task.cancel();
		this.uhcWorld.getWorldBorder().setSize(this.uhcWorld.getWorldBorder().getSize());

	}

	public void finishUHC() {

		Player lastPlayer = (Player) alivePlayers.toArray()[0];
		for(Player uhcPlayer : allPlayers)
			uhcPlayer.sendTitle(ChatColor.GOLD + lastPlayer.getName(), ChatColor.GOLD + "has won the UHC!");

		lastPlayer.setGameMode(GameMode.CREATIVE);

		stopUHC();

	}

	public void forceStopUHC() {

		Bukkit.getLogger().info("UHC has stopped. PvP has been re-enabled");

		for(Player player : allPlayers) player.sendTitle(ChatColor.RED + "UHC has ended!", "");

		stopUHC();

	}

}
