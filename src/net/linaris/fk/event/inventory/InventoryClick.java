package net.linaris.fk.event.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.linaris.fk.FKPlugin;
import net.linaris.fk.event.FKListener;
import net.linaris.fk.handler.Step;

public class InventoryClick extends FKListener
{
  public InventoryClick(FKPlugin plugin)
  {
    super(plugin);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    ItemStack current = event.getCurrentItem();
    if (Step.isStep(Step.LOBBY)) {
      if (!event.getWhoClicked().isOp()) {
        event.setCancelled(true);
      }
      FKPlugin.getGameAPI().onInventoryClick(event);
    }
  }
}