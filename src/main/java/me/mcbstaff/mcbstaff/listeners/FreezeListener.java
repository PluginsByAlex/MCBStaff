package me.mcbstaff.mcbstaff.listeners;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListener implements Listener {
    
    private final MCBStaff plugin;
    
    public FreezeListener(MCBStaff plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getFreezeManager().isFrozen(player)) return;
        
        // Only cancel if the player actually moved, not just looked around
        if (event.getFrom().getX() != event.getTo().getX() || 
            event.getFrom().getY() != event.getTo().getY() || 
            event.getFrom().getZ() != event.getTo().getZ()) {
            
            event.setCancelled(true);
            Component message = plugin.getConfigManager().getMessageComponent("freeze-deny-move");
            player.sendMessage(message);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getFreezeManager().isFrozen(player)) return;
        
        event.setCancelled(true);
        Component message = plugin.getConfigManager().getMessageComponent("freeze-deny-chat");
        player.sendMessage(message);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getFreezeManager().isFrozen(player)) return;
        
        String command = event.getMessage().toLowerCase().substring(1);
        String[] args = command.split(" ");
        String baseCommand = args[0];
        
        // Check if the command is blocked
        if (plugin.getConfigManager().isCommandBlocked(baseCommand)) {
            event.setCancelled(true);
            Component message = plugin.getConfigManager().getMessageComponent("freeze-deny-command");
            player.sendMessage(message);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Automatically unfreeze players when they leave
        if (plugin.getFreezeManager().isFrozen(player)) {
            plugin.getFreezeManager().unfreezePlayer(player);
        }
        
        // Disable staff mode when players leave
        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            plugin.getStaffModeManager().disableStaffMode(player);
        }
    }
} 