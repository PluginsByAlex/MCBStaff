package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StaffModeManager {
    
    private final MCBStaff plugin;
    private final Map<UUID, PlayerData> staffPlayers = new HashMap<>();
    
    public StaffModeManager(MCBStaff plugin) {
        this.plugin = plugin;
    }
    
    public boolean isInStaffMode(Player player) {
        return staffPlayers.containsKey(player.getUniqueId());
    }
    
    public void enableStaffMode(Player player) {
        if (isInStaffMode(player)) {
            return;
        }
        
        // Save player data
        PlayerData data = new PlayerData(
            player.getInventory().getContents().clone(),
            player.getInventory().getArmorContents().clone(),
            player.getGameMode(),
            player.getAllowFlight(),
            player.isFlying()
        );
        staffPlayers.put(player.getUniqueId(), data);
        
        // Clear inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        
        // Set creative mode and flight
        player.setGameMode(GameMode.CREATIVE);
        player.setAllowFlight(true);
        
        // Give staff items
        giveStaffItems(player);
        
        // Send message using Adventure API
        Component message = plugin.getConfigManager().getMessageComponent("staff-mode-enabled");
        player.sendMessage(message);
    }
    
    public void disableStaffMode(Player player) {
        PlayerData data = staffPlayers.remove(player.getUniqueId());
        if (data == null) {
            return;
        }
        
        // Restore inventory
        player.getInventory().clear();
        player.getInventory().setContents(data.getInventory());
        player.getInventory().setArmorContents(data.getArmor());
        
        // Restore game mode and flight
        player.setGameMode(data.getGameMode());
        player.setAllowFlight(data.getAllowFlight());
        player.setFlying(data.isFlying() && data.getAllowFlight());
        
        // Send message using Adventure API
        Component message = plugin.getConfigManager().getMessageComponent("staff-mode-disabled");
        player.sendMessage(message);
    }
    
    public void toggleStaffMode(Player player) {
        if (isInStaffMode(player)) {
            disableStaffMode(player);
        } else {
            enableStaffMode(player);
        }
    }
    
    public void disableAllStaffMode() {
        for (UUID uuid : new HashSet<>(staffPlayers.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                disableStaffMode(player);
            }
        }
    }
    
    private void giveStaffItems(Player player) {
        // Teleport GUI
        createAndGiveStaffItem(player, "teleportGUI");
        
        // Invisibility
        createAndGiveStaffItem(player, "invisibility");
        
        // Visibility
        createAndGiveStaffItem(player, "visibility");
        
        // Random Teleport
        createAndGiveStaffItem(player, "randomTeleport");
        
        // Freeze Rod
        createAndGiveStaffItem(player, "freezeRod");
        
        // Ore Tracker
        createAndGiveStaffItem(player, "oreTracker");
    }
    
    private void createAndGiveStaffItem(Player player, String itemKey) {
        Material material = plugin.getConfigManager().getStaffItemMaterial(itemKey);
        int slot = plugin.getConfigManager().getStaffItemSlot(itemKey);
        Component nameComponent = plugin.getConfigManager().getStaffItemNameComponent(itemKey);
        List<Component> loreComponents = plugin.getConfigManager().getStaffItemLoreComponents(itemKey);
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        // Set display name using Adventure API
        meta.displayName(nameComponent);
        
        // Set lore using Adventure API
        meta.lore(loreComponents);
        
        item.setItemMeta(meta);
        player.getInventory().setItem(slot, item);
    }
    
    // PlayerData inner class
    private static class PlayerData {
        private final ItemStack[] inventory;
        private final ItemStack[] armor;
        private final GameMode gameMode;
        private final boolean allowFlight;
        private final boolean flying;
        
        public PlayerData(ItemStack[] inventory, ItemStack[] armor, GameMode gameMode, 
                         boolean allowFlight, boolean flying) {
            this.inventory = inventory;
            this.armor = armor;
            this.gameMode = gameMode;
            this.allowFlight = allowFlight;
            this.flying = flying;
        }
        
        public ItemStack[] getInventory() { return inventory; }
        public ItemStack[] getArmor() { return armor; }
        public GameMode getGameMode() { return gameMode; }
        public boolean getAllowFlight() { return allowFlight; }
        public boolean isFlying() { return flying; }
    }
} 