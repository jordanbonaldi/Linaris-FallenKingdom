package net.linaris.fk.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;

public class FoodLevelChange extends FKListener {
    public FoodLevelChange(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
