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
    private static final String RAW_WEIGHT_KEY = "raw_weight_key";
    private static final double DEFAULT_WEIGHT = 1.0;

    private final BufferManager bufferManager = MopoChatGames.getINSTANCE().getBufferManager();
    private List<Map.Entry<ItemStack, Double>> lootPool = new ArrayList<>();

    public void addItemToLootPool(ItemStack item, double rawWeight) {
        Objects.requireNonNull(item, "item");
        if (!isValidWeight(rawWeight)) {
            throw new IllegalArgumentException("Reward weight must be a positive, finite number");
        }

        bufferManager.createOrGetBuffer(BUFFER_NAME);
        ItemStack weightedItem = item.clone();
        NBT.modify(weightedItem, nbt -> {
            nbt.setDouble(RAW_WEIGHT_KEY, rawWeight);
        });

        if (bufferManager.addItemToBuffer(BUFFER_NAME, weightedItem) >= 0) {
            lootPool.add(Map.entry(weightedItem, rawWeight));
        }
    }

    public void addItemToLootPool(ItemStack item) {
        addItemToLootPool(item, DEFAULT_WEIGHT);
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
            double rawWeight = readRawWeight(weightedItem);
            if (!isValidWeight(rawWeight)) {
                throw new IllegalArgumentException("Reward weight must be a positive, finite number");
            }
            NBT.modify(weightedItem, nbt -> {
                nbt.setDouble(RAW_WEIGHT_KEY, rawWeight);
            });
            inventory.setItem(slot, weightedItem);
            updatedLootPool.add(Map.entry(weightedItem, rawWeight));
        }

        bufferManager.saveInventoryToBuffer(BUFFER_NAME, inventory);
        lootPool = updatedLootPool;
    }

    public List<Map.Entry<ItemStack, Double>> getLootPool() {
        if (lootPool.isEmpty()) {
            List<ItemStack> bufferedItems = bufferManager.getBufferAsList(BUFFER_NAME);
            if (bufferedItems != null) {
                for (ItemStack itemStack : bufferedItems) {
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    double rawWeight = readRawWeight(itemStack);
                    if (isValidWeight(rawWeight)) {
                        lootPool.add(Map.entry(itemStack, rawWeight));
                    }
                }
            }
        }
        List<Map.Entry<ItemStack, Double>> newLootPool = new ArrayList<>();
        for (Map.Entry<ItemStack, Double> reward : lootPool) {
            newLootPool.add(Map.entry(reward.getKey().clone(), reward.getValue()));
        }
        return newLootPool;
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
        if (lootPool.isEmpty()) {
            return null;
        }

        double randomNumber = RandomGenerator.getDefault().nextDouble(0, getTotalWeight());
        double cumulative = 0;
        for (Map.Entry<ItemStack, Double> entry : lootPool) {
            cumulative += entry.getValue();
            if (randomNumber < cumulative) {
                return entry.getKey().clone();
            }
        }
        // Floating-point rounding can leave the roll just past the last entry
        return lootPool.get(lootPool.size() - 1).getKey().clone();
    }

    public double getTotalWeight() {
        double totalWeight = 0;
        for (Map.Entry<ItemStack, Double> reward : lootPool) {
            totalWeight += reward.getValue();
        }
        return totalWeight;
    }

    public double getPercentage(Map.Entry<ItemStack, Double> reward) {
        double totalWeight = getTotalWeight();
        if (totalWeight <= 0.0) {
            return 0.0;
        }
        return reward.getValue() / totalWeight * 100.0;
    }

    private double readRawWeight(ItemStack item) {
        return NBT.get(item, nbt -> {
            if (nbt.hasTag(RAW_WEIGHT_KEY)) {
                return nbt.getDouble(RAW_WEIGHT_KEY);
            } else {
                return DEFAULT_WEIGHT;
            }
        });
    }

    private boolean isValidWeight(double weight) {
        return Double.isFinite(weight) && weight > 0.0;
    }
}
