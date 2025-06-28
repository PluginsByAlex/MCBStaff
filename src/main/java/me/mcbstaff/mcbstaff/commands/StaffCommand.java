package me.mcbstaff.mcbstaff.commands;

import me.mcbstaff.mcbstaff.MCBStaff;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommand implements CommandExecutor {
    
    private final MCBStaff plugin;
    
    public StaffCommand(MCBStaff plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("mcbstaff.staff")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        // If no arguments, toggle staff mode for self
        if (args.length == 0) {
            plugin.getStaffModeManager().toggleStaffMode(player);
            return true;
        }
        
        // If one argument, toggle staff mode for target player
        if (args.length == 1) {
            if (!player.hasPermission("mcbstaff.staff.others")) {
                player.sendMessage("§cYou don't have permission to toggle staff mode for other players!");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
                return true;
            }
            
            plugin.getStaffModeManager().toggleStaffMode(target);
            player.sendMessage(plugin.getConfigManager().getMessage("staff-mode-toggle-others", 
                "player", target.getName()));
            return true;
        }
        
        // Invalid usage
        player.sendMessage("§cUsage: /staff [player]");
        return true;
    }
} 