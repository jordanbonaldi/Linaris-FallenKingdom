package net.linaris.fk;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import lombok.SneakyThrows;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.event.block.BlockBreak;
import net.linaris.fk.event.block.BlockFade;
import net.linaris.fk.event.block.BlockPlace;
import net.linaris.fk.event.entity.CreatureSpawn;
import net.linaris.fk.event.entity.EntityDamage;
import net.linaris.fk.event.entity.EntityDamageByPlayer;
import net.linaris.fk.event.entity.EntityDeath;
import net.linaris.fk.event.entity.EntityExplode;
import net.linaris.fk.event.entity.EntityTarget;
import net.linaris.fk.event.entity.FoodLevelChange;
import net.linaris.fk.event.inventory.InventoryClick;
import net.linaris.fk.event.player.AsyncPlayerChat;
import net.linaris.fk.event.player.PlayerAchievementAwarded;
import net.linaris.fk.event.player.PlayerCommandPreprocess;
import net.linaris.fk.event.player.PlayerDamage;
import net.linaris.fk.event.player.PlayerDamageByPlayer;
import net.linaris.fk.event.player.PlayerDeath;
import net.linaris.fk.event.player.PlayerDropItem;
import net.linaris.fk.event.player.PlayerInteract;
import net.linaris.fk.event.player.PlayerJoin;
import net.linaris.fk.event.player.PlayerKick;
import net.linaris.fk.event.player.PlayerLogin;
import net.linaris.fk.event.player.PlayerMove;
import net.linaris.fk.event.player.PlayerPickupItem;
import net.linaris.fk.event.player.PlayerQuit;
import net.linaris.fk.event.player.PlayerRespawn;
import net.linaris.fk.event.server.ServerCommand;
import net.linaris.fk.event.server.ServerListPing;
import net.linaris.fk.event.weather.ThunderChange;
import net.linaris.fk.event.weather.WeatherChange;
import net.linaris.fk.handler.Kit;
import net.linaris.fk.handler.MySQL;
import net.linaris.fk.handler.PlayerData;
import net.linaris.fk.handler.State;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;
import net.linaris.fk.util.Cuboid;
import net.linaris.fk.util.FileUtils;
import net.linaris.fk.util.ReflectionHandler;
import net.linaris.fk.util.ReflectionHandler.PackageType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.skelerex.LinarisKits.api.GameAPI;
import com.skelerex.LinarisKits.api.GameType;

public class FKPlugin extends JavaPlugin {
    public static FKPlugin i;
    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "FK" + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " ";

    public World world;
    public MySQL database;
    public Location lobbyLocation;
    private Map<UUID, PlayerData> data = null;
    private GameAPI m_gameAPI = new GameAPI(GameType.FALLEN_KINGDOM);

    @SneakyThrows
    
    

	public static GameAPI getGameAPI() {
		return i.m_gameAPI; 
	}
    
