package domain

import actions.CardinalDirection
import characters.DndCharacter
import in.{ForGeneratingNPCs, ForGeneratingVillains}
import model.{Coordinates, DndMapState}

import scala.util.Random

class RandomMapGenerator(
                          villainProvider: ForGeneratingVillains,
                          npcProvider: ForGeneratingNPCs
                        ):

  private val random = new Random()

  def generate(player: DndCharacter, width: Int = 15, height: Int = 10, difficulty: Int = 1): DndMapState =
    val playerCoords = Coordinates(0, 0)

    val enemyCount = (width * height) / 12
    val villains = (1 to enemyCount).map { _ =>
      val coords = randomEmptyCoords(width, height, occupied = List(playerCoords))
      (coords, villainProvider.generateRandomEnemy(difficulty))
    }.toList

    val treasureCount = (width * height) / 15
    val treasures = (1 to treasureCount).map { _ =>
      val coords = randomEmptyCoords(width, height, occupied = List(playerCoords) ++ villains.map(_._1))
      (coords, random.nextInt(50) + 10)
    }.toList
    
    val npcCount = 4
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

  private def randomEmptyCoords(w: Int, h: Int, occupied: List[Coordinates]): Coordinates =
    var c = Coordinates(random.nextInt(w), random.nextInt(h))
    var tries = 0
    while occupied.contains(c) && tries < 100 do
      c = Coordinates(random.nextInt(w), random.nextInt(h))
      tries += 1
    c