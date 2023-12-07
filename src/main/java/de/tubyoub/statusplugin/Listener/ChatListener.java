package de.tubyoub.statusplugin.Listener;

import de.tubyoub.statusplugin.StatusManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener {
    private final StatusManager statusManager;
    public ChatListener(StatusManager statusManager) {
        this.statusManager = statusManager;
    }
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        // Get the player and message
        Player player = event.getPlayer();
        String message = event.getMessage();
        String status = statusManager.getStatus(player);
        String broadcastMessage;

        if (status == null) {
            broadcastMessage = player.getName() + ": " + message;
        } else {
            broadcastMessage = "[" + status + "] " + player.getName() + ": " + message;
        }
        Bukkit.broadcastMessage(broadcastMessage);
        // Cancel the original event
        event.setCancelled(true);
    }
}