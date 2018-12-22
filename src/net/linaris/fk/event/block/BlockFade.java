package net.linaris.fk.event.block;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFadeEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class BlockFade extends FKListener {
    public BlockFade(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }
}
