package domain

import actions.{CardinalDirection, NextAction}
import in.ForMovingCharacter
import model.{Coordinates, DndMapState}
import out.ExplorationDataPortOut

class MovementEngine(dataPort: ExplorationDataPortOut) extends ForMovingCharacter:

  override def move(direction: CardinalDirection): NextAction =
    val state = dataPort.getMapState

    // Calculate Target
    val (currX, currY) = (state.characterCoordinates.x, state.characterCoordinates.y)
    val (dx, dy) = direction match
      case CardinalDirection.NORTH => (0, -1)
      case CardinalDirection.SOUTH => (0, 1)
      case CardinalDirection.EAST  => (1, 0)
      case CardinalDirection.WEST  => (-1, 0)

    val targetCoords = Coordinates(currX + dx, currY + dy)

    // Check collisions an,d interactions
    if isOutOfBounds(targetCoords, state.width, state.height) then
      // If you hit a wall it rotates
      updateState(state, state.characterCoordinates, direction)
      NextAction.MOVE

    else if state.villains.exists(_._1 == targetCoords) then
      // If you hit a villain you turn to them and fight
      updateState(state, state.characterCoordinates, direction)
      NextAction.FIGHT

    else if state.npcs.contains(targetCoords) then
      // If you hit and npc you turn to them and talk
      updateState(state, state.characterCoordinates, direction)
      NextAction.TALK

    else
      // Valid Move
      // Check for Treasure
      state.treasures.find(_._1 == targetCoords) match
        case Some((_, amount)) =>
          // Move + collect gold
          val richCharacter = state.character.copy(gold = state.character.gold + amount)
          val remainingTreasures = state.treasures.filterNot(_._1 == targetCoords)

          val newState = state.copy(
            character = richCharacter,
            characterCoordinates = targetCoords,
            characterDirection = direction,
            treasures = remainingTreasures
          )
          dataPort.saveMapState(newState)
          NextAction.LOOT

        case None =>
          // Empty tile: move
          updateState(state, targetCoords, direction)
          NextAction.MOVE

  private def isOutOfBounds(coords: Coordinates, w: Int, h: Int): Boolean =
    coords.x < 0 || coords.x >= w || coords.y < 0 || coords.y >= h

  private def updateState(currentState: DndMapState, newCoords: Coordinates, newDir: CardinalDirection): Unit =
    val newState = currentState.copy(
      characterCoordinates = newCoords,
      characterDirection = newDir
    )
    dataPort.saveMapState(newState)