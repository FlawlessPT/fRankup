package pt.flawless.frankup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pt.flawless.fapi.economy.EconomyApi;
import pt.flawless.fapi.economy.InsufficientFundsException;
import pt.flawless.fapi.ranks.RankApi;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RankService implements RankApi {
    private static final long CONFIRM_MS = 15_000L;
    private static final int LAST = RankTier.TOTAL - 1;

    private record Pending(int from, long expires) {}

    private final Plugin plugin;
    private final RankRepository repository;
    private final EconomyApi economy;
    private final RankCosts costs;
    private final Function<String, String> messages;
    // shortcut: uma entrada por jogador que pediu rankup, só removida ao confirmar; aceitável até haver evento de saída.
    private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

    public RankService(Plugin plugin, RankRepository repository, EconomyApi economy, RankCosts costs,
                       Function<String, String> messages) {
        this.plugin = plugin;
        this.repository = repository;
        this.economy = economy;
        this.costs = costs;
        this.messages = messages;
    }

    public RankCosts costs() {
        return costs;
    }

    public EconomyApi economy() {
        return economy;
    }

    public CompletableFuture<Integer> getIndex(UUID uuid) {
        return repository.getIndex(uuid);
    }

    /** Tag com cores, por exemplo {@code §7[IronV]} (chat e scoreboard). */
    @Override
    public CompletableFuture<String> getRankLabel(UUID uuid) {
        return repository.getIndex(uuid).thenApply(RankTier::tag);
    }

    @Override
    public CompletableFuture<String> getRankNumeral(UUID uuid) {
        return repository.getIndex(uuid).thenApply(RankTier::numeral);
    }

    /** Primeiro uso pede confirmação; o segundo, dentro de 15 s, cobra e sobe. */
    public void rankup(Player player) {
        UUID uuid = player.getUniqueId();
        repository.getIndex(uuid).thenCompose(idx -> {
            if (idx >= LAST) {
                tell(uuid, "max-rank", "{atual}", RankTier.label(idx));
                return CompletableFuture.<Void>completedFuture(null);
            }
            long cost = costs.cost(idx);
            Pending p = pending.get(uuid);
            if (p != null && p.from() == idx && p.expires() > System.currentTimeMillis()) {
                pending.remove(uuid);
                return charge(uuid, idx, cost);
            }
            return economy.getBalance(uuid).thenAccept(balance -> {
                if (balance < cost) {
                    notEnough(uuid, idx, cost, balance);
                    return;
                }
                pending.put(uuid, new Pending(idx, System.currentTimeMillis() + CONFIRM_MS));
                tell(uuid, "confirm", "{atual}", RankTier.label(idx), "{proximo}", RankTier.label(idx + 1),
                        "{custo}", economy.format(cost));
            });
        }).exceptionally(e -> {
            plugin.getLogger().warning("rankup falhou: " + unwrap(e));
            tell(uuid, "failed");
            return null;
        });
    }

    /** Cobra primeiro; se o rank já mudou entretanto devolve o dinheiro. */
    private CompletableFuture<Void> charge(UUID uuid, int idx, long cost) {
        return economy.withdraw(uuid, cost).handle((balance, e) -> {
            if (e != null) {
                if (unwrap(e) instanceof InsufficientFundsException) {
                    return economy.getBalance(uuid).thenAccept(b -> notEnough(uuid, idx, cost, b));
                }
                throw new CompletionException(unwrap(e));
            }
            return repository.advance(uuid, idx).handle((ok, e2) -> {
                if (e2 != null || !ok) {
                    economy.deposit(uuid, cost);
                    tell(uuid, "failed");
                } else {
                    tell(uuid, "success", "{proximo}", RankTier.label(idx + 1), "{custo}", economy.format(cost));
                }
                return (Void) null;
            });
        }).thenCompose(f -> f);
    }

    private void notEnough(UUID uuid, int idx, long cost, long balance) {
        tell(uuid, "not-enough", "{custo}", economy.format(cost), "{proximo}", RankTier.label(idx + 1),
                "{saldo}", economy.format(balance));
    }

    private static Throwable unwrap(Throwable e) {
        return e instanceof CompletionException && e.getCause() != null ? e.getCause() : e;
    }

    /** Envia a mensagem na thread principal; {@code kv} são pares placeholder/valor. */
    private void tell(UUID uuid, String key, String... kv) {
        String text = messages.apply(key);
        for (int i = 0; i < kv.length; i += 2) text = text.replace(kv[i], kv[i + 1]);
        String out = text;
        try {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) p.sendMessage(out);
            });
        } catch (RuntimeException e) {
            // plugin desligado entretanto
        }
    }
}
