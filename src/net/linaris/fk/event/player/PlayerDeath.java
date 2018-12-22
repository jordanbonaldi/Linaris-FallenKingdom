package net.linaris.fk.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Score;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;

public class PlayerDeath extends FKListener {
    public PlayerDeath(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team playerTeam = Team.getPlayerTeam(player);
        if (!Step.isStep(Step.IN_GAME) || playerTeam == Team.SPEC) {
            event.setDeathMessage(null);
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }
        event.setDeathMessage(FKPlugin.prefix + playerTeam.getColor() + player.getName() + ChatColor.GRAY + " " + (player.getKiller() == null ? "a succombé." : new StringBuilder("a été tué par ").append(Team.getPlayerTeam(player.getKiller()).getColor()).append(player.getKiller().getName()).toString()));
        if (player.getKiller() != null) {
            Score score = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("kills").getScore(player.getKiller());
            score.setScore(score.getScore() + 1);
            FKPlugin.getGameAPI().kill(player.getKiller());
        }
    }
}
