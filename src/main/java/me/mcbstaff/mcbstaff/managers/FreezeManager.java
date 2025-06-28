package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {
    
    private final MCBStaff plugin;
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Map<UUID, Long> lastToggleTime = new HashMap<>();
    private static final long TOGGLE_COOLDOWN = 100; // 100ms cooldown
    
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
        Component message = plugin.getConfigManager().getMessageComponent("freeze-notify-player");
        player.sendMessage(message);
        
        // Show title and subtitle if enabled
        if (plugin.getConfigManager().isFreezeTitle()) {
            Component title = plugin.getConfigManager().getFreezeTitle();
            Component subtitle = plugin.getConfigManager().getFreezeSubtitle();
            
            Title freezeTitle = Title.title(
                title,
                subtitle,
                Title.Times.times(
                    Duration.ofMillis(plugin.getConfigManager().getFreezeTitleFadeIn() * 50), // Convert ticks to milliseconds
                    Duration.ofMillis(plugin.getConfigManager().getFreezeTitleStay() * 50),
                    Duration.ofMillis(plugin.getConfigManager().getFreezeTitleFadeOut() * 50)
                )
            );
            
            player.showTitle(freezeTitle);
        }
        
        // Apply blindness effect if enabled
        if (plugin.getConfigManager().isFreezeBlindness()) {
            int blindnessLevel = plugin.getConfigManager().getFreezeBlindnessLevel();
            PotionEffect blindness = new PotionEffect(
                PotionEffectType.BLINDNESS, 
                Integer.MAX_VALUE, 
                blindnessLevel, 
                false, 
                false
            );
            player.addPotionEffect(blindness);
        }
        
        // Notify staff
        Component staffMessage = plugin.getConfigManager().getMessageComponent("freeze-enabled", "player", player.getName());
        notifyStaff(staffMessage);
    }
    
    public void unfreezePlayer(Player player) {
        if (!isFrozen(player)) {
            return;
        }
        
        frozenPlayers.remove(player.getUniqueId());
        Component message = plugin.getConfigManager().getMessageComponent("freeze-unfreeze-notify-player");
        player.sendMessage(message);
        
        // Clear title by showing empty title
        if (plugin.getConfigManager().isFreezeTitle()) {
            Title clearTitle = Title.title(
                Component.empty(),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Duration.ofMillis(1), Duration.ZERO)
            );
            player.showTitle(clearTitle);
        }
        
        // Remove blindness effect if it was enabled
        if (plugin.getConfigManager().isFreezeBlindness()) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        
        // Notify staff
        Component staffMessage = plugin.getConfigManager().getMessageComponent("freeze-disabled", "player", player.getName());
        notifyStaff(staffMessage);
    }
    
    public void toggleFreeze(Player player) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown to prevent rapid toggles
        if (lastToggleTime.containsKey(playerUUID)) {
            long timeSinceLastToggle = currentTime - lastToggleTime.get(playerUUID);
            if (timeSinceLastToggle < TOGGLE_COOLDOWN) {
                return; // Still in cooldown, ignore this toggle
            }
        }
        
        lastToggleTime.put(playerUUID, currentTime);
        
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
    
    private void notifyStaff(Component message) {
        plugin.getServer().getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("mcbstaff.freeze"))
            .forEach(p -> p.sendMessage(message));
    }
} 