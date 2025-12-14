package model

import characters.DndCharacter

case class FightState(
                       attacker: DndCharacter,
                       defender: DndCharacter,
                       attackRoll: Int,
                       damageDealt: Int,
                       isHit: Boolean
                     )