package pt.flawless.frankup;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

/** {@code /setrank <jogador> <tier> <divisão>}: força o rank sem cobrar. */
public class SetRankCommand implements CommandExecutor {
    private final RankService service;
    private final Function<String, String> messages;

    public SetRankCommand(RankService service, Function<String, String> messages) {
        this.service = service;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(messages.apply("setrank-usage"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(messages.apply("setrank-offline").replace("{jogador}", args[0]));
            return true;
        }
        int index = parse(args[1], args[2]);
        if (index < 0) {
            sender.sendMessage(messages.apply("setrank-invalid"));
            return true;
        }
        service.setIndex(target.getUniqueId(), index).thenRun(() -> Bukkit.getScheduler().runTask(service.plugin(), () ->
                sender.sendMessage(messages.apply("setrank-done").replace("{jogador}", target.getName())
                        .replace("{rank}", RankTier.label(index))))).exceptionally(e -> {
            Bukkit.getScheduler().runTask(service.plugin(), () -> sender.sendMessage(messages.apply("failed")));
            return null;
        });
        return true;
    }

    /** Índice do rank, ou -1 se o tier ou a divisão (1 a 5) forem inválidos. */
    private static int parse(String tier, String division) {
        int d;
        try {
            d = Integer.parseInt(division);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (d < 1 || d > RankTier.DIVISIONS) return -1;
        for (RankTier t : RankTier.values())
            if (t.displayName.equalsIgnoreCase(tier)) return t.ordinal() * RankTier.DIVISIONS + (RankTier.DIVISIONS - d);
        return -1;
    }
}
