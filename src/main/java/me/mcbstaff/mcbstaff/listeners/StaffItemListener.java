package me.mcbstaff.mcbstaff.listeners;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StaffItemListener implements Listener {
    
    private final MCBStaff plugin;
    private final Random random = new Random();
    
    public StaffItemListener(MCBStaff plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta()) return;
        if (!plugin.getStaffModeManager().isInStaffMode(player)) return;
        
        ItemMeta meta = item.getItemMeta();
        Component displayName = meta.displayName();
        String displayNameString = displayName != null ? 
            plugin.getConfigManager().getLegacySerializer().serialize(displayName) : null;
        
        // Identify the staff item type from config
        String staffItemType = plugin.getConfigManager().getStaffItemType(item.getType(), displayNameString);
        
        if (staffItemType != null) {
            event.setCancelled(true);
            
            switch (staffItemType) {
                case "teleportGUI":
                    openTeleportGUI(player);
                    break;
                    
                case "invisibility":
                    // Toggle invisibility state
                    boolean currentlyInvisible = plugin.getStaffModeManager().isInvisible(player);
                    toggleInvisibility(player, !currentlyInvisible);
                    break;
                    
                case "randomTeleport":
                    randomTeleport(player);
                    break;
                    
                case "freezeRod":
                    // Cancel this event - freeze functionality is handled in PlayerInteractEntityEvent
                    break;
                    
                case "oreTracker":
                    plugin.getOreTrackerManager().openOreTrackerGUI(player);
                    break;
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!item.hasItemMeta()) return;
        if (!plugin.getStaffModeManager().isInStaffMode(player)) return;
        
        ItemMeta meta = item.getItemMeta();
        Component displayName = meta.displayName();
        String displayNameString = displayName != null ? 
            plugin.getConfigManager().getLegacySerializer().serialize(displayName) : null;
        
        // Check if this is the freeze rod from config
        String staffItemType = plugin.getConfigManager().getStaffItemType(item.getType(), displayNameString);
        
        if ("freezeRod".equals(staffItemType)) {
            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                
                if (!player.hasPermission("mcbstaff.freeze")) {
                    Component message = plugin.getConfigManager().getMessageComponent("no-permission-freeze");
                    player.sendMessage(message);
                    return;
                }
                
                plugin.getFreezeManager().toggleFreeze(target);
                event.setCancelled(true);
            }
        } else if ("cpsChecker".equals(staffItemType)) {
            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                
                if (!player.hasPermission("mcbstaff.cps")) {
                    Component message = plugin.getConfigManager().getMessageComponent("no-permission-cps");
                    player.sendMessage(message);
                    return;
                }
                
                plugin.getCPSManager().startCPSTest(player, target);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // Prevent inventory manipulation while in staff mode (except for staff GUIs)
        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            String teleportGUITitle = plugin.getConfigManager().getTeleportGUITitle();
            boolean isStaffGUI = event.getView().getTitle().equals(teleportGUITitle) || 
                               plugin.getOreTrackerManager().isOreTrackerGUI(event.getInventory());
            
            // If it's not a staff GUI, always cancel the event
            if (!isStaffGUI) {
                event.setCancelled(true);
                return;
            }
            
            // Even in staff GUIs, prevent certain actions
            // Cancel if clicking on player inventory (bottom inventory)
            if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
            
            // Cancel shift-click that would move items to player inventory
            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
        }
        
        String teleportGUITitle = plugin.getConfigManager().getTeleportGUITitle();
        
        // Handle teleport GUI clicks
        if (event.getView().getTitle().equals(teleportGUITitle)) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;
            
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;
            
            Component displayName = meta.displayName();
            if (displayName == null) return;
            
            String targetName = plugin.getConfigManager().getLegacySerializer().serialize(displayName)
                .replace("§f", "").replace("§r", "").replace("§", "");
            Player target = Bukkit.getPlayer(targetName);
            
            if (target != null && target.isOnline()) {
                player.teleport(target.getLocation());
                Component message = plugin.getConfigManager().getMessageComponent("teleported-to-player", 
                    "player", target.getName());
                player.sendMessage(message);
                player.closeInventory();
            }
        }
        
        // Handle ore tracker GUI clicks
        else if (plugin.getOreTrackerManager().isOreTrackerGUI(event.getInventory())) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null) return;
            
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;
            
            Component displayName = meta.displayName();
            if (displayName == null) return;
            
            String displayNameString = plugin.getConfigManager().getLegacySerializer().serialize(displayName);
            
            // Handle refresh button - check if it's an emerald with refresh in the name
            if (clicked.getType() == Material.EMERALD && displayNameString != null && 
                (displayNameString.contains("Refresh") || displayNameString.contains("refresh"))) {
                plugin.getOreTrackerManager().openOreTrackerGUI(player);
                Component message = plugin.getConfigManager().getMessageComponent("ore-tracker-updated");
                player.sendMessage(message);
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // Prevent inventory dragging while in staff mode (except for staff GUIs)
        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            String teleportGUITitle = plugin.getConfigManager().getTeleportGUITitle();
            boolean isStaffGUI = event.getView().getTitle().equals(teleportGUITitle) || 
                               plugin.getOreTrackerManager().isOreTrackerGUI(event.getInventory());
            
            if (!isStaffGUI) {
                event.setCancelled(true);
                return;
            }
            
            // Even in staff GUIs, prevent dragging items to player inventory
            for (Integer slot : event.getRawSlots()) {
                if (slot >= event.getView().getTopInventory().getSize()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        // Prevent dropping items while in staff mode
        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        
        // Prevent picking up items while in staff mode
        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            event.setCancelled(true);
        }
    }
    
    private void openTeleportGUI(Player player) {
        String guiTitle = plugin.getConfigManager().getTeleportGUITitle();
        org.bukkit.inventory.Inventory gui = Bukkit.createInventory(null, 54, guiTitle);
        
        int slot = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) continue;
            if (slot >= 54) break;
            
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(onlinePlayer);
            
            // Use Adventure API with proper MiniMessage formatting
            MiniMessage miniMessage = MiniMessage.miniMessage();
            Component displayName = miniMessage.deserialize("<white>" + onlinePlayer.getName() + "</white>");
            meta.displayName(displayName);
            
            // Use Adventure API for lore with proper formatting
            List<Component> lore = new ArrayList<>();
            lore.add(miniMessage.deserialize("<gray>Click to teleport to " + onlinePlayer.getName() + "</gray>"));
            lore.add(miniMessage.deserialize("<gray>World:</gray> <white>" + onlinePlayer.getWorld().getName() + "</white>"));
            lore.add(miniMessage.deserialize("<gray>Location:</gray> <white>" + (int) onlinePlayer.getLocation().getX() + ", " + 
                    (int) onlinePlayer.getLocation().getY() + ", " + 
                    (int) onlinePlayer.getLocation().getZ() + "</white>"));
            meta.lore(lore);
            
            playerHead.setItemMeta(meta);
            gui.setItem(slot++, playerHead);
        }
        
        player.openInventory(gui);
    }
    
    private void toggleInvisibility(Player player, boolean invisible) {
        // Update the state in StaffModeManager
        plugin.getStaffModeManager().setInvisible(player, invisible);
        
        if (invisible) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            
            // Hide player from other players
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.hasPermission("mcbstaff.staff")) {
                    other.hidePlayer(plugin, player);
                }
            }
            
            // Send feedback to the player using Adventure API
            Component message = plugin.getConfigManager().getMessageComponent("invisibility-enabled");
            player.sendMessage(message);
            
            // Broadcast to staff if enabled in config
            if (plugin.getConfigManager().isBroadcastInvisibilityToStaff()) {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff != player && staff.hasPermission("mcbstaff.staff")) {
                        Component staffMessage = plugin.getConfigManager().getMessageComponent("invisibility-broadcast-enabled", "player", player.getName());
                        staff.sendMessage(staffMessage);
                    }
                }
            }
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            
            // Show player to other players
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(plugin, player);
            }
            
            // Send feedback to the player using Adventure API
            Component message = plugin.getConfigManager().getMessageComponent("invisibility-disabled");
            player.sendMessage(message);
            
            // Broadcast to staff if enabled in config
            if (plugin.getConfigManager().isBroadcastInvisibilityToStaff()) {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff != player && staff.hasPermission("mcbstaff.staff")) {
                        Component staffMessage = plugin.getConfigManager().getMessageComponent("invisibility-broadcast-disabled", "player", player.getName());
                        staff.sendMessage(staffMessage);
                    }
                }
            }
        }
    }
    
    private void randomTeleport(Player player) {
        if (!player.hasPermission("mcbstaff.randomtp")) {
            Component message = plugin.getConfigManager().getMessageComponent("no-permission-randomtp");
            player.sendMessage(message);
            return;
        }
        
        List<Player> availablePlayers = new ArrayList<>();
        List<String> ignoredWorlds = plugin.getConfigManager().getIgnoredWorlds();
        
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) continue;
            if (ignoredWorlds.contains(onlinePlayer.getWorld().getName())) continue;
            availablePlayers.add(onlinePlayer);
        }
        
        if (availablePlayers.isEmpty()) {
            Component message = plugin.getConfigManager().getMessageComponent("no-players-found");
            player.sendMessage(message);
            return;
        }
        
        Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));
        player.teleport(target.getLocation());
        Component message = plugin.getConfigManager().getMessageComponent("teleported-to-player", 
            "player", target.getName());
        player.sendMessage(message);
    }
} 