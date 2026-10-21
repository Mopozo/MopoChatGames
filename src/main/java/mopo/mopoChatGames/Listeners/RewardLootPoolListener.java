package mopo.mopoChatGames.Listeners;

import mopo.mopoChatGames.MopoChatGames;
import mopo.mopoChatGames.RewardLootPool.RewardLootPool;
import mopo.mopoChatGames.RewardLootPool.RewardLootPoolHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class RewardLootPoolListener implements Listener {

    @EventHandler
    public void playerInvetoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof RewardLootPoolHolder) {
            RewardLootPool rewardLootPool = MopoChatGames.getINSTANCE().getRewardLootPool();
            rewardLootPool.setLootPool(event.getInventory());
        }
    }
}
