package net.linaris.fk.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;

public class PlayerLogin extends FKListener {
    public PlayerLogin(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (Step.canJoin() && event.getResult() == Result.KICK_FULL && player.hasPermission("games.vip")) {
            event.allow();
        } else if (!Step.canJoin() && !player.hasPermission("games.join")) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Step.getMOTD());
        }
    }
}
