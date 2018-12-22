package net.linaris.fk.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.State;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;
import net.linaris.fk.util.MathUtils;

public class PlayerInteract extends FKListener {
    public PlayerInteract(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (Step.isStep(Step.IN_GAME)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Team playerTeam = Team.getPlayerTeam(player);
                if (playerTeam == Team.SPEC) {
                    event.setCancelled(true);
                    return;
                }
                Location loc = event.getClickedBlock().getLocation();
                if (playerTeam.getCuboid().contains(loc)) {
                    if (event.getClickedBlock().getState().getType() == Material.WOODEN_DOOR || event.getClickedBlock().getState().getType() == Material.FENCE_GATE || event.getClickedBlock().getState().getType() == Material.TRAP_DOOR) {
                        final Block door = event.getClickedBlock();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if ((door.getData() & 0x4) == 0) { return; }
                                Block topHalf = door.getRelative(BlockFace.UP);
                                door.setData((byte) (door.getData() ^ 0x4));
                                door.getWorld().playEffect(door.getLocation(), Effect.DOOR_TOGGLE, 0);
                                if (topHalf.getType() == Material.WOODEN_DOOR || topHalf.getType() == Material.FENCE_GATE || topHalf.getType() == Material.TRAP_DOOR) {
                                    topHalf.setData((byte) (topHalf.getData() ^ 0x4));
                                }
                            }
                        }.runTaskLater(plugin, 100);
                    }
                    return;
                }
                Material blockMat = event.getClickedBlock().getType();
                Material mat = event.hasItem() ? event.getItem().getType() : Material.AIR;
                boolean canPlaceOutdoor = mat == Material.TNT && blockMat != Material.WOODEN_DOOR && blockMat != Material.FENCE_GATE && blockMat != Material.WOOD_DOOR && (State.isState(State.ASSAULT) || State.isState(State.DEATHMATCH)) || mat == Material.FLINT_AND_STEEL && blockMat != Material.WOODEN_DOOR && blockMat != Material.FENCE_GATE && blockMat != Material.WOOD_DOOR;
                if (!canPlaceOutdoor) {
                    for (Team team : Team.values()) {
                        if (team == Team.SPEC || team == playerTeam || team.getCuboid() == null) {
                            continue;
                        } else if (team.getCuboid().contains(loc)) {
                            event.setCancelled(true);
                            if (event.hasItem() && event.getItem().getType().isBlock()) {
                                player.damage(0.5D);
                                player.sendMessage(FKPlugin.prefix + ChatColor.RED + "Vous ne pouvez pas poser de blocs dans les bases ennemies.");
                            }
                            break;
                        }
                    }
                }
            }
        } else if (Step.isStep(Step.LOBBY)) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
            if (event.hasItem()) {
                ItemStack item = event.getItem();
                if (item.getType() == Material.WOOD_AXE && player.isOp()) {
                    event.setCancelled(true);
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        player.removeMetadata("pos1", plugin);
                        player.setMetadata("pos1", new FixedMetadataValue(plugin, plugin.toString(event.getClickedBlock().getLocation())));
                        player.sendMessage(ChatColor.GREEN + "Vous venez de séléctionner le point " + ChatColor.AQUA + "1.");
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        player.removeMetadata("pos2", plugin);
                        player.setMetadata("pos2", new FixedMetadataValue(plugin, plugin.toString(event.getClickedBlock().getLocation())));
                        player.sendMessage(ChatColor.GREEN + "Vous venez de séléctionner le point " + ChatColor.AQUA + "2.");
                        player.sendMessage(ChatColor.GRAY + "Tapez " + ChatColor.GOLD + "/fk setcuboid <couleur>" + ChatColor.GRAY + " pour confirmer votre sélection.");
                    }
                }                    else if ((item.getType() == Material.NAME_TAG) && (item.hasItemMeta())) {
                    player.openInventory(FKPlugin.getGameAPI().getKitInventory(player));
                    }
                     else if (item.getType() == Material.INK_SACK && item.hasItemMeta()) {
                    for (Team team : Team.values()) {
                        if (item.isSimilar(team.getIcon())) {
                            String displayName = team.getDisplayName();
                            Team playerTeam = Team.getPlayerTeam(player);
                            if (playerTeam != team) {
                                if (Bukkit.getOnlinePlayers().length > 1 && team.getOnlinePlayers().size() >= 1 && team.getOnlinePlayers().size() >= MathUtils.ceil(Bukkit.getOnlinePlayers().length / (Team.values().length - 1))) {
                                    player.sendMessage(FKPlugin.prefix + ChatColor.GRAY + "Impossible de rejoindre cette équipe, trop de joueurs !");
                                } else {
                                    if (playerTeam != null) {
                                        playerTeam.removePlayer(player);
                                    }
                                    team.addPlayer(player);
                                    player.sendMessage(FKPlugin.prefix + ChatColor.GRAY + "Vous rejoignez l'équipe " + team.getColor() + displayName);
                                }
                            }
                            break;
                        }
                    }
                    player.updateInventory();
                }
            }
        }
    }
}
