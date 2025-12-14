package domain

import characters.DndCharacter
import errors.Death
import in.{ForFighting, NewCharacterState}
import model.FightState
import out.{CombatDataPortOut, FightRenderingPortOut, RandomnessPortOut}
import rolls.Die

class FightingEngine(
                      randomnessPortOut: RandomnessPortOut,
                      renderingPortOut: FightRenderingPortOut,
                      dataPortOut: CombatDataPortOut
                    ) extends ForFighting:

  override def fight(character: DndCharacter, villain: DndCharacter): Either[Death, NewCharacterState] =
    val charInit = randomnessPortOut.getRandom(Die.D20)
    val villInit = randomnessPortOut.getRandom(Die.D20)

    val (first, second) = if charInit >= villInit then (character, villain) else (villain, character)

    val (p1, p2) = resolveAttack(first, second)

    if p2.hp <= 0 then
      finalizeRound(p1, p2, character)
    else
      val (finalSecond, finalFirst) = resolveAttack(p2, p1)
      finalizeRound(finalFirst, finalSecond, character)

  private def resolveAttack(attacker: DndCharacter, defender: DndCharacter): (DndCharacter, DndCharacter) =
    val attackRoll = randomnessPortOut.getRandom(Die.D20)
    val isHit = attackRoll >= defender.armorClass

    val (damageDealt, newDefender) = if isHit then
      val dmgAction = attacker.dndClass.action
      val baseDmg   = rollDice(dmgAction.diceAmount, dmgAction.diceRoll)
      val bonusDmg  = attacker.dndClass.bonusAction.map(a => rollDice(a.diceAmount, a.diceRoll)).getOrElse(0)
      val totalDmg  = baseDmg + bonusDmg
      (totalDmg, defender.copy(hp = defender.hp - totalDmg))
    else
      (0, defender)

    renderingPortOut.renderFightState(FightState(attacker, defender, attackRoll, damageDealt, isHit))

    (attacker, newDefender)

  private def rollDice(amount: Int, die: Die): Int =
    (1 to amount).map(_ => randomnessPortOut.getRandom(die)).sum

  private def finalizeRound(c1: DndCharacter, c2: DndCharacter, originalPc: DndCharacter): Either[Death, NewCharacterState] =
    val (newPc, newVillain) = if c1.id == originalPc.id then (c1, c2) else (c2, c1)
    dataPortOut.saveCharacterState(newPc, newVillain)
    if newPc.hp <= 0 then Left(Death()) else Right(newPc)