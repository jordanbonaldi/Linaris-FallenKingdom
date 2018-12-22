package net.linaris.fk.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class PlayerKick extends FKListener {
    public PlayerKick(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
        Player player = event.getPlayer();
        player.getInventory().clear();
        plugin.removePlayer(player);
    }
}
