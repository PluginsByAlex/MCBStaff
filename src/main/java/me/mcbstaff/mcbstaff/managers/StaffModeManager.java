package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
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
        
        // Send message
        player.sendMessage(plugin.getConfigManager().getMessage("staff-mode-enabled"));
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
        
        // Send message
        player.sendMessage(plugin.getConfigManager().getMessage("staff-mode-disabled"));
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
        // Compass (teleport GUI)
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName("§6§lTeleport GUI");
        compassMeta.setLore(Arrays.asList("§7Right-click to open teleport menu"));
        compass.setItemMeta(compassMeta);
        player.getInventory().setItem(0, compass);
        
        // Gray Dye (invisibility)
        ItemStack grayDye = new ItemStack(Material.GRAY_DYE);
        ItemMeta grayMeta = grayDye.getItemMeta();
        grayMeta.setDisplayName("§8§lInvisibility");
        grayMeta.setLore(Arrays.asList("§7Right-click to become invisible"));
        grayDye.setItemMeta(grayMeta);
        player.getInventory().setItem(1, grayDye);
        
        // Green Dye (visibility)
        ItemStack greenDye = new ItemStack(Material.GREEN_DYE);
        ItemMeta greenMeta = greenDye.getItemMeta();
        greenMeta.setDisplayName("§a§lVisibility");
        greenMeta.setLore(Arrays.asList("§7Right-click to become visible"));
        greenDye.setItemMeta(greenMeta);
        player.getInventory().setItem(2, greenDye);
        
        // Ender Pearl (random teleport)
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta pearlMeta = enderPearl.getItemMeta();
        pearlMeta.setDisplayName("§d§lRandom Teleport");
        pearlMeta.setLore(Arrays.asList("§7Right-click to teleport to a random player"));
        enderPearl.setItemMeta(pearlMeta);
        player.getInventory().setItem(3, enderPearl);
        
        // Blaze Rod (freeze rod)
        ItemStack freezeRod = new ItemStack(Material.BLAZE_ROD);
        ItemMeta rodMeta = freezeRod.getItemMeta();
        rodMeta.setDisplayName("§c§lFreeze Rod");
        rodMeta.setLore(Arrays.asList("§7Right-click a player to freeze/unfreeze them"));
        freezeRod.setItemMeta(rodMeta);
        player.getInventory().setItem(4, freezeRod);
        
        // Book (ore tracker)
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName("§6§lOre Tracker");
        bookMeta.setLore(Arrays.asList("§7Right-click to view ore statistics"));
        book.setItemMeta(bookMeta);
        player.getInventory().setItem(8, book);
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