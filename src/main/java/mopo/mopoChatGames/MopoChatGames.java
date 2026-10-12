package mopo.mopoChatGames;

import mopo.mopoChatGames.Commands.RunChatGameCMD;
import mopo.mopoChatGames.Listeners.chatListener;
import mopo.mopoLib.inventories.BufferManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MopoChatGames extends JavaPlugin {

    private static MopoChatGames INSTANCE;
    private BufferManager bufferManager;
    private boolean activeGame;
    private String answer;


    @Override
    public void onEnable() {
        INSTANCE = this;
        bufferManager = new BufferManager();
        activeGame = false;
        answer = null;

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new chatListener(), this);

        saveDefaultConfig();

        getCommand("runchatgame").setExecutor(new RunChatGameCMD());
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

    public boolean isActiveGame() {
        return activeGame;
    }

    public void setActiveGame(boolean activeGame) {
        this.activeGame = activeGame;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
