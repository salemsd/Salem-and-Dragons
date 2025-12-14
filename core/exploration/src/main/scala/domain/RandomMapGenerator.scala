package domain

import actions.CardinalDirection
import characters.{DndCharacter, DndClass, DndRace}
import model.{Coordinates, DndMapState}

import java.util.UUID
import scala.util.Random

object RandomMapGenerator:

  private val random = new Random()

  def generate(player: DndCharacter, width: Int = 15, height: Int = 10, difficulty: Int = 1): DndMapState =
    val playerCoords = Coordinates(0, 0)

    // Enemies
    val enemyCount = (width * height) / 12
    val villains = (1 to enemyCount).map { _ =>
      val coords = randomEmptyCoords(width, height, occupied = List(playerCoords))
      (coords, generateRandomEnemy(difficulty))
    }.toList

    // Treasures
    val treasureCount = (width * height) / 15
    val treasures = (1 to treasureCount).map { _ =>
      val coords = randomEmptyCoords(width, height, occupied = List(playerCoords) ++ villains.map(_._1))
      (coords, random.nextInt(50) + 10)
    }.toList

    // NPC
    val npcCount = 2
    val npcs = (1 to npcCount).map { _ =>
      randomEmptyCoords(width, height, occupied = List(playerCoords) ++ villains.map(_._1) ++ treasures.map(_._1))
    }.toList

    DndMapState(
      width = width,
      height = height,
      character = player,
      characterCoordinates = playerCoords,
      characterDirection = CardinalDirection.SOUTH,
      villains = villains,
      npcs = npcs,
      treasures = treasures
    )

  private def generateRandomEnemy(level: Int): DndCharacter =
    val race = DndRace.values(random.nextInt(DndRace.values.length))

    val availableClasses = List(
      DndClass.PALADIN(level),
      DndClass.FIGHTER(level),
      DndClass.WIZARD(level)
    )
    val dndClass = availableClasses(random.nextInt(availableClasses.length))

    val (hp, ac) = calculateStats(dndClass, level)

    DndCharacter(
      id = UUID.randomUUID().toString,
      name = s"$race ${className(dndClass)}",
      dndRace = race,
      dndClass = dndClass,
      shout = randomShout(race),
      armorClass = ac,
      hp = hp,
      gold = random.nextInt(10) + (level * 2)
    )

  private def calculateStats(dndClass: DndClass, level: Int): (Int, Int) =
    dndClass match
      case DndClass.PALADIN(_) => (22 + (level * 4), 13)
      case DndClass.FIGHTER(_) => (25 + (level * 5), 12)
      case DndClass.WIZARD(_)  => (16 + (level * 3), 11)

  private def className(dndClass: DndClass): String =
    dndClass.toString.takeWhile(_ != '(')

  private def randomShout(race: DndRace): String =
    race match
      case DndRace.GOBLIN => "Khkhkhkhkhkhk!"
      case DndRace.ORC    => "HULK!"
      case DndRace.ELF    => "You lack grace."
      case DndRace.DWARF  => "By my beard!"
      case DndRace.HUMAN  => "Bonjour!"

  private def randomEmptyCoords(w: Int, h: Int, occupied: List[Coordinates]): Coordinates =
    var c = Coordinates(random.nextInt(w), random.nextInt(h))
    var tries = 0
    while occupied.contains(c) && tries < 100 do
      c = Coordinates(random.nextInt(w), random.nextInt(h))
      tries += 1
    c