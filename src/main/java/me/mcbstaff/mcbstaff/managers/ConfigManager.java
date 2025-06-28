package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {
    
    private final MCBStaff plugin;
    private FileConfiguration config;
    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;
    
    public ConfigManager(MCBStaff plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        this.miniMessage = MiniMessage.miniMessage();
        this.legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // Message methods with MiniMessage support
    public String getMessage(String key) {
        String rawMessage = config.getString("messages." + key, "&cMessage not found: " + key);
        rawMessage = replacePrefixPlaceholder(rawMessage);
        return processMessage(rawMessage);
    }
    
    public String getMessage(String key, String... placeholders) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        
        // Replace prefix placeholder first
        message = replacePrefixPlaceholder(message);
        
        // Replace other placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        
        return processMessage(message);
    }
    
    // Component versions for better Paper integration
    public Component getMessageComponent(String key) {
        String rawMessage = config.getString("messages." + key, "&cMessage not found: " + key);
        rawMessage = replacePrefixPlaceholder(rawMessage);
        return parseComponent(rawMessage);
    }
    
    public Component getMessageComponent(String key, String... placeholders) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        
        // Replace prefix placeholder first
        message = replacePrefixPlaceholder(message);
        
        // Replace other placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        
        return parseComponent(message);
    }
    
    /**
     * Process a message string, supporting both legacy color codes and MiniMessage
     */
    private String processMessage(String message) {
        if (message == null) return "";
        
        // Check if the message contains MiniMessage tags
        if (containsMiniMessageTags(message)) {
            // Parse as MiniMessage and convert back to legacy for compatibility
            Component component = miniMessage.deserialize(message);
            return legacySerializer.serialize(component);
        } else {
            // Process as legacy color codes
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }
    
    /**
     * Parse a message as a Component, supporting both legacy and MiniMessage
     */
    private Component parseComponent(String message) {
        if (message == null) return Component.empty();
        
        // Check if the message contains MiniMessage tags
        if (containsMiniMessageTags(message)) {
            return miniMessage.deserialize(message);
        } else {
            // Parse as legacy color codes
            return legacySerializer.deserialize(message);
        }
    }
    
    /**
     * Check if a string contains MiniMessage tags
     */
    private boolean containsMiniMessageTags(String message) {
        return message.contains("<") && message.contains(">");
    }
    
    /**
     * Replace the {prefix} placeholder with the actual prefix value
     */
    private String replacePrefixPlaceholder(String message) {
        if (message.contains("{prefix}")) {
            String prefix = config.getString("messages.prefix", "");
            return message.replace("{prefix}", prefix);
        }
        return message;
    }
    
    /**
     * Get the legacy serializer for backward compatibility
     */
    public LegacyComponentSerializer getLegacySerializer() {
        return legacySerializer;
    }
    
    // Staff item configuration methods with MiniMessage support
    public Material getStaffItemMaterial(String itemKey) {
        String materialName = config.getString("staffItems." + itemKey + ".material", "STONE");
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material for " + itemKey + ": " + materialName + ". Using STONE instead.");
            return Material.STONE;
        }
    }
    
    public int getStaffItemSlot(String itemKey) {
        return config.getInt("staffItems." + itemKey + ".slot", 0);
    }
    
    public String getStaffItemName(String itemKey) {
        String rawName = config.getString("staffItems." + itemKey + ".name", "&7" + itemKey);
        return processMessage(rawName);
    }
    
    public Component getStaffItemNameComponent(String itemKey) {
        String rawName = config.getString("staffItems." + itemKey + ".name", "&7" + itemKey);
        return parseComponent(rawName);
    }
    
    public List<String> getStaffItemLore(String itemKey) {
        List<String> lore = config.getStringList("staffItems." + itemKey + ".lore");
        return lore.stream()
                .map(this::processMessage)
                .collect(Collectors.toList());
    }
    
    public List<Component> getStaffItemLoreComponents(String itemKey) {
        List<String> lore = config.getStringList("staffItems." + itemKey + ".lore");
        return lore.stream()
                .map(this::parseComponent)
                .collect(Collectors.toList());
    }
    
    // Random teleport methods
    public List<String> getIgnoredWorlds() {
        return config.getStringList("randomTeleport.ignoreWorlds");
    }
    
    public int getMaxRadius() {
        return config.getInt("randomTeleport.maxRadius", 10000);
    }
    
    // Ore tracker methods
    public List<String> getTrackedOres() {
        return config.getStringList("oreTracker.trackedOres");
    }
    
    public int getOreTrackerUpdateInterval() {
        return config.getInt("oreTracker.updateIntervalTicks", 60);
    }
    
    public int getMaxPlayersShown() {
        return config.getInt("oreTracker.maxPlayersShown", 45);
    }
    
    // Invisibility methods
    public boolean isBroadcastInvisibilityToStaff() {
        return config.getBoolean("invisibility.broadcastToStaff", true);
    }
    
    public boolean canStaffSeeInvisibleStaff() {
        return config.getBoolean("invisibility.staffCanSeeInvisibleStaff", true);
    }
    
    public boolean isAutoInvisibleOnStaffMode() {
        return config.getBoolean("invisibility.autoInvisibleOnStaffMode", false);
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
    
    // Freeze title configuration
    public boolean isFreezeTitle() {
        return config.getBoolean("freeze.title.enabled", true);
    }
    
    public Component getFreezeTitle() {
        String titleText = config.getString("freeze.title.text", "<b><red>FROZEN</red></b>");
        return parseComponent(titleText);
    }
    
    public Component getFreezeSubtitle() {
        String subtitleText = config.getString("freeze.title.subtitle", "<gray>Join the discord</gray>");
        return parseComponent(subtitleText);
    }
    
    public int getFreezeTitleFadeIn() {
        return config.getInt("freeze.title.fadeIn", 10);
    }
    
    public int getFreezeTitleStay() {
        return config.getInt("freeze.title.stay", 70);
    }
    
    public int getFreezeTitleFadeOut() {
        return config.getInt("freeze.title.fadeOut", 20);
    }
    
    // Freeze blindness configuration
    public boolean isFreezeBlindness() {
        return config.getBoolean("freeze.blindness.enabled", true);
    }
    
    public int getFreezeBlindnessLevel() {
        return config.getInt("freeze.blindness.level", 1);
    }
    
    // Additional freeze settings
    public boolean isPreventDamage() {
        return config.getBoolean("freeze.preventDamage", true);
    }
    
    public boolean isPreventItemDrop() {
        return config.getBoolean("freeze.preventItemDrop", true);
    }
    
    public boolean isPreventItemPickup() {
        return config.getBoolean("freeze.preventItemPickup", true);
    }
    
    public boolean isLogActions() {
        return config.getBoolean("freeze.logActions", true);
    }
    
    // Advanced settings
    public boolean isDebugMode() {
        return config.getBoolean("advanced.debug", false);
    }
    
    public boolean isCheckForUpdates() {
        return config.getBoolean("advanced.checkForUpdates", true);
    }
    
    public int getDataSaveInterval() {
        return config.getInt("advanced.dataSaveInterval", 300);
    }
    
    public int getDataCleanupDays() {
        return config.getInt("advanced.dataCleanupDays", 30);
    }
    
    // Helper method to identify staff items
    public String getStaffItemType(Material material, String displayName) {
        String[] itemKeys = {"teleportGUI", "invisibility", "randomTeleport", "freezeRod", "cpsChecker", "oreTracker"};
        
        for (String itemKey : itemKeys) {
            Material configMaterial = getStaffItemMaterial(itemKey);
            String configName = getStaffItemName(itemKey);
            
            if (material == configMaterial && displayName != null && displayName.equals(configName)) {
                return itemKey;
            }
        }
        
        // Special handling for invisibility toggle - check for both gray and lime dye
        if ((material == Material.GRAY_DYE || material == Material.LIME_DYE) && displayName != null) {
            if (displayName.contains("Invisibility") || displayName.contains("Visibility")) {
                return "invisibility";
            }
        }
        
        return null; // Not a staff item
    }
    
    // Get teleport GUI title (derived from the teleport item name)
    public String getTeleportGUITitle() {
        // Convert the teleport GUI item name to a menu title
        String itemName = getStaffItemName("teleportGUI");
        return itemName.replace("GUI", "Menu");
    }
    
    // ──────────────────────────────────────────────────────────────
    // CPS Checker Configuration Methods
    // ──────────────────────────────────────────────────────────────
    
    public int getCPSTestDuration() {
        return config.getInt("cpsChecker.testDuration", 10);
    }
    
    public int getCPSCooldown() {
        return config.getInt("cpsChecker.cooldown", 30);
    }
    
    public boolean shouldBroadcastCPSResults() {
        return config.getBoolean("cpsChecker.broadcastToStaff", true);
    }
    
    public boolean shouldLogCPSResults() {
        return config.getBoolean("cpsChecker.logResults", true);
    }
    
    public double getCPSWarningThreshold() {
        return config.getDouble("cpsChecker.thresholds.warning", 12.0);
    }
    
    public double getCPSAlertThreshold() {
        return config.getDouble("cpsChecker.thresholds.alert", 16.0);
    }
    
    public double getCPSCriticalThreshold() {
        return config.getDouble("cpsChecker.thresholds.critical", 20.0);
    }
    
    public boolean isAutoActionsEnabled() {
        return config.getBoolean("cpsChecker.autoActions.enabled", false);
    }
    
    public String getCriticalAction() {
        return config.getString("cpsChecker.autoActions.criticalAction", "freeze");
    }
    
    public int getBanDuration() {
        return config.getInt("cpsChecker.autoActions.banDuration", 60);
    }
} 