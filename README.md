# MCBStaff

[![Build and Release](https://github.com/PluginsByAlex/MCBStaff/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/PluginsByAlex/MCBStaff/actions/workflows/build-and-release.yml)

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
- **Title Display**: Shows configurable title and subtitle to frozen players
- **Blindness Effect**: Optional blindness effect with adjustable intensity
- **Visual Feedback**: Clear indication when players are frozen with customizable messages

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

**NEW: MiniMessage Support!** 🎨
The plugin now supports both legacy color codes (`&a`, `&c`, etc.) and modern MiniMessage formatting:

**MiniMessage Features:**
- **Gradients**: `<gradient:red:blue>Beautiful gradient text</gradient>`
- **Click Events**: `<click:run_command:/help>Click me for help!</click>`
- **Hover Text**: `<hover:show_text:'This is a tooltip'>Hover over me</hover>`
- **Rich Formatting**: `<bold><italic><red>Bold italic red text</red></italic></bold>`
- **Color Names**: `<red>Red text</red>`, `<gold>Gold text</gold>`, etc.

**Examples:**
```yaml
# Legacy format (still supported)
staff-mode-enabled: "&a[MCBStaff] &7Staff mode &aenabled&7!"

# MiniMessage format with beautiful gradient prefix
staff-mode-enabled: "<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <gray>Staff mode <green>enabled</green>!</gray>"

# More gradient examples
freeze-enabled: "<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <gray>Player <white>{player}</white> has been <red>frozen</red>!</gray>"

# Interactive messages with the gradient prefix
teleported-to-player: "<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b> <gray>Teleported to <click:suggest_command:/tp {player}><white>{player}</white></click>!</gray>"
```

The plugin automatically detects which format you're using based on the presence of `<` and `>` characters.

**✨ Beautiful Gradient Prefix:** All messages now feature a stunning purple-to-pink gradient prefix: `<b><gradient:#832466:#BF4299:#832466>MCBSTAFF</gradient></b>`

### Staff Items Configuration
**NEW**: You can now fully customize all staff items including their material, name, lore, and hotbar slot position:

```yaml
staffItems:
  teleportGUI:
    material: "COMPASS"
    slot: 0
    name: "<b><gradient:#832466:#BF4299:#832466>Teleport GUI</gradient></b>"
    lore:
      - "<gray>Right-click to open teleport menu"
  
  invisibility:
    material: "GRAY_DYE"
    slot: 1
    name: "<b><dark_gray>Invisibility</dark_gray></b>"
    lore:
      - "<gray>Right-click to become invisible"
  
  visibility:
    material: "GREEN_DYE"
    slot: 2
    name: "<b><green>Visibility</green></b>"
    lore:
      - "<gray>Right-click to become visible"
  
  randomTeleport:
    material: "ENDER_PEARL"
    slot: 3
    name: "<b><gradient:#832466:#BF4299:#832466>Random Teleport</gradient></b>"
    lore:
      - "<gray>Right-click to teleport to a random player"
  
  freezeRod:
    material: "BLAZE_ROD"
    slot: 4
    name: "<b><red>Freeze Rod</red></b>"
    lore:
      - "<gray>Right-click a player to freeze/unfreeze them"
  
  oreTracker:
    material: "BOOK"
    slot: 8
    name: "<b><gradient:#832466:#BF4299:#832466>Ore Tracker</gradient></b>"
    lore:
      - "<gray>Right-click to view ore statistics"
```

**Customization Options:**
- **material**: Any valid Bukkit Material name
- **slot**: Hotbar slot (0-8) where the item will be placed
- **name**: Display name with color codes (&-style)
- **lore**: List of lore lines with color codes

### Random Teleport
The Random Teleport Pearl teleports staff to a random online player. You can configure which worlds to exclude players from being teleport targets:

```yaml
randomTeleport:
  ignoreWorlds:
    - "world_nether"
    - "world_the_end"
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
  
  # NEW: Title and subtitle configuration
  title:
    enabled: true
    text: "<b><red>FROZEN</red></b>"
    subtitle: "<gray>Join the discord</gray>"
    fadeIn: 10    # Ticks for fade in
    stay: 70      # Ticks to stay visible
    fadeOut: 20   # Ticks for fade out
  
  # NEW: Blindness effect configuration
  blindness:
    enabled: true
    level: 1      # Effect level (0-255, recommended: 0-2)
```

**New Freeze Features:**
- **Title Display**: Shows a configurable title and subtitle to frozen players
- **Blindness Effect**: Optional blindness effect with configurable intensity
- **MiniMessage Support**: Both title and subtitle support full MiniMessage formatting
- **Timing Control**: Customizable fade in/out and display duration for titles

## 🎯 Compatibility

- **Minecraft**: 1.21.4+
- **Server Software**: Paper, Spigot
- **Java**: 21+