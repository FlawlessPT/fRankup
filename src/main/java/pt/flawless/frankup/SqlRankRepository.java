package pt.flawless.frankup;

import pt.flawless.fapi.database.DatabaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Acesso à tabela {@code frankup_players}; corre no pool do {@link DatabaseService}. */
public class SqlRankRepository implements RankRepository {
    private static final String SELECT = "SELECT rank_index FROM frankup_players WHERE uuid = ?";
    private static final String INSERT_FIRST = "INSERT IGNORE INTO frankup_players (uuid, rank_index) VALUES (?, 1)";
    private static final String UPDATE = "UPDATE frankup_players SET rank_index = ? WHERE uuid = ? AND rank_index = ?";

    private final DatabaseService database;

    public SqlRankRepository(DatabaseService database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<Integer> getIndex(UUID uuid) {
        return database.supplyAsync(c -> {
            try (PreparedStatement ps = c.prepareStatement(SELECT)) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> advance(UUID uuid, int from) {
        return database.supplyAsync(c -> {
            // Sem linha = Iron 5: o primeiro rankup cria a linha já no índice 1.
            if (from == 0) {
                try (PreparedStatement ps = c.prepareStatement(INSERT_FIRST)) {
                    ps.setString(1, uuid.toString());
                    return ps.executeUpdate() > 0;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(UPDATE)) {
                ps.setInt(1, from + 1);
                ps.setString(2, uuid.toString());
                ps.setInt(3, from);
                return ps.executeUpdate() > 0;
            }
        });
    }
}
