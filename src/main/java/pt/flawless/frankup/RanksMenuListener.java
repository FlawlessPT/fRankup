package pt.flawless.frankup;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/** Os menus cancelam cliques e arrastos; no {@code /rank}, clicar no item central corre {@code /rankup}. */
public class RanksMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Object holder = e.getInventory().getHolder();
        if (holder instanceof RanksMenu) e.setCancelled(true);
        if (holder instanceof RankMenu) {
            e.setCancelled(true);
            if (e.getRawSlot() == RankMenu.CENTER && e.getWhoClicked() instanceof Player p) {
                p.closeInventory();
                p.performCommand("rankup");
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Object holder = e.getInventory().getHolder();
        if (holder instanceof RanksMenu || holder instanceof RankMenu) e.setCancelled(true);
    }
}
