package net.linaris.fk.event.weather;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.ThunderChangeEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class ThunderChange extends FKListener {
    public ThunderChange(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        if (event.toThunderState()) {
            event.setCancelled(true);
        }
    }
}
