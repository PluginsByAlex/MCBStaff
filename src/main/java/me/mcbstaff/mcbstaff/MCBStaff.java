package me.mcbstaff.mcbstaff;

import me.mcbstaff.mcbstaff.commands.FreezeCommand;
import me.mcbstaff.mcbstaff.commands.OresCommand;
import me.mcbstaff.mcbstaff.commands.StaffCommand;
import me.mcbstaff.mcbstaff.listeners.CPSListener;
import me.mcbstaff.mcbstaff.listeners.FreezeListener;
import me.mcbstaff.mcbstaff.listeners.StaffItemListener;
import me.mcbstaff.mcbstaff.managers.ConfigManager;
import me.mcbstaff.mcbstaff.managers.CPSManager;
import me.mcbstaff.mcbstaff.managers.FreezeManager;
import me.mcbstaff.mcbstaff.managers.OreTrackerManager;
import me.mcbstaff.mcbstaff.managers.StaffModeManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCBStaff extends JavaPlugin {

    private static MCBStaff instance;
    
    private ConfigManager configManager;
    private StaffModeManager staffModeManager;
    private FreezeManager freezeManager;
    private OreTrackerManager oreTrackerManager;
    private CPSManager cpsManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        configManager = new ConfigManager(this);
        staffModeManager = new StaffModeManager(this);
        freezeManager = new FreezeManager(this);
        oreTrackerManager = new OreTrackerManager(this);
        cpsManager = new CPSManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("MCBStaff has been enabled!");
    }

    @Override
    public void onDisable() {
        // Restore all players from staff mode
        if (staffModeManager != null) {
            staffModeManager.disableAllStaffMode();
        }
        
        // Unfreeze all players
        if (freezeManager != null) {
            freezeManager.unfreezeAll();
        }
        
        getLogger().info("MCBStaff has been disabled!");
    }
    
    private void registerCommands() {
        // Register command executors and tab completers
        StaffCommand staffCommand = new StaffCommand(this);
        getCommand("staff").setExecutor(staffCommand);
        getCommand("staff").setTabCompleter(staffCommand);
        
        FreezeCommand freezeCommand = new FreezeCommand(this);
        getCommand("freeze").setExecutor(freezeCommand);
        getCommand("freeze").setTabCompleter(freezeCommand);
        
        OresCommand oresCommand = new OresCommand(this);
        getCommand("ores").setExecutor(oresCommand);
        getCommand("ores").setTabCompleter(oresCommand);
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new StaffItemListener(this), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
        getServer().getPluginManager().registerEvents(new CPSListener(this), this);
    }
    
    // Getters
    public static MCBStaff getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public StaffModeManager getStaffModeManager() {
        return staffModeManager;
    }
    
    public FreezeManager getFreezeManager() {
        return freezeManager;
    }
    
    public OreTrackerManager getOreTrackerManager() {
        return oreTrackerManager;
    }
    
    public CPSManager getCPSManager() {
        return cpsManager;
    }
} 