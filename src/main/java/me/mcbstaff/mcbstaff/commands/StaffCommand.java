package me.mcbstaff.mcbstaff.commands;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
            Component message = Component.text().append(
                MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>This command can only be used by players!</red>")
            ).build();
            sender.sendMessage(message);
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("mcbstaff.staff")) {
            Component message = Component.text().append(
                MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>You don't have permission to use this command!</red>")
            ).build();
            player.sendMessage(message);
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
                Component message = Component.text().append(
                    MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>You don't have permission to toggle staff mode for other players!</red>")
                ).build();
                player.sendMessage(message);
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Component message = plugin.getConfigManager().getMessageComponent("player-not-found");
                player.sendMessage(message);
                return true;
            }
            
            plugin.getStaffModeManager().toggleStaffMode(target);
            Component message = plugin.getConfigManager().getMessageComponent("staff-mode-toggle-others", 
                "player", target.getName());
            player.sendMessage(message);
            return true;
        }
        
        // Invalid usage
        Component message = Component.text().append(
            MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>Usage: /staff [player]</red>")
        ).build();
        player.sendMessage(message);
        return true;
    }
} 