package net.linaris.fk.event.block;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;

public class BlockBreak extends FKListener {
    public BlockBreak(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Team playerTeam = Team.getPlayerTeam(player);
        if (Step.isStep(Step.LOBBY) || playerTeam == Team.SPEC) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        } else if (Step.isStep(Step.IN_GAME)) {
            Block block = event.getBlock();
            Location loc = block.getLocation();
            int x1 = plugin.lobbyLocation.getBlockX(), x2 = loc.getBlockX();
            int z1 = plugin.lobbyLocation.getBlockZ(), z2 = loc.getBlockZ();
            if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2)) <= 25) {
                player.damage(0.5);
                event.setCancelled(true);
                return;
            }
            Material mat = block.getType();
            boolean canBreak = mat == Material.TNT || mat.name().contains("TORCH") || mat == Material.SIGN || mat == Material.YELLOW_FLOWER || mat == Material.RED_ROSE;
            if (playerTeam.getCuboid().contains(loc) && playerTeam.getWitherLocation().distanceSquared(event.getBlock().getLocation()) < 8) {
                event.setCancelled(true);
                player.sendMessage(FKPlugin.prefix + ChatColor.RED + "Vous ne pouvez pas casser � moins de 3 blocs de votre wither.");
                return;
            } else if (canBreak) { return; }
            for (Team team : Team.values()) {
                if (team == Team.SPEC || team == playerTeam || team.getCuboid() == null) {
                    continue;
                } else if (team.getCuboid().contains(loc)) {
                    event.setCancelled(true);
                    player.sendMessage(FKPlugin.prefix + ChatColor.RED + "Vous ne pouvez pas casser dans les bases ennemies.");
                    return;
                }
            }
            if (block.getType() == Material.FURNACE) {
                block.removeMetadata("owner", plugin);
            }
        }
    }
}
