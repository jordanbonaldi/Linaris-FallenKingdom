package net.linaris.fk.event.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class PlayerCommandPreprocess extends FKListener {
    public PlayerCommandPreprocess(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && event.getMessage().split(" ")[0].equalsIgnoreCase("/reload")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Cette fonctionnalit� est d�sactiv�e par le plugin Fallen Kingdoms � cause de contraintes techniques (reset de map).");
        }
    }
}
