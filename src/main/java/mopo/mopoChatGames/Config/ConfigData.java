package mopo.mopoChatGames.Config;

import mopo.mopoChatGames.MopoChatGames;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigData {

    private static FileConfiguration config;

    static  {
        reload();
    }

    public static void reload() {config = MopoChatGames.getINSTANCE().getConfig();}

    public static String getMathMessage() {return  config.getString("Math.message");}
    public static int getMathMax(){return  config.getInt("Math.maxNumber");}
    public static int getMathMin(){return  config.getInt("Math.minNumber");}
    public static List<Character> getMathOperators() {return config.getCharacterList("Math.operators");}

}
