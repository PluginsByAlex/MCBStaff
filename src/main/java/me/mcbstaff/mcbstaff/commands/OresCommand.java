package me.mcbstaff.mcbstaff.commands;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OresCommand implements CommandExecutor, TabCompleter {
    
    private final MCBStaff plugin;
    
    public OresCommand(MCBStaff plugin) {
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
        
        if (!player.hasPermission("mcbstaff.ores")) {
            Component message = Component.text().append(
                MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>You don't have permission to use this command!</red>")
            ).build();
            player.sendMessage(message);
            return true;
        }
        
        plugin.getOreTrackerManager().openOreTrackerGUI(player);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // This command takes no arguments, so return empty list
        return new ArrayList<>();
    }
} 