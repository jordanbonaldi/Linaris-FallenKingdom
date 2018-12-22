package net.linaris.fk.event;

import org.bukkit.event.Listener;

import net.linaris.fk.FKPlugin;

public class FKListener implements Listener{
	
	protected FKPlugin plugin;
	
	protected FKListener(FKPlugin plugin){
		
		this.plugin  = plugin;
		
	}
	
}
