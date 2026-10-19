package pt.flawless.frankup;

import org.bukkit.Material;

/** Os tiers do LoL até Diamond; cada um tem 5 divisões (5 a 1). */
public enum RankTier {
    IRON("Iron", "§7", Material.IRON_INGOT),
    BRONZE("Bronze", "§6", Material.CLAY_BRICK),
    SILVER("Silver", "§f", Material.QUARTZ),
    GOLD("Gold", "§e", Material.GOLD_INGOT),
    PLATINUM("Platinum", "§3", Material.PRISMARINE_CRYSTALS),
    EMERALD("Emerald", "§a", Material.EMERALD),
    DIAMOND("Diamond", "§b", Material.DIAMOND);

    public static final int DIVISIONS = 5;
    /** Número de ranks; o índice 0 é Iron 5 e o último é Diamond 1. */
    public static final int TOTAL = values().length * DIVISIONS;

    public final String displayName, color;
    public final Material icon;

    RankTier(String displayName, String color, Material icon) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
    }

    public static RankTier of(int index) {
        return values()[index / DIVISIONS];
    }

    private static final String[] ROMAN = {"I", "II", "III", "IV", "V"};

    /** Tag para chat e scoreboard, por exemplo {@code §7[IronV]}. */
    public static String tag(int index) {
        return of(index).color + "[" + of(index).displayName + ROMAN[DIVISIONS - index % DIVISIONS - 1] + "]";
    }

    /** Só o numeral, por exemplo {@code §7[V]}. */
    public static String numeral(int index) {
        return of(index).color + "[" + ROMAN[DIVISIONS - index % DIVISIONS - 1] + "]";
    }

    /** Rank com cores, por exemplo {@code §7Iron 5}. */
    public static String label(int index) {
        return of(index).color + of(index).displayName + " " + (DIVISIONS - index % DIVISIONS);
    }
}
