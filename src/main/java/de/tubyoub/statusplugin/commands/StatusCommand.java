package de.tubyoub.statusplugin.commands;

import de.tubyoub.statusplugin.StatusManager;
import de.tubyoub.statusplugin.StatusPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StatusCommand implements CommandExecutor {
    private final StatusManager statusManager;

    public StatusCommand(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    @Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    StatusPlugin plugin = (StatusPlugin) Bukkit.getPluginManager().getPlugin("StatusPlugin");

    if (!(sender instanceof Player)) {
        // Handle console commands here
        if (args.length > 0 && "reload".equals(args[0])) {
            statusManager.reloadStatuses();
            sender.sendMessage("Statuses have been reloaded.");
            return true;
        }
        sender.sendMessage("This command can only be run by a player.");
        return true;
    }

    Player player = (Player) sender;

    if (args.length == 0) {
        player.sendMessage("Try using /status help");
        return true;
    }

    switch (args[0].toLowerCase()) {
        case "reload":
            reloadPlugin(player);
            return true;
        case "help":
            helpCommand(player, plugin, args);
            return true;
        case "setmaxlength":
            setmaxlenghtCommand(player, args);
            return true;
        case "resetmaxlength":
            resetmaxlenghtCommand(player);
            return true;
        case "info":
            infoCommand(player, plugin);
            return true;
        case "remove":
            if (args.length == 1) {
                removeOwnStatus(player);
            } else if (args.length == 2) {
                removeOtherPlayerStatus(player, args[1]);
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /status remove [player]");
            }
            return true;
        default:
            handleDefaultCommand(player, args);
            return true;
    }
}

        private void handleDefaultCommand(Player player, String[] args) {
            if (args.length > 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if (!player.hasPermission("StatusPlugin.admin.setStatus")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to set another player's status.");
                        return;
                    }
                    String status = Arrays.stream(args, 1, args.length).collect(Collectors.joining(" "));
                    if (statusManager.setStatus(target, status, player)) {
                        player.sendMessage("Set " + target.getName() + "'s status to: " + ChatColor.translateAlternateColorCodes('&', status));
                    }
                    return;
                }
                player.sendMessage("Invalid player name: " + args[0]);
                return;
            }
            if (!player.hasPermission("StatusPlugin.setStatus")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to set your own status.");
                return;
            }
            String status = String.join(" ", args);
            if (statusManager.setStatus(player, status, player)) {
                player.sendMessage("Your status has been set to: " + "[" + status + "]");
            }

        }
        private void reloadPlugin(Player sender) {
            if (!sender.hasPermission("StatusPlugin.admin.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to reload statuses.");
                }
                statusManager.reloadStatuses();
                sender.sendMessage("Statuses have been reloaded.");
        }
        private void helpCommand(Player sender, StatusPlugin plugin, String[] args) {
            if (args.length > 1 && "colorcodes".equals(args[1].toLowerCase())) {
                plugin.sendPluginMessages(sender, "title");
                displayColorCodes(sender,plugin);
                plugin.sendPluginMessages(sender, "line");
            } else {
                plugin.sendPluginMessages(sender, "title");
                sender.sendMessage("Here you can see all available commands:");
                sender.sendMessage("/status <status> - Set your own status.");
                sender.sendMessage("/status remove - Remove your Status.");
                sender.sendMessage("/status help colorcodes - Get all colorcodes to use in your status.");
                if (sender.hasPermission("StatusPlugin.admin.setStatus")) {
                    sender.sendMessage("/status remove <player> - Remove a player's status. (Admin)");
                    sender.sendMessage("/status <player> <status> - Set a player's status. (Admin)");
                }
                sender.sendMessage("/status help colors - Show a list of color codes.");
                if (sender.hasPermission("StatusPlugin.admin.reload")) {
                    sender.sendMessage("/status reload - Reload all statuses. (Admin)");
                }
                if (sender.hasPermission("StatusPlugin.admin.setMaxlength")) {
                    sender.sendMessage("/status setmaxlength <length> - Set the max length of status. (Admin)");
                }
                if (sender.hasPermission("StatusPlugin.admin.resetMaxlength")) {
                    sender.sendMessage("/status resetmaxlength - Reset the max length of status to default. (Admin)");
                }
                sender.sendMessage("/status info - Show info about the plugin.");
                plugin.sendPluginMessages(sender, "line");
            }
        }
        private void displayColorCodes(CommandSender sender, StatusPlugin plugin) {
            sender.sendMessage(ChatColor.GOLD + "Available Color Codes:");
            for (ChatColor color : ChatColor.values()) {
                if (color.isColor()) {
                    sender.sendMessage(color + color.name() + ChatColor.RESET + " - &" + color.getChar());
                }
            }
        }
        private void setmaxlenghtCommand(Player sender, String[] args) {
            if (!sender.hasPermission("StatusPlugin.admin.setMaxlength")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to set the maximum status length.");
                }
                if (args.length == 2) {
                    try {
                        int maxLength = Integer.parseInt(args[1]);
                        statusManager.setMaxStatusLength(maxLength);
                        sender.sendMessage(ChatColor.GREEN + "Max status length set to " + maxLength);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid number format.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /status setmaxlength <length>");
                }
        }
        private void resetmaxlenghtCommand(Player sender) {
            if (!sender.hasPermission("StatusPlugin.admin.resetMaxlength")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reset the maximum status length.");
            }
            statusManager.resetMaxStatusLength();
            sender.sendMessage(ChatColor.GREEN + "Max status length reset to default.");
        }
        public void infoCommand(Player sender, StatusPlugin plugin){
            plugin.sendPluginMessages(sender, "title");
            sender.sendMessage(ChatColor.GREEN + "Author: TubYoub");
            sender.sendMessage(ChatColor.GREEN + "Version: 1.2");

            TextComponent githubLink = new TextComponent(ChatColor.DARK_GRAY + "" + ChatColor.UNDERLINE + "GitHub");
            githubLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/TubYoub/StatusPlugin"));
            githubLink.setUnderlined(true);
            sender.spigot().sendMessage(githubLink);

            sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Discord" + ChatColor.RESET + " is coming soon! (Maybe)");
            sender.sendMessage("If you have any issues please report them on GitHub.");

            plugin.sendPluginMessages(sender, "line");
        }
        private void removeOwnStatus(Player player) {
            if (!player.hasPermission("StatusPlugin.setStatus")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to remove your status.");
                return;
            }
            statusManager.removeStatus(player);
            player.sendMessage(ChatColor.GREEN + "Your status has been removed.");
}

        private void removeOtherPlayerStatus(Player sender, String targetName) {
            if (!sender.hasPermission("StatusPlugin.admin.setStatus")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to remove another player's status.");
                return;
            }
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + targetName);
                return;
            }
            statusManager.removeStatus(target);
            sender.sendMessage(ChatColor.GREEN + "Removed " + target.getName() + "'s status.");
        }

}