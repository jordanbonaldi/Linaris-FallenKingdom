package net.linaris.fk.event.player;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;
import net.linaris.fk.handler.Team;

public class AsyncPlayerChat extends FKListener {
   public AsyncPlayerChat(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
    	
        Player player = event.getPlayer();
        Team playerTeam = Team.getPlayerTeam(player);
    	if(Step.isStep(Step.LOBBY)){
       
        	
    		if(player.getName().equals("Neferett")){
				
			event.setFormat(" §f§l[§c§lFondateur§f§l] §b§l" + player.getName()+"§f: §c"+ event.getMessage());
			}
			else if(player.hasPermission("game.megavip")) {
				event.setFormat(" §f[§aMegaVip§f] §a" + player.getName()+"§f: "+ event.getMessage());
			}else if(player.hasPermission("game.vip")){
				event.setFormat(" §f[§eVip§f] §e" + player.getName()+"§f: "+ event.getMessage());
				
			}else if(player.hasPermission("game.modo")){
				event.setFormat(" §f[§6Modo§f] §6" + player.getName()+"§f: §c"+ event.getMessage());
				
			}else if(player.hasPermission("game.admin")){
				event.setFormat(" §f[§cAdmin§f] §c" + player.getName()+"§f: §c"+ event.getMessage());
			}else if(player.hasPermission("game.vipelite")) {
				event.setFormat(" §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage());
			}else{
				event.setFormat(" §7" + player.getName()+"§f: "+ event.getMessage());	
			}
    	}
    	
        if (Step.isStep(Step.IN_GAME)) {
            if (playerTeam == Team.SPEC || !event.getMessage().startsWith("!")) {
                if (playerTeam != Team.SPEC) {
                	if(player.getName().equals("Neferett")){
            			
            			event.setFormat("§f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] "+ "§f§l[§c§lFondateur§f§l] §b§l" + player.getName()+"§f: §c"+ event.getMessage());
            			}
            			else if(player.hasPermission("game.megavip")) {
            				event.setFormat("§f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + "§f[§aMegaVip§f] §a" + player.getName()+"§f: "+ event.getMessage());
            			}else if(player.hasPermission("game.vip")){
            				event.setFormat("§f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§eVip§f] §e" + player.getName()+"§f: "+ event.getMessage());
            				
            			}else if(player.hasPermission("game.modo")){
            				event.setFormat("§f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§6Modo§f] §6" + player.getName()+"§f: §c"+ event.getMessage());
            				
            			}else if(player.hasPermission("game.admin")){
            				event.setFormat("§f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§cAdmin§f] §c" + player.getName()+"§f: §c"+ event.getMessage());
            			}else if(player.hasPermission("game.vipelite")) {
            				event.setFormat("§f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage());
            			}else{
            				event.setFormat("§f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f]" + " §7" + player.getName()+"§f: "+ event.getMessage());	
            			}
                }
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Team team = Team.getPlayerTeam(online);
                    if (team != null && team != playerTeam) {
                        event.getRecipients().remove(online);
                    }
                }
            } else {
            	if(player.getName().equals("Neferett")){
        			
        			event.setFormat("§a(Tous) §f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] "+ "§f§l[§c§lFondateur§f§l] §b§l" + player.getName()+"§f: §c"+ event.getMessage().replaceFirst("!", ""));
        			}
        			else if(player.hasPermission("game.megavip")) {
        				event.setFormat("§a(Tous) §f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + "§f[§aMegaVip§f] §a" + player.getName()+"§f: "+ event.getMessage().replaceFirst("!", ""));
        			}else if(player.hasPermission("game.vip")){
        				event.setFormat("§a(Tous) §f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§eVip§f] §e" + player.getName()+"§f: "+ event.getMessage().replaceFirst("!", ""));
        				
        			}else if(player.hasPermission("game.modo")){
        				event.setFormat("§a(Tous) §f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§6Modo§f] §6" + player.getName()+"§f: §c"+ event.getMessage().replaceFirst("!", ""));
        				
        			}else if(player.hasPermission("game.admin")){
        				event.setFormat("§a(Tous) §f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§cAdmin§f] §c" + player.getName()+"§f: §c"+ event.getMessage().replaceFirst("!", ""));
        			}else if(player.hasPermission("game.vipelite")) {
        				event.setFormat("§a(Tous) §f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f] " + " §f[§bVipElite§f] §b" + player.getName()+"§f: " + event.getMessage().replaceFirst("!", ""));
        			}else{
        				event.setFormat("§a(Tous) §f["+playerTeam.getColor()+ StringUtils.capitalize(playerTeam.getDisplayName())+"§f]" + " §7" + player.getName()+"§f: "+ event.getMessage().replaceFirst("!", ""));	
        			}
            }
        }
    }
}