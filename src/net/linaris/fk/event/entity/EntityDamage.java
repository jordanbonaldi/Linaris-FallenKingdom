package net.linaris.fk.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;

public class EntityDamage extends FKListener {
    public EntityDamage(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
