package net.linaris.fk.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;

public class PlayerDropItem extends FKListener {
    public PlayerDropItem(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
