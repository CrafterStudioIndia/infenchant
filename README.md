# ⚡ InfEnchant

Tired of vanilla's strict enchantment caps? Frustrated when EssentialsX blocks you from putting **Sharpness 225** on a stick? 

**InfEnchant** is a lightweight, modern Paper plugin designed for server admins and operators who just want full control without the headache. It removes the arbitrary vanilla and Essentials limits, letting you safely enchant **any item** up to **level 225** (and the absolute byte ceiling of **255**) using either a clean visual GUI or fast tab-completing commands.

---

### 📋 Commands & Permissions

> **Note:** InfEnchant is operator-only by default (`default: op`). Regular players cannot use it unless explicitly granted permission.

| Command | Description |
| :--- | :--- |
| `/infenchant` | Opens the Switcher GUI with level 225 preset |
| `/infenchant <level>` | Opens the GUI preset to that specific level (e.g. `/infenchant 255`) |
| `/infenchant <enchantment> [level]` | Directly enchants your held item (default: 225) |
| `/infenchant all [level]` | Applies all registered enchantments to your item |
| `/infenchant clear` | Clears all enchantments from your held item |

**Short Aliases:** `/ie`, `/infiniteenchant`

#### Permission Node
- `infenchant.admin` — Grants access to `/infenchant` and the GUI (defaults to server operators).

---

### 📦 Installation

1. Download the latest `InfEnchant-x.x.x.jar`.
2. Drop it into your server's `plugins/` folder.
3. Restart your server (or load it using your favorite plugin manager).
4. Hold any item in your main hand and type `/infenchant`!

---

### 🛠️ Compatibility

- **Server Software:** Paper 1.21.11
- **Java Requirement:** Java 21 or higher.
