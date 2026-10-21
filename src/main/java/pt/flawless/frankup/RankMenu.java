package pt.flawless.frankup;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pt.flawless.fapi.enums.FInventorySize;
import pt.flawless.fapi.inventory.FInventory;
import pt.flawless.fapi.items.FItemStack;

/** Menu {@code /rank}: um item ao centro com o rank atual; clicar nele corre {@code /rankup}. */
public class RankMenu implements InventoryHolder {
    public static final int CENTER = 13;

    private final Player player;
    private final Inventory inventory;

    public RankMenu(Player player, RankService service, int index, long balance) {
        this.player = player;
        this.inventory = new FInventory(this, FInventorySize.SIZE_27, "§8O teu rank").getInventory();
        ItemStack glass = new FItemStack(Material.STAINED_GLASS_PANE, 1)
                .setDisplayName(" ").setDurability((short) 7).createItem();
        for (int i = 0; i < 27; i++) inventory.setItem(i, glass);
        inventory.setItem(CENTER, item(service, index, balance));
    }

    private static ItemStack item(RankService service, int index, long balance) {
        RankTier tier = RankTier.of(index);
        FItemStack item = new FItemStack(tier.icon, 1).setDisplayName("§7Rank atual: " + RankTier.label(index));
        if (index >= RankTier.TOTAL - 1) {
            item.addLoreLine("§7Estás no rank máximo!");
        } else {
            long cost = service.costs().cost(index);
            item.addLoreLine("§7Próximo: " + RankTier.label(index + 1));
            item.addLoreLine("§7Custo: §a" + service.economy().format(cost));
            item.addLoreLine(balance >= cost ? "§7Falta: §a0,00€"
                    : "§7Falta: §c" + service.economy().format(cost - balance));
            item.addLoreLine(" ");
            item.addLoreLine("§eClica para subir de rank");
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
