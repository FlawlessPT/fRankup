package pt.flawless.frankup;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import pt.flawless.fapi.database.DatabaseService;
import pt.flawless.fapi.database.Databases;
import pt.flawless.fapi.economy.Economies;
import pt.flawless.fapi.enums.EMessageTypeEnum;
import pt.flawless.fapi.logs.FConsoleLogger;
import pt.flawless.fapi.ranks.RankApi;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class FRankup extends JavaPlugin {
    private final FConsoleLogger consoleLogger = new FConsoleLogger(this.getName());
    private boolean enabledOk = false;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            DatabaseService database = Databases.get();
            database.migrate(this, List.of("db/frankup/V001__create_players.sql"));
            Function<String, String> messages = key -> getConfig().getString("messages." + key, key);
            RankCosts costs = new RankCosts(getConfig().getLong("base", 100000), getConfig().getDouble("multiplier", 2.0));
            RankService service = new RankService(this, new SqlRankRepository(database), Economies.get(), costs, messages);

            getServer().getPluginManager().registerEvents(new RanksMenuListener(), this);
            getCommand("rankup").setExecutor(new RankupCommand(service, messages));
            getCommand("setrank").setExecutor(new SetRankCommand(service, messages));
            getCommand("ranks").setExecutor(new RanksCommand(this, service, messages));
            getServer().getServicesManager().register(RankApi.class, service, this, ServicePriority.Normal);
        } catch (SQLException | RuntimeException e) {
            // inclui DatabaseUnavailableException e EconomyUnavailableException (RuntimeException)
            consoleLogger.sendMessage("não foi possível iniciar os ranks: " + e.getMessage(), EMessageTypeEnum.ERROR);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        enabledOk = true;
        consoleLogger.sendEnablePluginMessage();
    }

    @Override
    public void onDisable() {
        if (enabledOk) {
            consoleLogger.sendDisablePluginMessage();
        }
    }
}
