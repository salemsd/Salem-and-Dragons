package model

import actions.CardinalDirection
import characters.DndCharacter

case class Coordinates(x: Int, y: Int)

case class DndMapState(
    width: Int,
    height: Int,
    character: DndCharacter,
    characterCoordinates: Coordinates,
    characterDirection: CardinalDirection,
    villains: List[(Coordinates, DndCharacter)],
    npcs: List[Coordinates],
    treasures: List[(Coordinates, Int)]
)
