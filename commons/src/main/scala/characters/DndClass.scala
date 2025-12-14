package characters

import actions.CombatAction
import rolls.Die.{D4, D6, D8, D10, D12}

enum DndClass:
  case PALADIN(lvl: Int)
  case WIZARD(lvl: Int)
  case FIGHTER(lvl: Int)

  def action: CombatAction =
    this match
      case PALADIN(_) => CombatAction(2, D6)
      case WIZARD(_)  => CombatAction(1, D10)
      case FIGHTER(_) => CombatAction(1, D12)

  def bonusAction: Option[CombatAction] =
    this match
      case PALADIN(lvl) => if lvl > 3 then Some(CombatAction(1, D4)) else None
      case WIZARD(lvl)  => if lvl > 2 then Some(CombatAction(1, D4)) else None
      case FIGHTER(lvl) => if lvl > 1 then Some(CombatAction(1, D6)) else None