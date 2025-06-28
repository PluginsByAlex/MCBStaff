package me.mcbstaff.mcbstaff.commands;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaffCommand implements CommandExecutor, TabCompleter {
    
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
        
        // Command only accepts no arguments - toggle for self only
        Component message = Component.text().append(
            MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>Usage: /staff</red>")
        ).build();
        player.sendMessage(message);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Staff command takes no arguments, so return empty list
        return new ArrayList<>();
    }
} 