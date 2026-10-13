package mopo.mopoChatGames.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mopo.mopoChatGames.MopoChatGames;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class chatListener implements Listener {

    @EventHandler
    //Running on the wrong event not async chat event
    public void chatEvent(AsyncChatEvent event ) {
        if (!MopoChatGames.getINSTANCE().getActiveGame().equals("null")) {
            String answer = MopoChatGames.getINSTANCE().getAnswer();
            boolean contains = event.message().contains(MiniMessage.miniMessage().deserialize(answer));
            if (contains) {
                Player player = event.getPlayer();
                player.getServer().broadcast(MiniMessage.miniMessage().deserialize(player + "got the correct answer of" + answer));
                MopoChatGames.getINSTANCE().setActiveGame(null);
                MopoChatGames.getINSTANCE().setAnswer(null);
                player.getInventory().addItem(new ItemStack(Material.ACACIA_BOAT));
                //change reward out of testing
            }
        }
    }

}