    @Override
    public void onLoad() {
		{
	        try {
	            Bukkit.unloadWorld("world", false);
	            final File worldContainer = this.getServer().getWorldContainer();
	            final File worldFolder = new File(worldContainer, "world");
	            final File copyFolder = new File(worldContainer, "fk");
	            if (copyFolder.exists()) {
	                ReflectionHandler.getClass("RegionFileCache", ReflectionHandler.PackageType.MINECRAFT_SERVER).getMethod("a", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
	                FileUtils.delete(worldFolder);
	                FileUtils.copyFolder(copyFolder, worldFolder);
	            }
	        }
	        catch (Throwable $ex) {
	            try {
					throw $ex;
				} catch (Throwable e) {
					e.printStackTrace();
				}
	        }
		}
	    }
    

    @Override
    public void onEnable() {
        FKPlugin.i = this;
        Step.setCurrentStep(Step.LOBBY);
        State.setState(State.NONE);
        ConfigurationSerialization.registerClass(Cuboid.class);
        world = Bukkit.getWorlds().get(0);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);
        this.load();
        database = new MySQL(this, this.getConfig().getString("mysql.host"), this.getConfig().getString("mysql.port"), this.getConfig().getString("mysql.database"), this.getConfig().getString("mysql.user"), this.getConfig().getString("mysql.pass"));
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ConnectionSide.SERVER_SIDE, Packets.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                String soundName = event.getPacket().getStrings().read(0);
                if (soundName.contains("mob.wither") && !soundName.contains("mob.wither.hurt")) {
                    event.setCancelled(true);
                }
            }
        });
        try {
			this.register(BlockBreak.class, BlockFade.class, BlockPlace.class, CreatureSpawn.class, EntityDamage.class, EntityDamageByPlayer.class, EntityDeath.class, EntityExplode.class, EntityTarget.class, FoodLevelChange.class, InventoryClick.class, AsyncPlayerChat.class, PlayerAchievementAwarded.class, PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDamageByPlayer.class, PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerJoin.class, PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class, PlayerQuit.class, PlayerRespawn.class, ServerCommand.class, ServerListPing.class, ThunderChange.class, WeatherChange.class);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        CustomEntityType.registerEntities();
    }

    @SneakyThrows
    private void register(Class<? extends FKListener>... classes) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        for (Class<? extends FKListener> clazz : classes) {
            Constructor<? extends FKListener> constructor = clazz.getConstructor(FKPlugin.class);
            Bukkit.getPluginManager().registerEvents(constructor.newInstance(this), this);
        }
    }

    @Override
    public void onDisable() {
        this.save();
        CustomEntityType.unregisterEntities();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Vous devez être un joueur.");
            return true;
        }
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("fk")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "Plugin Fallen Kingdoms v1.0.1 | par Rellynn pour The Nexus.");
            } else {
                String sub = args[0];
                if (sub.equalsIgnoreCase("help")) {
                    player.sendMessage(ChatColor.GOLD + "Aide du plugin Fallen Kingdoms :");
                    player.sendMessage("/fk setlobby" + ChatColor.YELLOW + " - définit le lobby du jeu");
                    player.sendMessage("/fk setspawn <couleur>" + ChatColor.YELLOW + " - définit le spawn de l'équipe <couleur>");
                    player.sendMessage("/fk setcuboid <couleur>" + ChatColor.YELLOW + " - définit la base de l'équipe <couleur>");
                    player.sendMessage("/fk setwither <couleur>" + ChatColor.YELLOW + " - définit l'emplacement du wither de l'équipe <couleur>");
                } else if (sub.equalsIgnoreCase("setlobby")) {
                    lobbyLocation = player.getLocation();
                    player.sendMessage(ChatColor.GREEN + "Vous avez défini le lobby avec succès.");
                    this.getConfig().set("lobby", this.toString(player.getLocation()));
                    this.saveConfig();
                } else if (sub.equalsIgnoreCase("setspawn")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("green") && !args[1].equalsIgnoreCase("yellow") && !args[1].equalsIgnoreCase("spec")) {
                        player.sendMessage(ChatColor.RED + "L'équipe " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        Location location = player.getLocation();
                        Team team = Team.getTeam(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini avec succès le spawn de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setSpawnLocation(location);
                        this.getConfig().set("teams." + args[1] + ".spawn", this.toString(location));
                        this.saveConfig();
                    }
                } else if (sub.equalsIgnoreCase("setwither")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("green") && !args[1].equalsIgnoreCase("yellow")) {
                        player.sendMessage(ChatColor.RED + "L'équipe " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        Location location = player.getLocation();
                        Team team = Team.getTeam(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini l'emplacement du wither de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setWitherLocation(location);
                        this.getConfig().set("teams." + args[1] + ".wither", this.toString(location));
                        this.saveConfig();
                    }
                } else if (sub.equalsIgnoreCase("setcuboid")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("green") && !args[1].equalsIgnoreCase("yellow")) {
                        player.sendMessage(ChatColor.RED + "L'équipe " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        Team team = Team.getTeam(args[1]);
                        Cuboid cuboid = new Cuboid(this.toLocation(player.getMetadata("pos1").get(0).asString()), this.toLocation(player.getMetadata("pos2").get(0).asString()));
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini avec succès la base de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setCuboid(cuboid);
                        this.getConfig().set("teams." + args[1] + ".cuboid", cuboid);
                        this.saveConfig();
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Mauvais arguments ou commande inexistante. Tapez " + ChatColor.DARK_RED + "/fk help" + ChatColor.RED + " pour de l'aide.");
                }
                return true;
            }
        }
        return false;
    }

    private void load() {
        this.saveDefaultConfig();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : Team.values()) {
            team.createTeam(scoreboard);
        }
        ConfigurationSection teams = this.getConfig().getConfigurationSection("teams");
        if (teams != null) {
            Objective objective = scoreboard.getObjective("teams");
            if (objective == null) {
                objective = scoreboard.registerNewObjective("teams", "dummy");
            }
            objective.setDisplayName(ChatColor.DARK_GRAY + "[-" + ChatColor.YELLOW + "Fallen Kingdoms" + ChatColor.DARK_GRAY + "-]");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (String key : teams.getKeys(false)) {
                Team team = Team.getTeam(key);
                final Team team2 = Team.getTeam(key);
                ConfigurationSection section = teams.getConfigurationSection(key);
                if (section.isString("spawn")) {
                    Location spawnLoc = this.toLocation(section.getString("spawn"));
                    team.setSpawnLocation(spawnLoc);
                    spawnLoc.getChunk().load(true);
                }
                if (section.isString("wither")) {
                    final Location witherLoc = this.toLocation(section.getString("wither"));
                    team2.setWitherLocation(witherLoc);
                    witherLoc.getChunk().load(true);
                    final CustomEntityWither entityWither = new CustomEntityWither((net.minecraft.server.v1_7_R4.World)((CraftWorld)witherLoc.getWorld()).getHandle());
                    entityWither.setPosition(witherLoc.getX(), witherLoc.getY(), witherLoc.getZ());
                    ((CraftWorld)witherLoc.getWorld()).getHandle().addEntity((net.minecraft.server.v1_7_R4.Entity)entityWither);
                    final Wither wither = (Wither)entityWither.getBukkitEntity();
                    wither.setCustomName(team2.getColor() + "Equipe " + team2.getDisplayName());
                    wither.setCustomNameVisible(true);
                    wither.setMaxHealth(1000.0);
                    wither.setHealth(1000.0);
                    team2.isWither(wither);
                }
                team.setCuboid((Cuboid) section.get("cuboid"));
                if (team != Team.SPEC) {
                    team.setScore(0);
                }
            }
        }
        String defaultLoc = this.toString(world.getSpawnLocation());
        lobbyLocation = this.toLocation(this.getConfig().getString("lobby", defaultLoc));
    }

    /*public PlayerData getData(Player player) {
        PlayerData data = this.data.get(player.getUniqueId());
        if (data == null) {
            data = new PlayerData();
           // this.loadData(player);
        }
        return data;
    }*/

  /*  public void loadData(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final ResultSet res = database.querySQL("SELECT * FROM players WHERE uuid=UNHEX('" + player.getUniqueId().toString().replaceAll("-", "") + "')");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerData data = null;
                            try {
                                if (res.first()) {
                                    data = new PlayerData();
                                } else {
                                    data = new PlayerData();
                                }
                                FKPlugin.this.data.put(player.getUniqueId(), data);
                            } catch (SQLException e) {
                                player.kickPlayer(ChatColor.RED + "Impossible de charger vos statistiques... :(");
                            }
                        }
                    }.runTask(FKPlugin.this);
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this);
    }*/

    private void save() {
        this.getConfig().set("lobby", this.toString(lobbyLocation));
        for (Team team : Team.values()) {
            String name = team.getName();
            if (team.getSpawnLocation() != null) {
                this.getConfig().set("teams." + name + ".spawn", this.toString(team.getSpawnLocation()));
            }
            if (team.getWitherLocation() != null) {
                this.getConfig().set("teams." + name + ".wither", this.toString(team.getWitherLocation()));
            }
            if (team.getCuboid() != null) {
                this.getConfig().set("teams." + name + ".cuboid", team.getCuboid());
            }
        }
        this.saveConfig();
    }

    public void setSpectator(Player player, boolean lose) {
        player.setAllowFlight(true);
        if (lose && Team.getPlayerTeam(player) != Team.SPEC) {
            this.removePlayer(player);
        }
        Team.SPEC.addPlayer(player);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (player != online) {
                player.showPlayer(online);
                if (Team.getPlayerTeam(online) != Team.SPEC) {
                    online.hidePlayer(player);
                }
            }
        }
    }

    public void removePlayer(Player player) {
        final Team team = Team.getPlayerTeam(player);
        if (team != Team.SPEC) {
            team.removePlayer(player);
            Kit.setPlayerKit(player, null);
            if (Step.isStep(Step.LOBBY)) {
                data.remove(player.getUniqueId());
            } else if (Step.isStep(Step.IN_GAME) && team.getOnlinePlayers().size() == 0) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (Wither wither : Bukkit.getWorlds().get(0).getEntitiesByClass(Wither.class)) {
                            if (team.isWither(wither)) {
                                team.loose(wither);
                                break;
                            }
                        }
                        Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.GRAY + "L'équipe " + team.getColor() + team.getDisplayName() + ChatColor.GRAY + " est éliminée !");
                        List<Team> aliveTeams = Team.getAliveTeams();
                        if (aliveTeams.size() == 1) {
                            Team winnerTeam = aliveTeams.get(0);
                            Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.GOLD + ChatColor.BOLD + "Victoire de l'équipe " + winnerTeam.getColor() + ChatColor.BOLD + winnerTeam.getDisplayName() + " " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.BOLD + " Félicitations " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|");
                            FKPlugin.this.stopGame(winnerTeam);
                            List players = new LinkedList();
                            for (Player player : winnerTeam.getOnlinePlayers()) {
                              players.add(player);
                            }
                            
                            FKPlugin.getGameAPI().win(players);
                        }
                    }
                }.runTaskLater(this, 1);
            }
        }
    }

    public void stopGame(Team winnerTeam) {
        Step.setCurrentStep(Step.POST_GAME);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setAllowFlight(true);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    FKPlugin.this.teleportToLobby(online);
                }
            }
        }.runTaskLater(this, 300);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.shutdown();
            }
        }.runTaskLater(this, 400);
    }

    public void teleportToLobby(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("Hub1");
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    private Location toLocation(String string) {
        String[] splitted = string.split("_");
        World world = Bukkit.getWorld(splitted[0]);
        return new Location(world, Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
    }

    public String toString(Location location) {
        World world = location.getWorld();
        return world.getName() + "_" + location.getX() + "_" + location.getY() + "_" + location.getZ() + "_" + location.getYaw() + "_" + location.getPitch();
    }
}
