package pt.flawless.frankup;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pt.flawless.fapi.enums.FInventorySize;
import pt.flawless.fapi.inventory.FInventory;
import pt.flawless.fapi.items.FItemStack;

/** Menu {@code /ranks}. O holder identifica o inventário nos eventos. */
public class RanksMenu implements InventoryHolder {
    private final Player player;
    private final Inventory inventory;

    public RanksMenu(Player player, RankService service, int currentIndex) {
        this.player = player;
        this.inventory = new FInventory(this, FInventorySize.SIZE_54, "§8Ranks").getInventory();
        // nome " " (espaço): o Bukkit 1.8 aceita e o tooltip fica vazio
        ItemStack glass = new FItemStack(Material.GRAY_STAINED_GLASS_PANE, 1)
                .setDisplayName(" ").createItem();
        for (int i = 0; i < RanksMenuLayout.SIZE; i++) if (RanksMenuLayout.isBorder(i)) inventory.setItem(i, glass);
        for (RankTier tier : RankTier.values()) {
            inventory.setItem(RanksMenuLayout.tierSlot(tier.ordinal()), icon(tier, service, currentIndex));
        }
    }

    private static ItemStack icon(RankTier tier, RankService service, int current) {
        FItemStack item = new FItemStack(tier.icon, 1).setDisplayName(tier.color + "§l" + tier.displayName);
        int last = RankTier.TOTAL - 1;
        for (int d = 0; d < RankTier.DIVISIONS; d++) {
            int i = tier.ordinal() * RankTier.DIVISIONS + d;
            String line = i == last
                    ? RankTier.label(i) + "§7: rank máximo"
                    : RankTier.label(i) + " §7➜ " + RankTier.label(i + 1) + "§7: §a"
                            + service.economy().format(service.costs().cost(i));
            item.addLoreLine((i == current ? "§e▶ " : "§8• ") + line);
        }
        return item.createItem();
    }

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
