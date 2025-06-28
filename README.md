# MCBStaff

[![Build and Release](https://github.com/yourusername/mcbstaff/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/yourusername/mcbstaff/actions/workflows/build-and-release.yml)

A lightweight staff utility plugin for Minecraft servers that provides a dedicated staff mode with moderation tools and inventory management.

## 🌟 Features

### Staff Mode
- **Inventory Management**: Automatically saves and restores player inventory when toggling staff mode
- **Creative Mode**: Enables creative mode and flight for staff members
- **Staff Tools**: Provides essential moderation tools in a clean interface

### Staff Tools
- 🧭 **Compass (Teleport GUI)**: Opens a GUI to teleport to any online player
- 🔸 **Gray Dye (Invisibility)**: Makes staff members invisible to regular players
- 🟢 **Green Dye (Visibility)**: Makes staff members visible again
- 🔮 **Random Teleport Pearl**: Teleports to a random online player
- 🔥 **Freeze Rod**: Right-click players to freeze/unfreeze them
- 📖 **Ore Tracker**: View comprehensive ore mining statistics for all players

### Player Freeze System
- **Movement Blocking**: Prevents frozen players from moving
- **Chat Restriction**: Blocks chat messages from frozen players
- **Command Blocking**: Configurable list of blocked commands for frozen players
- **Staff Notifications**: Notifies staff when players are frozen/unfrozen

### Ore Tracker
- **Real-time Statistics**: Shows mining statistics for all tracked ores
- **Comprehensive Tracking**: Supports all ore types including deepslate variants
- **GUI Interface**: Easy-to-use graphical interface
- **Auto-refresh**: Configurable update intervals

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/staff [player]` | Toggle staff mode for yourself or another player | `mcbstaff.staff` |
| `/freeze <player>` | Freeze or unfreeze a player | `mcbstaff.freeze` |
| `/ores` | Open the ore tracker GUI | `mcbstaff.ores` |

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `mcbstaff.*` | Access to all MCBStaff features | `op` |
| `mcbstaff.staff` | Access to staff mode and staff items | `op` |
| `mcbstaff.freeze` | Use freeze command and Freeze Rod | `op` |
| `mcbstaff.ores` | View ore tracker GUI | `op` |
| `mcbstaff.randomtp` | Use the Random Teleport Pearl | `op` |

## ⚙️ Configuration

The plugin creates a `config.yml` file with the following sections:

### Messages
Customize all plugin messages including colors and placeholders.

### Random Teleport
```yaml
randomTeleport:
  ignoreWorlds:
    - "world_nether"
    - "world_the_end"
  radius: 1000
```

### Ore Tracker
```yaml
oreTracker:
  trackedOres:
    - "DIAMOND_ORE"
    - "EMERALD_ORE"
    # ... more ores
  updateIntervalTicks: 60
```

### Freeze System
```yaml
freeze:
  denyCommands:
    - "spawn"
    - "home"
    - "tp"
    # ... more commands
```

## 🎯 Compatibility

- **Minecraft**: 1.21.4+
- **Server Software**: Paper, Spigot
- **Java**: 21+