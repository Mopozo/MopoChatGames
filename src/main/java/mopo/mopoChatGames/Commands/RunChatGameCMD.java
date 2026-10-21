package mopo.mopoChatGames.Commands;

import mopo.mopoChatGames.Config.ConfigData;
import mopo.mopoChatGames.MopoChatGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator;

public class RunChatGameCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("Usage: /" + label + " <game>");
            return true;
        }

        MopoChatGames instance = MopoChatGames.getINSTANCE();
        FileConfiguration config = instance.getConfig();
        String message;

        if (instance.getActiveGame() != null) {
            player.sendMessage("Chat game is already active, please wait until its completed!");
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "math" -> {
                if (!config.getBoolean("Math.enabled")) {
                    player.sendMessage("This chat game is not enabled, please enable it in the config!");
                    return true;
                }

                List<Character> configuredOperators = ConfigData.getMathOperators();
                List<Character> operators = configuredOperators.stream()
                        .filter(operator -> operator == '+' || operator == '-' || operator == '*' || operator == '/')
                        .toList();
                if (operators.isEmpty()) {
                    player.sendMessage("No valid math operators are configured.");
                    return true;
                }

                int min = ConfigData.getMathMin();
                int max = ConfigData.getMathMax();
                if (min > max || max == Integer.MAX_VALUE) {
                    player.sendMessage("The configured math number range is invalid.");
                    return true;
                }

                RandomGenerator random = RandomGenerator.getDefault();
                int a = random.nextInt(min, max + 1);
                int b = random.nextInt(min, max + 1);
                char operator = operators.get(random.nextInt(operators.size()));
                while (operator == '/') {
                    int remainder = a / b;
                    if (remainder != 0) {
                        b++;
                    }
                }

                String equation = a + " " + operator + " " + b;
                message = ConfigData.getMathMessage().replace("{equation}", equation);
                int answer = switch (operator) {
                    case '+' -> a + b;
                    case '-' -> a - b;
                    case '*' -> a * b;
                    case '/' -> a / b;
                    default -> throw new IllegalStateException("Unsupported math operator: " + operator);
                };
                instance.startGame("Math", String.valueOf(answer));
            }
            case "repeatword" -> {
                if (!config.getBoolean("RepeatWord.enabled")) {
                    player.sendMessage("This chat game is disabled, please enable it in config!");
                    return true;
                }

                List<String> wordlist = ConfigData.getRepeatWordList();
                if (wordlist.isEmpty()) {
                    player.sendMessage("No words are configured for the repeat-word game.");
                    return true;
                }

                RandomGenerator random = RandomGenerator.getDefault();
                String chosenWord = wordlist.get(random.nextInt(wordlist.size()));
                message = ConfigData.getRepeatWordMessage().replace("{word}", chosenWord);
                instance.startGame("RepeatWord", chosenWord);
            }
            default -> {
                player.sendMessage("Unknown game: " + args[0]);
                return true;
            }
        }

        Component broadcast = MiniMessage.miniMessage().deserialize(message);
        sender.getServer().broadcast(broadcast);
        return true;
    }
}
