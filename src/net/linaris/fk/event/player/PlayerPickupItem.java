package net.linaris.fk.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;

public class PlayerPickupItem extends FKListener {
    public PlayerPickupItem(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (Step.isStep(Step.LOBBY) || Team.getPlayerTeam(event.getPlayer()) == Team.SPEC) {
            event.setCancelled(true);
        }
    }
}
