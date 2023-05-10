package me.lesar.uhcplugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.lesar.uhcplugin.commands.*;
import me.lesar.uhcplugin.listeners.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class UHCPlugin extends JavaPlugin {

	private MultiverseCore mvCore;

	private boolean isActive = false;
	private boolean isPvPDisabled = false;
	public boolean getPvPDisabled() {
		return isPvPDisabled && isActive;
	}
	public boolean getActive() {
		return isActive;
	}

	public Set<Player> allPlayers = new HashSet<>(), alivePlayers = new HashSet<>();

	public MultiverseWorld uhcWorld;
	public World currentUhcWorld;

	private Set<BukkitTask> currentTasks = new HashSet<>();



	@Override
	public void onEnable() {

		initDepends();

		initConfig();

		registerCommands();
		registerListeners();



		generateAutomatedUHCWorld();

	}



	private void initDepends() {

		mvCore = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");

	}

	private void initConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();

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
		new ConnectionListener(this, mvCore);

	}



	private void generateAutomatedUHCWorld() {

		Bukkit.getLogger().info("Generating the UHC world");

		String uhcWorldName = getConfig().getString("auto.world_name");

		// TODO: choose random seed from config
		long seed = 0;

		WorldCreator creator = new WorldCreator(uhcWorldName);
		creator.seed(seed);
		World world = creator.createWorld();
		uhcWorld = mvCore.getMVWorldManager().getMVWorld(world);

		uhcWorld.setGameMode(GameMode.SPECTATOR);
		world.getWorldBorder().setSize(20);

		world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);

		Bukkit.getLogger().info("Generated the UHC world");

	}

	private void regenerateAutomatedUHCWorld() {

		Bukkit.getLogger().info("Regenerating the UHC world");

		for(Player player : uhcWorld.getCBWorld().getPlayers())
			player.teleport(mvCore.getMVWorldManager().getSpawnWorld().getSpawnLocation());

		String uhcWorldName = getConfig().getString("auto.world_name");

		// TODO: choose random seed from config
		String seed = "0";
		mvCore.getMVWorldManager().regenWorld(uhcWorldName, true, false, seed);

	}



	public void playerJoinAutomatedUHC(Player player) {

		if(allPlayers.contains(player)) return;

		allPlayers.add(player);
		resetAutomatedStartTimer();

	}

	public void playerLeaveAutomatedUHC(Player player) {

		cancelAutomatedStartTimer();

		allPlayers.remove(player);

		if(!isActive) return;

		if(alivePlayers.remove(player)) {

			if(alivePlayers.size() <= 1) finishUHC();
			else for(Player other : allPlayers) other.sendMessage(ChatColor.RED + player.getName() + " has left! " + alivePlayers.size() + " players remain!");

			player.sendMessage(ChatColor.RED + "You have left the UHC");

		}

	}

	private BukkitTask startTimerTask;
	public void resetAutomatedStartTimer() {

		if(startTimerTask != null) startTimerTask.cancel();
		if(allPlayers.size() < getConfig().getInt("auto.minimum_players")) return;

		int startTimer = getConfig().getInt("auto.start_countdown_timer");

		startTimerTask = getServer().getScheduler().runTaskLater(this, () -> startAutomatedUHC(), startTimer * 20L);
		for(Player player : allPlayers) player.sendMessage(ChatColor.GREEN + "The UHC starts in " + startTimer + " seconds!");

	}

	public void cancelAutomatedStartTimer() {

		if(startTimerTask == null) return;
		if(startTimerTask.isCancelled()) return;
		if(allPlayers.size() >= getConfig().getInt("auto.minimum_players")) return;

		startTimerTask.cancel();
		for(Player player : allPlayers) player.sendMessage(ChatColor.RED + "The UHC has been cancelled");

	}

	private void startAutomatedUHC() {

		uhcWorld.setDifficulty(Difficulty.NORMAL);
		startUHCDisablePvP(
			(float) getConfig().getDouble("auto.grace_timer"),
			(float) getConfig().getDouble("auto.border_shrink_timer"),
			getConfig().getLong("auto.start_border_size_per_player") * allPlayers.size(),
			getConfig().getLong("auto.end_border_size"),
			uhcWorld.getCBWorld()
		);

	}



	public void startUHCDisablePvP(float graceTimeInMinutes, float borderShrinkTimeInMinutes, long startBorderRadius, long endBorderRadius, @NotNull World uhcWorld) {

		isActive = true;
		isPvPDisabled = true;
		this.currentUhcWorld = uhcWorld;

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

		float minDistanceBetweenPlayers = (float) Math.min(getConfig().getInt("min_distance_between_players"), size / 2);

		alivePlayers = new HashSet<>(allPlayers);

		List<Location> spawnLocations = new ArrayList<>();
		Random random = new Random();

		for(Player player : alivePlayers) {

			int x = 0, z = 0;

			boolean isGoodLocation = false;
			while(!isGoodLocation){

				isGoodLocation = true;
				x = (int) (random.nextLong(-size / 2, size / 2)) + (int) uhcWorld.getCBWorld().getWorldBorder().getCenter().getX();
				z = (int) (random.nextLong(-size / 2, size / 2)) + (int) uhcWorld.getCBWorld().getWorldBorder().getCenter().getZ();

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

			Location spawnLocation = new Location(currentUhcWorld, x, currentUhcWorld.getHighestBlockAt(x, z).getY() + 1, z);
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
		this.currentUhcWorld.getWorldBorder().setSize(this.currentUhcWorld.getWorldBorder().getSize());

		if(currentUhcWorld == uhcWorld.getCBWorld()) {

			for(Player player : currentUhcWorld.getPlayers()) player.sendMessage(ChatColor.RED + "The world is going to close in 10 seconds!");
			getServer().getScheduler().runTaskLater(this, () -> regenerateAutomatedUHCWorld(), 200L);

		}

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
