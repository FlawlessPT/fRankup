package pt.flawless.frankup;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface RankRepository {
    /** Índice do rank (0 = Iron 5 se o jogador não tem linha). */
    CompletableFuture<Integer> getIndex(UUID uuid);

    /** Sobe de {@code from} para {@code from + 1}; {@code false} se o rank já não era {@code from}. */
    CompletableFuture<Boolean> advance(UUID uuid, int from);

    /** Define o rank diretamente (cria a linha se não existir). */
    CompletableFuture<Void> set(UUID uuid, int index);
}
