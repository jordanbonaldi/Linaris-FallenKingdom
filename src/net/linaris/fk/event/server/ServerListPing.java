package net.linaris.fk.event.server;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.State;
import net.linaris.fk.handler.Step;
import net.linaris.fk.scheduler.BeginCountdown;
import net.linaris.fk.scheduler.GameTask;

public class ServerListPing extends FKListener {
    public ServerListPing(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            if (BeginCountdown.started) {
                int remainingMins = BeginCountdown.timeUntilStart / 60 % 60;
                int remainingSecs = BeginCountdown.timeUntilStart % 60;
                event.setMotd(ChatColor.GREEN + "Début : " + (remainingMins > 0 ? remainingMins + "mn" : remainingSecs + "s"));
            } else {
                event.setMotd(Step.getMOTD());
            }
        } else if (Step.isStep(Step.IN_GAME)) {
            event.setMotd(ChatColor.YELLOW + "J" + GameTask.day + ">" + State.getState().getName());
        } else {
            event.setMotd(Step.getMOTD());
        }
    }
}
