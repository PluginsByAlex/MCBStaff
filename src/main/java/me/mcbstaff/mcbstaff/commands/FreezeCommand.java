package me.mcbstaff.mcbstaff.commands;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {
    
    private final MCBStaff plugin;
    
    public FreezeCommand(MCBStaff plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mcbstaff.freeze")) {
            Component message = Component.text().append(
                MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>You don't have permission to use this command!</red>")
            ).build();
            sender.sendMessage(message);
            return true;
        }
        
        if (args.length != 1) {
            Component message = Component.text().append(
                MiniMessage.miniMessage().deserialize("<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <red>Usage: /freeze <player></red>")
            ).build();
            sender.sendMessage(message);
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Component message = plugin.getConfigManager().getMessageComponent("player-not-found");
            sender.sendMessage(message);
            return true;
        }
        
        if (plugin.getFreezeManager().isFrozen(target)) {
            plugin.getFreezeManager().unfreezePlayer(target);
            Component message = plugin.getConfigManager().getMessageComponent("freeze-disabled", 
                "player", target.getName());
            sender.sendMessage(message);
        } else {
            plugin.getFreezeManager().freezePlayer(target);
            Component message = plugin.getConfigManager().getMessageComponent("freeze-enabled", 
                "player", target.getName());
            sender.sendMessage(message);
        }
        
        return true;
    }
} 