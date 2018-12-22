package net.linaris.fk.event.weather;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class WeatherChange extends FKListener {
    public WeatherChange(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        World world = event.getWorld();
        if (!world.isThundering() && !world.hasStorm()) {
            event.setCancelled(true);
        }
    }
}
