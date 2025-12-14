package domain

import characters.{DndCharacter, DndClass, DndRace}
import actions.CardinalDirection
import model.{Coordinates, DndMapState}
import out.ExplorationDataPortOut
import munit.FunSuite

class MapManagerSpec extends FunSuite:
  class MockDataPort extends ExplorationDataPortOut:
    var savedState: Option[DndMapState] = None

    override def saveMapState(dndMap: DndMapState): Unit =
      savedState = Some(dndMap)

    override def getMapState: DndMapState =
      savedState.getOrElse(throw new Exception("Empty state"))

  test("MapManager should parse a valid map correctly") {
    val mapContent = List(
      "M - 3 - 4",
      "NPC - 1 - 0",
      "PC - 2 - 1 - 6 - HUMAN - PALADIN - 13 - 25",
      "C - 0 - 0 - 6 - HUMAN - PALADIN - 13 - 25 - S",
      "GP - 0 - 3 - 2"
    )

    val mockPort = new MockDataPort()
    val manager  = new MapManager(mockPort)

    val result = manager.validateAndStoreMap(mapContent)

    assert(result.isRight)

    val state = mockPort.savedState.get
    assertEquals(state.width, 3)
    assertEquals(state.height, 4)
    assertEquals(state.characterCoordinates, Coordinates(0, 0))
    assertEquals(state.characterDirection, CardinalDirection.SOUTH)
    assertEquals(state.treasures.head, (Coordinates(0, 3), 2))
    assertEquals(state.villains.head._1, Coordinates(2, 1))
  }

  test("MapManager should fail on invalid map data") {
    val invalidContent = List("M - 3 - 4", "InvalidLine") // Missing Character
    val mockPort = new MockDataPort()
    val manager  = new MapManager(mockPort)

    val result = manager.validateAndStoreMap(invalidContent)
    assert(result.isLeft)
  }