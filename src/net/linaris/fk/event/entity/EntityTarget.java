package net.linaris.fk.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Team;

public class EntityTarget extends FKListener {

    public EntityTarget(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityTargetByEntity(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && Team.getPlayerTeam((Player) event.getTarget()) == Team.SPEC) {
            event.setCancelled(true);
        }
    }
}
