package pt.flawless.frankup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class RankupCommand implements CommandExecutor {
    private final RankService service;
    private final Function<String, String> messages;

    public RankupCommand(RankService service, Function<String, String> messages) {
        this.service = service;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.apply("only-players"));
            return true;
        }
        service.rankup(player);
        return true;
    }
}
