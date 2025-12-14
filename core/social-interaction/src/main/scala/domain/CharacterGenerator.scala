package domain

import characters.{DndCharacter, DndClass, DndRace}
import in.{ForGeneratingNPCs, ForGeneratingVillains}
import java.util.UUID
import scala.util.Random

class CharacterGenerator extends ForGeneratingNPCs, ForGeneratingVillains:
  private val random = new Random()

  override def generateRandomEnemy(level: Int): DndCharacter =
    val race = randomRace()
    val dndClass = randomClass(level)
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

  override def generateRandomNpc(): DndCharacter =
    val race = randomRace()
    DndCharacter(
      id = UUID.randomUUID().toString,
      name = "Traveler",
      dndRace = race,
      dndClass = DndClass.FIGHTER(1),
      shout = "Howdy",
      armorClass = 10,
      hp = 10,
      gold = 5
    )

  // --- Helpers ---

  private def randomShout(race: DndRace): String =
    race match
      case DndRace.GOBLIN => "Khkhkhkhkhkhk!"
      case DndRace.ORC    => "HULK!"
      case DndRace.ELF    => "You lack grace."
      case DndRace.DWARF  => "By my beard!"
      case DndRace.HUMAN  => "Bonjour!"

  private def randomRace(): DndRace =
    val races = DndRace.values
    races(random.nextInt(races.length))

  private def randomClass(level: Int): DndClass =
    random.nextInt(3) match
      case 0 => DndClass.PALADIN(level)
      case 1 => DndClass.FIGHTER(level)
      case _ => DndClass.WIZARD(level)

  private def calculateStats(dndClass: DndClass, level: Int): (Int, Int) =
    dndClass match
      case DndClass.PALADIN(_) => (22 + (level * 4), 13)
      case DndClass.FIGHTER(_) => (25 + (level * 5), 12)
      case DndClass.WIZARD(_)  => (16 + (level * 3), 11)

  private def className(dndClass: DndClass): String = dndClass.toString.takeWhile(_ != '(')