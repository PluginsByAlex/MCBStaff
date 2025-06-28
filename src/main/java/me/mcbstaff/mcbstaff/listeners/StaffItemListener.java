package me.mcbstaff.mcbstaff.listeners;

import me.mcbstaff.mcbstaff.MCBStaff;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        String displayName = meta.getDisplayName();
        
        switch (item.getType()) {
            case COMPASS:
                if ("§6§lTeleport GUI".equals(displayName)) {
                    openTeleportGUI(player);
                    event.setCancelled(true);
                }
                break;
                
            case GRAY_DYE:
                if ("§8§lInvisibility".equals(displayName)) {
                    toggleInvisibility(player, true);
                    event.setCancelled(true);
                }
                break;
                
            case GREEN_DYE:
                if ("§a§lVisibility".equals(displayName)) {
                    toggleInvisibility(player, false);
                    event.setCancelled(true);
                }
                break;
                
            case ENDER_PEARL:
                if ("§d§lRandom Teleport".equals(displayName)) {
                    randomTeleport(player);
                    event.setCancelled(true);
                }
                break;
                
            case BOOK:
                if ("§6§lOre Tracker".equals(displayName)) {
                    plugin.getOreTrackerManager().openOreTrackerGUI(player);
                    event.setCancelled(true);
                }
                break;
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!item.hasItemMeta()) return;
        if (!plugin.getStaffModeManager().isInStaffMode(player)) return;
        
        ItemMeta meta = item.getItemMeta();
        String displayName = meta.getDisplayName();
        
        if (item.getType() == Material.BLAZE_ROD && "§c§lFreeze Rod".equals(displayName)) {
            if (event.getRightClicked() instanceof Player) {
                Player target = (Player) event.getRightClicked();
                
                if (!player.hasPermission("mcbstaff.freeze")) {
                    player.sendMessage("§cYou don't have permission to freeze players!");
                    return;
                }
                
                plugin.getFreezeManager().toggleFreeze(target);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // Handle teleport GUI clicks
        if (event.getView().getTitle().equals("§6§lTeleport Menu")) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;
            
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;
            
            String targetName = meta.getDisplayName().replace("§f", "");
            Player target = Bukkit.getPlayer(targetName);
            
            if (target != null && target.isOnline()) {
                player.teleport(target.getLocation());
                player.sendMessage(plugin.getConfigManager().getMessage("teleported-to-player", 
                    "player", target.getName()));
                player.closeInventory();
            }
        }
        
        // Handle ore tracker GUI clicks
        else if (plugin.getOreTrackerManager().isOreTrackerGUI(event.getInventory())) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null) return;
            
            // Handle refresh button
            if (clicked.getType() == Material.EMERALD && 
                clicked.getItemMeta().getDisplayName().equals("§a§lRefresh")) {
                plugin.getOreTrackerManager().openOreTrackerGUI(player);
                player.sendMessage(plugin.getConfigManager().getMessage("ore-tracker-updated"));
            }
        }
    }
    
    private void openTeleportGUI(Player player) {
        org.bukkit.inventory.Inventory gui = Bukkit.createInventory(null, 54, "§6§lTeleport Menu");
        
        int slot = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) continue;
            if (slot >= 54) break;
            
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = playerHead.getItemMeta();
            meta.setDisplayName("§f" + onlinePlayer.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to teleport to " + onlinePlayer.getName());
            lore.add("§7World: §f" + onlinePlayer.getWorld().getName());
            lore.add("§7Location: §f" + (int) onlinePlayer.getLocation().getX() + ", " + 
                    (int) onlinePlayer.getLocation().getY() + ", " + 
                    (int) onlinePlayer.getLocation().getZ());
            meta.setLore(lore);
            
            playerHead.setItemMeta(meta);
            gui.setItem(slot++, playerHead);
        }
        
        player.openInventory(gui);
    }
    
    private void toggleInvisibility(Player player, boolean invisible) {
        if (invisible) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            
            // Hide player from other players
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.hasPermission("mcbstaff.staff")) {
                    other.hidePlayer(plugin, player);
                }
            }
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            
            // Show player to other players
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(plugin, player);
            }
        }
    }
    
    private void randomTeleport(Player player) {
        if (!player.hasPermission("mcbstaff.randomtp")) {
            player.sendMessage("§cYou don't have permission to use random teleport!");
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
            player.sendMessage(plugin.getConfigManager().getMessage("no-players-found"));
            return;
        }
        
        Player target = availablePlayers.get(random.nextInt(availablePlayers.size()));
        player.teleport(target.getLocation());
        player.sendMessage(plugin.getConfigManager().getMessage("teleported-to-player", 
            "player", target.getName()));
    }
} 