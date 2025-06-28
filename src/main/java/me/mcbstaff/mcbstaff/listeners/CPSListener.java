package me.mcbstaff.mcbstaff.listeners;

import me.mcbstaff.mcbstaff.MCBStaff;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CPSListener implements Listener {
    
    private final MCBStaff plugin;
    
    public CPSListener(MCBStaff plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Only record left clicks and right clicks
        if (event.getAction() == Action.LEFT_CLICK_AIR || 
            event.getAction() == Action.LEFT_CLICK_BLOCK ||
            event.getAction() == Action.RIGHT_CLICK_AIR || 
            event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            
            // Check if this player is being tested
            if (plugin.getCPSManager().isBeingTested(player)) {
                plugin.getCPSManager().recordClick(player);
            }
        }
    }
} 