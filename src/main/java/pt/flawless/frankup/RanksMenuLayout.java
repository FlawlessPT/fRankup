package pt.flawless.frankup;

/** Layout fixo do menu (54 slots, moldura à volta); os 7 tiers ficam na linha do meio. */
public final class RanksMenuLayout {
    public static final int SIZE = 54;
    private static final int FIRST_TIER_SLOT = 19;

    private RanksMenuLayout() {}

    public static boolean isBorder(int slot) {
        int row = slot / 9, col = slot % 9;
        return row == 0 || row == 5 || col == 0 || col == 8;
    }

    public static int tierSlot(int tier) {
        return FIRST_TIER_SLOT + tier;
    }
}
