# InfEnchant

A modern Minecraft Paper plugin built for **Paper 1.21.11** running on **Java 21**.

InfEnchant removes the vanilla and EssentialsX enchantment caps, allowing operators to enchant any held item up to **level 225** (and byte ceiling **255**) using both an interactive **Switcher Dialog GUI** and direct Brigadier commands.

---

## Features

- **Operator-Only Access**: Strictly restricted to OP players or users with `infenchant.admin`.
- **Bypasses Vanilla & EssentialsX Limits**: Enchants items up to level 225/255 safely using Paper's unsafe enchantment pipeline.
- **Interactive Switcher Dialog GUI**:
  - **Category Switcher**: Cycle across All, Weapons & Combat, Armor & Defense, Tools & Mining, Bows & Crossbows, Trident & Mace, and Utility/Curses.
  - **Level Adjustment**: Preset buttons for Level 1, 50, 100, **225** (Vanilla Max), and **255** (Absolute Max), plus `+1`, `-1`, `+10`, `-10` adjusters.
  - **Real-Time Held Item Preview**: View the item in your hand and its active enchantment levels dynamically.
  - **Click to Apply / Remove**: Left-click to apply at the selected level; right-click to strip the enchantment.
  - **Quick Actions**: Apply all enchantments or clear all enchantments with one click.
- **Brigadier Command & Tab Completion**: Autocompletes all registered 1.21.11 enchantments, aliases, and level suggestions.
- **Enchanted Books & Any Item**: Works on weapons, armor, tools, blocks, items, and `ENCHANTED_BOOK` storage meta.
- **Folia & Paper Ready**: Built on Paper 1.21.11 lifecycle events and `paper-plugin.yml`.

---

## Commands & Usage

*All commands require operator status or the `infenchant.admin` permission.*

| Command | Description |
| :--- | :--- |
| `/infenchant` | Opens the interactive Switcher Dialog GUI (default target level: 225). |
| `/infenchant <level>` | Opens the Switcher Dialog GUI pre-set to the given level (e.g. `225` or `255`). |
| `/infenchant <enchantment>` | Enchants the held item with the specified enchantment at level 225. |
| `/infenchant <enchantment> <level>` | Enchants the held item with the specified enchantment at `level` (1–255). |
| `/infenchant all [level]` | Enchants the held item with **all** registered enchantments at `level` (default: 225). |
| `/infenchant clear` | Clears all enchantments from the held item. |
| `/infenchant gui [level]` | Explicit alias to open the Switcher Dialog GUI. |

**Command Aliases**: `/ie`, `/infiniteenchant`.

---

## Project Structure
```
infenchant/
├── build.gradle.kts           # Gradle configuration with Paper repo & dependencies
├── settings.gradle.kts        # Root project settings
├── src/
│   ├── main/
│   │   ├── java/com/infenchant/
│   │   │   ├── InfEnchant.java               # Main plugin entry point & registration
│   │   │   ├── command/
│   │   │   │   └── InfEnchantCommand.java   # Brigadier command implementation
│   │   │   ├── gui/
│   │   │   │   ├── EnchantmentCategory.java  # Category tabs & filtering
│   │   │   │   ├── EnchantmentGui.java       # 54-slot dialog GUI renderer
│   │   │   │   ├── EnchantmentGuiHolder.java # Custom inventory holder
│   │   │   │   └── EnchantmentGuiListener.java # Interactive click & drag events
│   │   │   └── util/
│   │   │       └── EnchantmentUtil.java      # Unsafe enchantment & storage meta handling
│   │   └── resources/
│   │       └── paper-plugin.yml              # Modern Paper plugin manifest & permissions
│   └── test/
│       └── java/com/infenchant/
│           └── EnchantmentUtilTest.java      # Unit tests
└── .gitignore
```

---

## Common Tasks

### 1. Build the Plugin JAR
To compile the code and build the plugin JAR into `build/libs/`:
```bash
gradle build
```

### 2. Run Local Paper 1.21.11 Test Server
To launch an automatic local Paper 1.21.11 test server with your plugin pre-loaded:
```bash
gradle runServer
```
*(On first run, this downloads Paper 1.21.11 and automatically accepts the Mojang EULA).*
