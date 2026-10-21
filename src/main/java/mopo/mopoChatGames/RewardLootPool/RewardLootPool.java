package mopo.mopoChatGames.RewardLootPool;

import mopo.mopoChatGames.MopoChatGames;
import mopo.mopoLib.inventories.BufferManager;
import mopo.mopoLib.nbt.NBT;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.random.RandomGenerator;

public class RewardLootPool {
    private static final String BUFFER_NAME = "rewardlootpool";
    private static final String WEIGHT_KEY = "reward_weight";
    private static final double DEFAULT_WEIGHT = 1.0;

    private final BufferManager bufferManager = MopoChatGames.getINSTANCE().getBufferManager();
    private List<Map.Entry<ItemStack, Double>> lootPool = new ArrayList<>();

    public void addItemToLootPool(ItemStack item, double weight) {
        Objects.requireNonNull(item, "item");
        if (!Double.isFinite(weight) || weight <= 0.0) {
            throw new IllegalArgumentException("Reward weight must be a positive, finite number");
        }

        bufferManager.createOrGetBuffer(BUFFER_NAME);
        ItemStack weightedItem = item.clone();
        NBT.modify(weightedItem, nbt -> {
            nbt.setDouble(WEIGHT_KEY, weight);
        });

        if (bufferManager.addItemToBuffer(BUFFER_NAME, weightedItem) >= 0) {
            lootPool.add(Map.entry(weightedItem, weight));
        }
    }

    public void setLootPool(Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        bufferManager.createOrGetBuffer(BUFFER_NAME);
        List<Map.Entry<ItemStack, Double>> updatedLootPool = new ArrayList<>();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }

            ItemStack weightedItem = item.clone();
            double weight = NBT.get(weightedItem, nbt -> nbt.hasTag(WEIGHT_KEY)
                    ? nbt.getDouble(WEIGHT_KEY)
                    : DEFAULT_WEIGHT);
            if (!Double.isFinite(weight) || weight <= 0.0) {
                throw new IllegalArgumentException("Reward weight must be a positive, finite number");
            }
            NBT.modify(weightedItem, nbt -> {
                nbt.setDouble(WEIGHT_KEY, weight);
            });
            inventory.setItem(slot, weightedItem);
            updatedLootPool.add(Map.entry(weightedItem, weight));
        }

        bufferManager.saveInventoryToBuffer(BUFFER_NAME, inventory);
        lootPool = updatedLootPool;
    }

    public List<Map.Entry<ItemStack, Double>> getLootPool() {
        return lootPool.isEmpty() ? null : lootPool;
    }

    public void openLootPoolInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(
                new RewardLootPoolHolder(),
                54,
                MiniMessage.miniMessage().deserialize("<aqua>Reward LootPool")
        );
        bufferManager.createOrGetBuffer(BUFFER_NAME);
        bufferManager.loadInventoryFromBuffer(BUFFER_NAME, inventory);
        player.openInventory(inventory);
    }

    public ItemStack chooseRandomReward() {
        double randomNumber = RandomGenerator.getDefault().nextDouble(0, 100);
        for (int i = 0; i <= lootPool.size() ; i++) {
            Map.Entry<ItemStack, Double> entry = lootPool.get(i);
            Double entryValue = entry.getValue();

        }
        return  null;
    }

    public void updatePercentages() {
        if (lootPool.isEmpty()) {
            return;
        }

        double totalWeight = 0;
        for (Map.Entry<ItemStack, Double> reward : lootPool) {
            totalWeight += reward.getValue();
        }

        List<Map.Entry<ItemStack, Double>> updatedLootPool = new ArrayList<>();
        for (Map.Entry<ItemStack, Double> reward : lootPool) {
            double percentage = reward.getValue() / totalWeight * 100.0;
            NBT.modify(reward.getKey(), nbt -> {
                nbt.setDouble(WEIGHT_KEY, percentage);
            });
            updatedLootPool.add(Map.entry(reward.getKey(), percentage));
        }
        lootPool = updatedLootPool;
    }
}
