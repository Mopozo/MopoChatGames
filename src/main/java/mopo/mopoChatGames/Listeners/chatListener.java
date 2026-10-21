package mopo.mopoChatGames.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mopo.mopoChatGames.MopoChatGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class chatListener implements Listener {

    @EventHandler
    public void chatEvent(AsyncChatEvent event) {
        MopoChatGames plugin = MopoChatGames.getINSTANCE();
        String submittedAnswer = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
        boolean caseSensitive = plugin.getConfig().getBoolean(
                "RepeatWord.caseSensitive",
                plugin.getConfig().getBoolean("RepeatWord.casesensative", false)
        );
        String answer = plugin.completeGameIfAnswer(submittedAnswer, caseSensitive);
        if (answer == null) {
            return;
        }

        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getServer().broadcast(Component.text(
                    player.getName() + " got the correct answer: " + answer
            ));
            player.getInventory().addItem(new ItemStack(Material.ACACIA_BOAT));
        });
    }

}
