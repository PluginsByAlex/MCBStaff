package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreTrackerManager {
    
    private final MCBStaff plugin;
    private final Map<Material, Material> oreToBlock = new HashMap<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
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
        
        int maxPlayers = plugin.getConfigManager().getMaxPlayersShown();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (slot >= maxPlayers) break; // Leave space for navigation
            
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(onlinePlayer);
            
            // Use Adventure API with proper MiniMessage formatting
            Component displayName = miniMessage.deserialize("<white>" + onlinePlayer.getName() + "</white>");
            meta.displayName(displayName);
            
            List<Component> lore = new ArrayList<>();
            lore.add(miniMessage.deserialize("<gray>Ore Statistics:</gray>"));
            lore.add(Component.empty()); // Empty line
            
            // Get ore statistics for this player
            for (String oreName : trackedOreNames) {
                try {
                    Material oreMaterial = Material.valueOf(oreName);
                    if (oreToBlock.containsKey(oreMaterial)) {
                        int count = onlinePlayer.getStatistic(Statistic.MINE_BLOCK, oreMaterial);
                        String displayOre = getDisplayName(oreMaterial);
                        lore.add(miniMessage.deserialize("<gold>" + displayOre + ":</gold> <white>" + count + "</white>"));
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    // Skip invalid materials or statistics
                }
            }
            
            meta.lore(lore);
            playerHead.setItemMeta(meta);
            gui.setItem(slot++, playerHead);
        }
        
        // Add refresh button with Adventure API
        ItemStack refreshButton = new ItemStack(Material.EMERALD);
        ItemMeta refreshMeta = refreshButton.getItemMeta();
        
        Component refreshName = miniMessage.deserialize("<green><bold>Refresh</bold></green>");
        refreshMeta.displayName(refreshName);
        
        List<Component> refreshLore = new ArrayList<>();
        refreshLore.add(miniMessage.deserialize("<gray>Click to refresh the ore tracker</gray>"));
        refreshMeta.lore(refreshLore);
        
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