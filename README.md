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

### 3. Social Interaction Module
* **Modular Architecture:** Implemented a dedicated `social-interaction` module following Hexagonal Architecture to handle NPC logic separately from combat and exploration.
* **Dynamic Dialogues:** Players can interact with NPCs to trigger random dialogues, which pulls content from an adapter.
* **Generation:** The `CharacterGenerator` service is injected into the map generator to create random Enemies and NPCs with diffeerent races and shouts.

### 4. Combat Engine & Mechanics
* **Turn-Based Logic:** Fully implemented `FightingEngine` with Initiative rolls to determine turn order.
* **AC & Attack Rolls:** Implemented D&D style combat mechanics where attacks must roll against the defender's Armor Class (AC) to hit.
* **Robust Identity:** Refactored the `DndCharacter` model to use unique IDs (`UUID`) and display Names, ensuring the combat log correctly identifies who attacks whom (e.g., "Orc Enemy vs Player") instead of generic labels.

### 5. Technical Improvements
* **Rendering:** Enhanced `ConsoleRenderingAdapter` to provide detailed combat feedback (rolls, hits, misses, damage dealt) and specific direction symbols for the player.

---

# How to Play

### 1. Prerequisites
Before starting, ensure you have **Java** and **sbt** (Scala Build Tool) installed on your machine.

### 2. Running the Game
Open your terminal in the project root folder and run the following command:

```bash
sbt "project endGame" run
````

### 3\. Game Interface

The game is played entirely in the terminal. Here is what the symbols mean:

* **^ / v / \< / \>** : Your Character (The arrow points to where you are looking)
* **E** : Enemy (Goblin, Orc, etc.)
* **$** : Treasure (Gold)
* **?** : NPC (Friendly character)
* **.** : Empty floor

**Stats interface:**
Below the map, you will see your stats:

> **Hero Name** (HP: 25 | GOLD: 100)
> Pos: (X, Y) | Dir: SOUTH

### 4\. Controls

Type the letter corresponding to your desired action and press **ENTER**.

* **Z** or **N** : Move **NORTH** (Up)
* **S** : Move **SOUTH** (Down)
* **Q** or **W** : Move **WEST** (Left)
* **D** or **E** : Move **EAST** (Right)
* **X** : **EXIT** the game

### 5\. Mechanics

* **Movement:** If you walk into a wall, you will just rotate to face it.
* **Combat:** Walk into an **Enemy (E)** to attack them. You may need to hit them multiple times to defeat them.
* **Looting:** Walk onto a **Treasure ($)** to pick it up automatically.
* **Talking:** Walk into an **NPC (?)** to hear what they have to say.

-----