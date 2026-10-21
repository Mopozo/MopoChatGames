package mopo.mopoChatGames;

import mopo.mopoChatGames.Commands.EndChatGameCMD;
import mopo.mopoChatGames.Commands.OpenRewardLootPoolCMD;
import mopo.mopoChatGames.Commands.RunChatGameCMD;
import mopo.mopoChatGames.Listeners.chatListener;
import mopo.mopoChatGames.RewardLootPool.RewardLootPool;
import mopo.mopoLib.inventories.BufferManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MopoChatGames extends JavaPlugin {

    private static MopoChatGames INSTANCE;
    private BufferManager bufferManager;
    private String activeGame;
    private String answer;
    private RewardLootPool rewardLootPool;


    @Override
    public void onEnable() {
        INSTANCE = this;
        bufferManager = new BufferManager();
        activeGame = null;
        answer = null;
        rewardLootPool = new RewardLootPool();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new chatListener(), this);

        saveDefaultConfig();

        PluginCommand runChatGameCommand = getCommand("runchatgame");
        if (runChatGameCommand == null) {
            throw new IllegalStateException("Command 'runchatgame' is missing from plugin.yml");
        }
        getCommand("endchatgame").setExecutor(new EndChatGameCMD());
        getCommand("openrewardlootpool").setExecutor(new OpenRewardLootPoolCMD());
        runChatGameCommand.setExecutor(new RunChatGameCMD());
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }


    public static MopoChatGames getINSTANCE() {
        return INSTANCE;
    }

    public synchronized String getActiveGame() {
        return activeGame;
    }

    public synchronized void setActiveGame(String activeGame) {
        this.activeGame = activeGame;
    }

    public synchronized String getAnswer() {
        return answer;
    }

    public synchronized void setAnswer(String answer) {
        this.answer = answer;
    }

    public synchronized void startGame(String activeGame, String answer) {
        this.activeGame = activeGame;
        this.answer = answer;
        startGameTimer(getConfig().getInt("ChatGameTimeSeconds"));
    }

    public synchronized void startGame(String activeGame, String answer, int length) {
        this.activeGame = activeGame;
        this.answer = answer;
        startGameTimer(length);
    }

    public synchronized String completeGameIfAnswer(String submittedAnswer, boolean caseSensitive) {
        if (activeGame == null || answer == null) {
            return null;
        }

        boolean correct;
        if (caseSensitive) {
            correct = answer.equals(submittedAnswer);
        } else {
            correct = answer.equalsIgnoreCase(submittedAnswer);
        }
        if (!correct) {
            return null;
        }

        String correctAnswer = answer;
        activeGame = null;
        answer = null;
        return correctAnswer;
    }

    public void startGameTimer(int seconds) {
        long ticks = seconds * 20L;
        getINSTANCE().getServer().getScheduler().runTaskLater(getINSTANCE(), bukkitTask -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "endchatgame");
        }, ticks);
    }

    public RewardLootPool getRewardLootPool() {
        return rewardLootPool;
    }
}


