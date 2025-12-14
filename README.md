# E5 N Dragons (END)
Parse a map, move your character, fight PCs (optional: talk to NPCs), collect treasures, render every step of the game on your shell

## Project structure
![structure](./assets/projet-E5.svg)


## About scalafmt
I recommend using scalafmt on save (Settings -> Editor > Code Style > Scala) then select .scalafmt.conf

## Work submission
### How to submit your work
Create a repo on any git provider and invite me
If you are using Gitlab or BitBucket hit me up on Discord

Github ID: guihardbastien

### Deadline
**On December 1st** we'll have a few questions about your project I strongly recommend it is finished by then. 
However, if you still have some tweaks to add, you can still commit some work until December 8th

---

# What was added

### 1. Game Modes & Progression
* **Roguelike Mode:** Added a random map generator (`RandomMapGenerator`) that creates random dungeons with dirreent difficulty (enemies, loot, and NPCs).
* **Story Mode:** Implemented a map loader allowing the player to choose between multiple pre-designed maps (`arena.dndmap`, `orc_ambush.dndmap`, `testing.dndmap`).

### 2. Advanced Character Creation
* **Interactive Setup:** Players can now customize their Hero's Name, Race, Class, and Battle Shout via the terminal.
* **Stat Allocation System:** Implemented a system allowing players to distribute points between **HP** and **Armor Class (AC)** with limits.
* **Expanded Classes:** Added `WIZARD` (d10 damage) and `FIGHTER` (d12 damage) alongside the existing `PALADIN`.

### 3. Combat Engine & Mechanics
* **Turn-Based Logic:** Fully implemented `FightingEngine` with Initiative rolls to determine turn order.
* **AC & Attack Rolls:** Implemented D&D style combat mechanics where attacks must roll against the defender's Armor Class (AC) to hit.
* **Robust Identity:** Refactored the `DndCharacter` model to use unique IDs (`UUID`) and display Names, ensuring the combat log correctly identifies who attacks whom (e.g., "Orc Enemy vs Player") instead of generic labels.

### 4. Technical Improvements
* **Rendering:** Enhanced `ConsoleRenderingAdapter` to provide detailed combat feedback (rolls, hits, misses, damage dealt) and specific direction symbols for the player.
 
