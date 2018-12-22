package net.linaris.fk.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class PlayerQuit extends FKListener {
    public PlayerQuit(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        player.getInventory().clear();
        plugin.removePlayer(player);
    }
}
