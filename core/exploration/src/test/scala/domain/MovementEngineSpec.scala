package domain

import actions.{CardinalDirection, NextAction}
import characters.{DndCharacter, DndClass, DndRace}
import model.{Coordinates, DndMapState}
import out.ExplorationDataPortOut
import munit.FunSuite

class MovementEngineSpec extends FunSuite:
  val hero = DndCharacter(DndRace.HUMAN, DndClass.PALADIN(1), "Hero", 10, 10, 0)
  val villain = DndCharacter(DndRace.HUMAN, DndClass.PALADIN(1), "Villain", 10, 10, 0)

  class MockDataPort(initialState: DndMapState) extends ExplorationDataPortOut:
    var storedState: DndMapState = initialState

    override def getMapState: DndMapState = storedState
    override def saveMapState(dndMap: DndMapState): Unit = storedState = dndMap

  // Simple map
  // (0,0) (1,0) (2,0)
  // (0,1) (1,1) (2,1)
  // (0,2) (1,2) (2,2)
  def createTestMap(
                     charCoords: Coordinates,
                     direction: CardinalDirection,
                     villains: List[(Coordinates, DndCharacter)] = List.empty,
                     npcs: List[Coordinates] = List.empty,
                     treasures: List[(Coordinates, Int)] = List.empty
                   ): DndMapState =
    DndMapState(
      width = 3,
      height = 3,
      character = hero,
      characterCoordinates = charCoords,
      characterDirection = direction,
      villains = villains,
      npcs = npcs,
      treasures = treasures
    )

  test("Should MOVE to empty cell and update coordinates") {
    // Playet at (1,1) goes north (0,1) (empty).
    val startState = createTestMap(Coordinates(1, 1), CardinalDirection.NORTH)
    val port       = new MockDataPort(startState)
    val engine     = new MovementEngine(port)

    val action = engine.move(CardinalDirection.NORTH)

    assertEquals(action, NextAction.MOVE)
    assertEquals(port.storedState.characterCoordinates, Coordinates(1, 0)) // A bougé en Y-1
    assertEquals(port.storedState.characterDirection, CardinalDirection.NORTH)
  }

  test("Should ROTATE only when hitting a WALL") {
    // Player at (0,0) goes north (beyond limits)
    val startState = createTestMap(Coordinates(0, 0), CardinalDirection.EAST) // Initialement EST
    val port       = new MockDataPort(startState)
    val engine     = new MovementEngine(port)

    val action = engine.move(CardinalDirection.NORTH)

    assertEquals(action, NextAction.MOVE)
    assertEquals(port.storedState.characterCoordinates, Coordinates(0, 0))
    assertEquals(port.storedState.characterDirection, CardinalDirection.NORTH)
  }

  test("Should FIGHT when hitting a VILLAIN") {
    // Player at (0,0), villain at (1,0)
    val startState = createTestMap(
      Coordinates(0, 0),
      CardinalDirection.SOUTH,
      villains = List((Coordinates(1, 0), villain))
    )
    val port   = new MockDataPort(startState)
    val engine = new MovementEngine(port)

    val action = engine.move(CardinalDirection.EAST)

    assertEquals(action, NextAction.FIGHT)
    assertEquals(port.storedState.characterCoordinates, Coordinates(0, 0))
    assertEquals(port.storedState.characterDirection, CardinalDirection.EAST)
  }

  test("Should TALK when hitting an NPC") {
    // Player at (0,0), NPC at (0,1)
    val startState = createTestMap(
      Coordinates(0, 0),
      CardinalDirection.EAST,
      npcs = List(Coordinates(0, 1))
    )
    val port   = new MockDataPort(startState)
    val engine = new MovementEngine(port)

    val action = engine.move(CardinalDirection.SOUTH)

    assertEquals(action, NextAction.TALK)
    assertEquals(port.storedState.characterCoordinates, Coordinates(0, 0))
    assertEquals(port.storedState.characterDirection, CardinalDirection.SOUTH)
  }

  test("Should LOOT and move when hitting TREASURE") {
    // Player at (0,0), gold (100) at (1,0)
    val startState = createTestMap(
      Coordinates(0, 0),
      CardinalDirection.SOUTH,
      treasures = List((Coordinates(1, 0), 100))
    )
    val port   = new MockDataPort(startState)
    val engine = new MovementEngine(port)

    assertEquals(startState.character.gold, 0)

    val action = engine.move(CardinalDirection.EAST)

    assertEquals(action, NextAction.LOOT)
    assertEquals(port.storedState.characterCoordinates, Coordinates(1, 0))
    assertEquals(port.storedState.characterDirection, CardinalDirection.EAST)
    assertEquals(port.storedState.character.gold, 100)
    assert(port.storedState.treasures.isEmpty)
  }