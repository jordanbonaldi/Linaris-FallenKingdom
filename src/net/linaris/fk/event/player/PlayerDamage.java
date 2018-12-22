package net.linaris.fk.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;

public class PlayerDamage extends FKListener {
    public PlayerDamage(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!Step.isStep(Step.IN_GAME) || Team.getPlayerTeam((Player) event.getEntity()) == Team.SPEC) {
                event.setCancelled(true);
            }
        }
    }
}
