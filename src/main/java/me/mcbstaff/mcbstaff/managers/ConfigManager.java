package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    
    private final MCBStaff plugin;
    private FileConfiguration config;
    
    public ConfigManager(MCBStaff plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // Message methods
    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', 
            config.getString("messages." + key, "&cMessage not found: " + key));
    }
    
    public String getMessage(String key, String... placeholders) {
        String message = getMessage(key);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return message;
    }
    
    // Random teleport methods
    public List<String> getIgnoredWorlds() {
        return config.getStringList("randomTeleport.ignoreWorlds");
    }
    
    public int getTeleportRadius() {
        return config.getInt("randomTeleport.radius", 1000);
    }
    
    // Ore tracker methods
    public List<String> getTrackedOres() {
        return config.getStringList("oreTracker.trackedOres");
    }
    
    public int getOreTrackerUpdateInterval() {
        return config.getInt("oreTracker.updateIntervalTicks", 60);
    }
    
    // Freeze methods
    public List<String> getDenyCommands() {
        return config.getStringList("freeze.denyCommands");
    }
    
    public boolean isCommandBlocked(String command) {
        List<String> blockedCommands = getDenyCommands();
        return blockedCommands.stream()
            .anyMatch(blocked -> command.toLowerCase().startsWith(blocked.toLowerCase()));
    }
} 