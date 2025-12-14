package data

import characters.DndCharacter
import model.DndMapState
import out.{CombatDataPortOut, ExplorationDataPortOut}

class MutableCollectionDataStorageAdapter extends ExplorationDataPortOut, CombatDataPortOut:
  private var currentMapState: Option[DndMapState] = None

  override def saveMapState(dndMap: DndMapState): Unit = currentMapState = Some(dndMap)

  override def getMapState: DndMapState = currentMapState.getOrElse(throw new RuntimeException("Map state not initialized. Call saveMapState first."))

  override def saveCharacterState(dndCharacter: DndCharacter, villain: DndCharacter): Unit =
    val currentState = getMapState

    val updatedVillains = currentState.villains.map { case (coords, v) =>
      if v.id == villain.id then (coords, villain) else (coords, v)
    }

    val newState = currentState.copy(
      character = dndCharacter,
      villains = updatedVillains
    )
    saveMapState(newState)
