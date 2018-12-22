package net.linaris.fk.event.server;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;

public class ServerCommand extends FKListener {

    public ServerCommand(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().split(" ")[0].contains("reload")) {
            event.setCommand("/reload");
            event.getSender().sendMessage(ChatColor.RED + "Cette fonctionnalité est désactivée par le plugin Fallen Kingdoms à cause de contraintes techniques (reset de map).");
        }
    }
}
