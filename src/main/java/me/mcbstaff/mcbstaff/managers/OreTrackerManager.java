package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreTrackerManager {
    
    private final MCBStaff plugin;
    private final Map<Material, Material> oreToBlock = new HashMap<>();
    
    public OreTrackerManager(MCBStaff plugin) {
        this.plugin = plugin;
        initializeOreMapping();
    }
    
    private void initializeOreMapping() {
        // Map ores to their corresponding block materials for statistics
        oreToBlock.put(Material.DIAMOND_ORE, Material.DIAMOND_ORE);
        oreToBlock.put(Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE);
        oreToBlock.put(Material.EMERALD_ORE, Material.EMERALD_ORE);
        oreToBlock.put(Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE);
        oreToBlock.put(Material.GOLD_ORE, Material.GOLD_ORE);
        oreToBlock.put(Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_GOLD_ORE);
        oreToBlock.put(Material.IRON_ORE, Material.IRON_ORE);
        oreToBlock.put(Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_IRON_ORE);
        oreToBlock.put(Material.COPPER_ORE, Material.COPPER_ORE);
        oreToBlock.put(Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_COPPER_ORE);
        oreToBlock.put(Material.COAL_ORE, Material.COAL_ORE);
        oreToBlock.put(Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_COAL_ORE);
        oreToBlock.put(Material.LAPIS_ORE, Material.LAPIS_ORE);
        oreToBlock.put(Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE);
        oreToBlock.put(Material.REDSTONE_ORE, Material.REDSTONE_ORE);
        oreToBlock.put(Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE);
        oreToBlock.put(Material.NETHER_QUARTZ_ORE, Material.NETHER_QUARTZ_ORE);
        oreToBlock.put(Material.NETHER_GOLD_ORE, Material.NETHER_GOLD_ORE);
        oreToBlock.put(Material.ANCIENT_DEBRIS, Material.ANCIENT_DEBRIS);
    }
    
    public void openOreTrackerGUI(Player player) {
        String title = plugin.getConfigManager().getMessage("ore-tracker-title");
        Inventory gui = Bukkit.createInventory(null, 54, title);
        
        List<String> trackedOreNames = plugin.getConfigManager().getTrackedOres();
        int slot = 0;
        
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (slot >= 45) break; // Leave space for navigation
            
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = playerHead.getItemMeta();
            meta.setDisplayName("§f" + onlinePlayer.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Ore Statistics:");
            lore.add("");
            
            // Get ore statistics for this player
            for (String oreName : trackedOreNames) {
                try {
                    Material oreMaterial = Material.valueOf(oreName);
                    if (oreToBlock.containsKey(oreMaterial)) {
                        int count = onlinePlayer.getStatistic(Statistic.MINE_BLOCK, oreMaterial);
                        String displayName = getDisplayName(oreMaterial);
                        lore.add("§6" + displayName + ": §f" + count);
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    // Skip invalid materials or statistics
                }
            }
            
            meta.setLore(lore);
            playerHead.setItemMeta(meta);
            gui.setItem(slot++, playerHead);
        }
        
        // Add refresh button
        ItemStack refreshButton = new ItemStack(Material.EMERALD);
        ItemMeta refreshMeta = refreshButton.getItemMeta();
        refreshMeta.setDisplayName("§a§lRefresh");
        refreshMeta.setLore(List.of("§7Click to refresh the ore tracker"));
        refreshButton.setItemMeta(refreshMeta);
        gui.setItem(53, refreshButton);
        
        player.openInventory(gui);
    }
    
    private String getDisplayName(Material material) {
        String name = material.name().toLowerCase().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1));
        }
        
        return result.toString();
    }
    
    public boolean isOreTrackerGUI(Inventory inventory) {
        String title = plugin.getConfigManager().getMessage("ore-tracker-title");
        return inventory.getViewers().size() > 0 && 
               title.equals(inventory.getViewers().get(0).getOpenInventory().getTitle());
    }
} 