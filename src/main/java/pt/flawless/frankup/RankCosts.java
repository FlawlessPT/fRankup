package pt.flawless.frankup;

/** Custo do rankup {@code n} = base × multiplicador^n, arredondado ao euro; tudo em cêntimos. */
public record RankCosts(long baseEuros, double multiplier) {
    public long cost(int n) {
        return Math.round(baseEuros * Math.pow(multiplier, n)) * 100L;
    }
}
