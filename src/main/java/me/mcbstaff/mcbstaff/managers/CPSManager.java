package me.mcbstaff.mcbstaff.managers;

import me.mcbstaff.mcbstaff.MCBStaff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CPSManager {
    private final MCBStaff plugin;
    private final MiniMessage miniMessage;
    private final ConfigManager configManager;
    private final FreezeManager freezeManager;
    
    // Track active CPS tests
    private final Map<UUID, CPSTest> activeTests = new ConcurrentHashMap<>();
    
    // Track cooldowns to prevent spam testing
    private final Map<String, Long> testCooldowns = new ConcurrentHashMap<>(); // "tester-target" -> timestamp
    
    public CPSManager(MCBStaff plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.configManager = plugin.getConfigManager();
        this.freezeManager = plugin.getFreezeManager();
    }
    
    /**
     * Starts a CPS test for the target player
     */
    public boolean startCPSTest(Player tester, Player target) {
        // Validation checks
        if (target.equals(tester)) {
            sendMessage(tester, configManager.getMessage("cps-cannot-test-self"));
            return false;
        }
        
        if (activeTests.containsKey(target.getUniqueId())) {
            sendMessage(tester, configManager.getMessage("cps-test-in-progress"));
            return false;
        }
        
        // Check cooldown
        String cooldownKey = tester.getUniqueId() + "-" + target.getUniqueId();
        if (testCooldowns.containsKey(cooldownKey)) {
            long timeLeft = (testCooldowns.get(cooldownKey) + (configManager.getCPSCooldown() * 1000L)) - System.currentTimeMillis();
            if (timeLeft > 0) {
                String timeLeftStr = String.valueOf((int) Math.ceil(timeLeft / 1000.0));
                String message = configManager.getMessage("cps-cooldown-active")
                    .replace("{time}", timeLeftStr);
                sendMessage(tester, message);
                return false;
            }
        }
        
        // Start the test
        int duration = configManager.getCPSTestDuration();
        CPSTest test = new CPSTest(tester, target, duration);
        activeTests.put(target.getUniqueId(), test);
        
        // Set cooldown
        testCooldowns.put(cooldownKey, System.currentTimeMillis());
        
        // Notify tester
        String startMessage = configManager.getMessage("cps-test-started")
            .replace("{player}", target.getName())
            .replace("{duration}", String.valueOf(duration));
        sendMessage(tester, startMessage);
        
        // Start the test timer
        test.start();
        
        return true;
    }
    
    /**
     * Records a click for a player if they're being tested
     */
    public void recordClick(Player player) {
        CPSTest test = activeTests.get(player.getUniqueId());
        if (test != null && test.isActive()) {
            test.addClick();
        }
    }
    
    /**
     * Checks if a player is currently being tested
     */
    public boolean isBeingTested(Player player) {
        CPSTest test = activeTests.get(player.getUniqueId());
        return test != null && test.isActive();
    }
    
    /**
     * Gets the active CPS test for a player
     */
    public CPSTest getActiveTest(Player player) {
        return activeTests.get(player.getUniqueId());
    }
    
    private void sendMessage(Player player, String message) {
        Component component = miniMessage.deserialize(message);
        player.sendMessage(component);
    }
    
    private void broadcastToStaff(String message) {
        if (!configManager.shouldBroadcastCPSResults()) return;
        
        Component component = miniMessage.deserialize(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("mcbstaff.staff")) {
                player.sendMessage(component);
            }
        }
    }
    
    /**
     * CPS Test class to handle individual tests
     */
    public class CPSTest {
        private final Player tester;
        private final Player target;
        private final int duration;
        private final long startTime;
        private int clicks = 0;
        private boolean active = true;
        private BukkitTask task;
        
        public CPSTest(Player tester, Player target, int duration) {
            this.tester = tester;
            this.target = target;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
        }
        
        public void start() {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    endTest();
                }
            }.runTaskLater(plugin, duration * 20L); // Convert seconds to ticks
        }
        
        public void addClick() {
            if (active) {
                clicks++;
            }
        }
        
        public void endTest() {
            if (!active) return;
            
            active = false;
            if (task != null) {
                task.cancel();
            }
            
            // Calculate actual duration (in case test was ended early)
            long actualDuration = (System.currentTimeMillis() - startTime) / 1000;
            if (actualDuration == 0) actualDuration = 1; // Prevent division by zero
            
            double cps = (double) clicks / actualDuration;
            
            // Format CPS to 1 decimal place
            String cpsFormatted = String.format("%.1f", cps);
            
            // Send results to tester
            String resultsMessage = configManager.getMessage("cps-test-results")
                .replace("{player}", target.getName())
                .replace("{clicks}", String.valueOf(clicks))
                .replace("{duration}", String.valueOf(actualDuration))
                .replace("{cps}", cpsFormatted);
            sendMessage(tester, resultsMessage);
            
            // Broadcast to staff if enabled
            if (configManager.shouldBroadcastCPSResults()) {
                String broadcastMessage = configManager.getMessage("cps-test-broadcast")
                    .replace("{staff}", tester.getName())
                    .replace("{player}", target.getName())
                    .replace("{cps}", cpsFormatted);
                broadcastToStaff(broadcastMessage);
            }
            
            // Log results if enabled
            if (configManager.shouldLogCPSResults()) {
                plugin.getLogger().info("CPS Test - Player: " + target.getName() + 
                    ", Tester: " + tester.getName() + 
                    ", Clicks: " + clicks + 
                    ", Duration: " + actualDuration + "s" +
                    ", CPS: " + cpsFormatted);
            }
            
            // Check thresholds and send warnings
            checkThresholds(cps, cpsFormatted);
            
            // Take automatic actions if enabled
            handleAutoActions(cps);
            
            // Remove from active tests
            activeTests.remove(target.getUniqueId());
        }
        
        private void checkThresholds(double cps, String cpsFormatted) {
            if (cps >= configManager.getCPSCriticalThreshold()) {
                String criticalMessage = configManager.getMessage("cps-critical")
                    .replace("{player}", target.getName())
                    .replace("{cps}", cpsFormatted);
                broadcastToStaff(criticalMessage);
            } else if (cps >= configManager.getCPSAlertThreshold()) {
                String warningMessage = configManager.getMessage("cps-warning")
                    .replace("{player}", target.getName())
                    .replace("{cps}", cpsFormatted);
                broadcastToStaff(warningMessage);
            }
        }
        
        private void handleAutoActions(double cps) {
            if (!configManager.isAutoActionsEnabled()) return;
            
            if (cps >= configManager.getCPSCriticalThreshold()) {
                String action = configManager.getCriticalAction();
                
                switch (action.toLowerCase()) {
                    case "freeze":
                        if (!freezeManager.isFrozen(target)) {
                            freezeManager.freezePlayer(target);
                            notifyAutoAction("Frozen");
                        }
                        break;
                    case "kick":
                        target.kick(miniMessage.deserialize("<red>Kicked for suspicious clicking behavior (CPS: " + String.format("%.1f", cps) + ")</red>"));
                        notifyAutoAction("Kicked");
                        break;
                    case "ban":
                        int banDuration = configManager.getBanDuration();
                        // Note: This would require a ban plugin integration in a real scenario
                        target.kick(miniMessage.deserialize("<red>Banned for " + banDuration + " minutes for suspicious clicking behavior</red>"));
                        notifyAutoAction("Banned (" + banDuration + " minutes)");
                        break;
                }
            }
        }
        
        private void notifyAutoAction(String action) {
            String actionMessage = configManager.getMessage("cps-auto-action-taken")
                .replace("{player}", target.getName())
                .replace("{action}", action);
            broadcastToStaff(actionMessage);
            
            plugin.getLogger().warning("Auto-action taken against " + target.getName() + ": " + action);
        }
        
        // Getters
        public Player getTester() { return tester; }
        public Player getTarget() { return target; }
        public int getClicks() { return clicks; }
        public boolean isActive() { return active; }
        public long getStartTime() { return startTime; }
        public int getDuration() { return duration; }
    }
} 