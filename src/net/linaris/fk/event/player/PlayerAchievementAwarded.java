package net.linaris.fk.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class PlayerAchievementAwarded extends FKListener {
    public PlayerAchievementAwarded(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerAchievementArwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}
