package de.tubyoub.statusplugin.Listener;

import de.tubyoub.statusplugin.Managers.StatusManager;
import de.tubyoub.statusplugin.StatusPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Class implementing the Listener interface to handle player join events.
 * When a player joins, their display name is updated based on their status.
 */
public class PlayerJoinListener implements Listener {
    private final StatusManager statusManager;
    private final StatusPlugin plugin;
    /**
     * Constructor for the PlayerJoinListener class.
     * @param statusManager The StatusManager instance used to manage player statuses.
     */
    public PlayerJoinListener(StatusPlugin plugin, StatusManager statusManager) {
        this.statusManager = statusManager;
        this.plugin = plugin;
    }

    /**
     * Event handler for player join events.
     * When a player joins, their display name is updated based on their status.
     * @param event The PlayerJoinEvent to be handled.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfigManager().isTablistFormatter()) {
            Player player = event.getPlayer();
            statusManager.updateDisplayName(player);
        }
    }
}