package mopo.mopoChatGames.Commands;

import mopo.mopoChatGames.MopoChatGames;
import mopo.mopoChatGames.RewardLootPool.RewardLootPool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpenRewardLootPoolCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            MopoChatGames instance = MopoChatGames.getINSTANCE();
            RewardLootPool rewardLootPool = instance.getRewardLootPool();
            rewardLootPool.openLootPoolInventory(player);
        }
        return true;
    }
}
