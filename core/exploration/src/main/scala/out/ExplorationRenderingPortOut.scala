package out

import model.DndMapState

trait ExplorationRenderingPortOut:
  def renderMapState(dndMap: DndMapState): Unit
