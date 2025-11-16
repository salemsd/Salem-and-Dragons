package rendering

import model.{DndMapState, FightState}
import out.{ExplorationRenderingPortOut, FightRenderingPortOut}

class ConsoleRenderingAdapter extends FightRenderingPortOut, ExplorationRenderingPortOut:
  override def renderFightState(fightState: FightState): Unit = ???

  override def renderMapState(dndMap: DndMapState): Unit = ???
