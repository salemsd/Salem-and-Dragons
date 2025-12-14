package out

import model.DndMapState

trait ExplorationDataPortOut:
  def saveMapState(dndMap: DndMapState): Unit
  def getMapState: DndMapState
