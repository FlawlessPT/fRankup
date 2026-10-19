package pt.flawless.frankup;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.function.Function;

public class RanksCommand implements CommandExecutor {
    private final Plugin plugin;
    private final RankService service;
    private final Function<String, String> messages;

    public RanksCommand(Plugin plugin, RankService service, Function<String, String> messages) {
        this.plugin = plugin;
        this.service = service;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.apply("only-players"));
            return true;
        }
        UUID uuid = player.getUniqueId();
        // O rank vem da BD (assíncrono); o inventário abre-se na thread principal.
        service.getIndex(uuid).whenComplete((idx, e) -> Bukkit.getScheduler().runTask(plugin, () -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;
            if (e != null) {
                p.sendMessage(messages.apply("failed"));
                return;
            }
            new RanksMenu(p, service, idx).open();
        }));
        return true;
    }
}
