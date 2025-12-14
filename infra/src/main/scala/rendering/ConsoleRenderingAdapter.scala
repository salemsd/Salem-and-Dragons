package rendering

import actions.CardinalDirection
import model.{DndMapState, FightState}
import out.{ExplorationRenderingPortOut, FightRenderingPortOut}


class ConsoleRenderingAdapter extends FightRenderingPortOut, ExplorationRenderingPortOut:
  override def renderFightState(state: FightState): Unit =
    val attacker = state.attacker.name
    val target = state.defender.name

    if state.isHit then
      println(s"$attacker attacks (Roll ${state.attackRoll}) and HITS $target for ${state.damageDealt} damage !")
    else
      println(s"$attacker attacks (Roll ${state.attackRoll}) but MISSES $target (AC ${state.defender.armorClass}).")

    Thread.sleep(1600)

  override def renderMapState(dndMap: DndMapState): Unit =
    print("\u001b[2J") // clear
    println(s"--- MAP (${dndMap.width}x${dndMap.height}) ---")

    for (y <- 0 until dndMap.height) do
      val line = (0 until dndMap.width).map { x =>
        renderCell(x, y, dndMap)
      }.mkString(" ")
      println(line)

    println("-" * 20)
    println(s"${dndMap.character.name} (HP: ${dndMap.character.hp} | GOLD: ${dndMap.character.gold})")
    println(s"Pos: (${dndMap.characterCoordinates.x}, ${dndMap.characterCoordinates.y}) | Dir: ${dndMap.characterDirection}")
    println("-" * 20)

  private def renderCell(x: Int, y: Int, map: DndMapState): String =
    val currentCoords = model.Coordinates(x, y)

    if map.characterCoordinates == currentCoords then
      directionSymbol(map.characterDirection)
    else if map.villains.exists(_._1 == currentCoords) then
      "E" // Enemy
    else if map.npcs.contains(currentCoords) then
      "?" // NPC
    else if map.treasures.exists(_._1 == currentCoords) then
      "$" // Gold
    else
      "." // Empty

  private def directionSymbol(dir: CardinalDirection): String = dir match
    case CardinalDirection.NORTH => "^"
    case CardinalDirection.SOUTH => "v"
    case CardinalDirection.EAST  => ">"
    case CardinalDirection.WEST  => "<"
