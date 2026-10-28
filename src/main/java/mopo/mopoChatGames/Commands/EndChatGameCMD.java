package mopo.mopoChatGames.Commands;

import mopo.mopoChatGames.MopoChatGames;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndChatGameCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        MopoChatGames instance = MopoChatGames.getINSTANCE();
        FileConfiguration config = instance.getConfig();

        if (sender instanceof Player player) {
            if (instance.getActiveGame() != null) {
                instance.setAnswer(null);
                instance.setActiveGame(null);
                instance.getServer().getScheduler().runTask(instance, bukkitTask -> {
                    Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<red>Chat game ended"));
                });
            } else {
                player.sendMessage("No active chat game to cancel");
            }
        }

        return true;
    }
}
