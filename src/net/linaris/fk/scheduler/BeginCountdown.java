package net.linaris.fk.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.handler.Kit;
import net.linaris.fk.handler.PlayerData;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;

public class BeginCountdown extends BukkitRunnable {
    public static boolean started = false;
    public static int timeUntilStart = 60;

    private FKPlugin plugin;

    public BeginCountdown(FKPlugin plugin) {
        this.plugin = plugin;
        BeginCountdown.started = true;
        this.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        if (BeginCountdown.timeUntilStart > 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setExp(BeginCountdown.timeUntilStart / 60.0F);
                player.setLevel(BeginCountdown.timeUntilStart);
            }
        } else {
            this.cancel();
            if (Bukkit.getOnlinePlayers().length < 4) {
                Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.RED + "Il n'y a pas assez de joueurs !");
                BeginCountdown.timeUntilStart = 60;
                BeginCountdown.started = false;
            } else {
                Step.setCurrentStep(Step.IN_GAME);
                Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.AQUA + "La partie vient de commencer, bon jeu !");
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Objective health = scoreboard.getObjective("health");
                if (health != null) {
                    health.unregister();
                }
                health = scoreboard.registerNewObjective("health", "health");
                health.setDisplayName(ChatColor.RED + "♥");
                health.setDisplaySlot(DisplaySlot.BELOW_NAME);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Team team = Team.getPlayerTeam(player);
                    if (team == Team.SPEC) {
                        team = Team.getRandomTeam();
                        team.addPlayer(player);
                    }
                    BeginCountdown.resetPlayer(player);
                    player.setExp(0);
                    player.setLevel(0);
                    health.getScore(player).setScore(20);
                    player.teleport(team.getSpawnLocation());
                    FKPlugin.getGameAPI().applyKit(player);
                }
                Objective kills = scoreboard.getObjective("kills");
                if (kills != null) {
                    kills.unregister();
                }
                kills = scoreboard.registerNewObjective("kills", "dummy");
                kills.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                Objective teams = scoreboard.getObjective("teams");
                for (Team team : Team.values()) {
                    if (team == Team.SPEC || team.getCuboid() == null) {
                        continue;
                    }
                    scoreboard.resetScores(team.getScore().getEntry());
                    Score score = teams.getScore(team.getColor() + "Wither " + (team.getDisplayName().endsWith("e") && team != Team.RED && team != Team.YELLOW ? team.getDisplayName().substring(0, team.getDisplayName().length() - 1) : team.getDisplayName()));
                    score.setScore(500);
                }
                Score score = teams.getScore("--------");
                score.setScore(1);
                score.setScore(0);
                scoreboard.resetScores("Lobby");
                new GameTask(plugin);
                new TimeTask(plugin);
            }
            return;
        }
        int remainingMins = BeginCountdown.timeUntilStart / 60 % 60;
        int remainingSecs = BeginCountdown.timeUntilStart % 60;
        if (BeginCountdown.timeUntilStart % 30 == 0 || remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs <= 5)) {
            Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.GOLD + "Démarrage du jeu dans " + ChatColor.YELLOW + (remainingMins > 0 ? remainingMins + " minute" + (remainingMins > 1 ? "s" : "") : "") + (remainingSecs > 0 ? (remainingMins > 0 ? " " : "") + remainingSecs + " seconde" + (remainingSecs > 1 ? "s" : "") : "") + ".");
            if (remainingMins == 0 && remainingSecs <= 10) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), remainingSecs == 1 ? Sound.ANVIL_LAND : Sound.CLICK, 1f, 1f);
                }
            }
        }
        BeginCountdown.timeUntilStart--;
    }

    private ItemStack enchantBook(ItemStack item, Enchantment enchant, int level) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(enchant, level, false);
        item.setItemMeta(meta);
        return item;
    }

    public static void resetPlayer(Player player) {
        player.setFireTicks(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(5.0F);
        player.setFallDistance(0);
        player.setExp(0.0F);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.closeInventory();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}
