package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {
    
    private final MCBStaff plugin;
    private final Set<UUID> frozenPlayers = new HashSet<>();
    
    public FreezeManager(MCBStaff plugin) {
        this.plugin = plugin;
    }
    
    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }
    
    public void freezePlayer(Player player) {
        if (isFrozen(player)) {
            return;
        }
        
        frozenPlayers.add(player.getUniqueId());
        player.sendMessage(plugin.getConfigManager().getMessage("freeze-notify-player"));
        
        // Notify staff
        notifyStaff(plugin.getConfigManager().getMessage("freeze-enabled", "player", player.getName()));
    }
    
    public void unfreezePlayer(Player player) {
        if (!isFrozen(player)) {
            return;
        }
        
        frozenPlayers.remove(player.getUniqueId());
        player.sendMessage(plugin.getConfigManager().getMessage("freeze-unfreeze-notify-player"));
        
        // Notify staff
        notifyStaff(plugin.getConfigManager().getMessage("freeze-disabled", "player", player.getName()));
    }
    
    public void toggleFreeze(Player player) {
        if (isFrozen(player)) {
            unfreezePlayer(player);
        } else {
            freezePlayer(player);
        }
    }
    
    public void unfreezeAll() {
        for (UUID uuid : new HashSet<>(frozenPlayers)) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                unfreezePlayer(player);
            }
        }
    }
    
    private void notifyStaff(String message) {
        plugin.getServer().getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("mcbstaff.freeze"))
            .forEach(p -> p.sendMessage(message));
    }
} 