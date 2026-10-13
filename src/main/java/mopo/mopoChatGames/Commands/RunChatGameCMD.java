package mopo.mopoChatGames.Commands;

import mopo.mopoChatGames.Config.ConfigData;
import mopo.mopoChatGames.MopoChatGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.random.RandomGenerator;

public class RunChatGameCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if ((sender instanceof Player player)) {
            if (args.length == 0) {
            player.sendMessage("Usage: /" + label + " <game>");
                return true;
            }

            MopoChatGames instance = MopoChatGames.getINSTANCE();
            instance.setActiveGame(true);

            String message;
            final FileConfiguration config = instance.getConfig();
            switch (args[0].toLowerCase()) {
                case "math" -> {
                    if (!config.getBoolean("Math.enabled")) {
                        player.sendMessage("This chat game is not enabled, please enable in the config!");
                    }
                    List<Character> operators = ConfigData.getMathOperators();
                    if (operators.isEmpty()) {
                        sender.sendMessage("No math operators are configured.");
                        return true;
                    }

                    RandomGenerator random = RandomGenerator.getDefault();
                    int a = random.nextInt(ConfigData.getMathMin(), ConfigData.getMathMax() + 1);
                    int b = random.nextInt(ConfigData.getMathMin(), ConfigData.getMathMax() + 1);
                    char operator = operators.get(random.nextInt(operators.size()));
                    while (operator == '/') {
                        int remainder = a / b;
                        if (remainder != 0) {
                            b++;
                        }
                    }
                    String equation = a + " " + operator + " " + b;
                    message = ConfigData.getMathMessage().replace("{equation}", equation);
                    int answer = a + operator + b;
                    instance.setActiveGame("math");
                    instance.setAnswer(String.valueOf(answer));
                }
                case "repeatword" -> {
                    if (!config.getBoolean("RepeatWord.enabled")) {
                        player.sendMessage("This chat game is disabled, please enable in config!");
                    }

                    List<String> wordlist = ConfigData.getRepeatWordList();
                    if (wordlist.isEmpty()) {
                        player.sendMessage("No words in config");
                    }
                    RandomGenerator random = RandomGenerator.getDefault();
                    int randomIndex = random.nextInt(0, wordlist.size() - 1);
                    String chosenWord = wordlist.get(randomIndex);

                    ConfigData.getMathMessage().replace("{word}", chosenWord);
                    instance.setActiveGame("repeatword");
                    instance.setAnswer(chosenWord);
                }
                default -> {
                    player.sendMessage("Unknown game: " + args[0]);
                    return true;
                }
            }

            MiniMessage miniMessage = MiniMessage.miniMessage();
            Component broadcast = miniMessage.deserialize(message);
            sender.getServer().broadcast(broadcast);
            return true;
        } else {
            sender.sendMessage("only players can run this command.");
        }

        return true;
    }
}
