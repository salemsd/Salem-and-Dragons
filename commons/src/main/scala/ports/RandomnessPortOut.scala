package ports

import rolls.Die

trait RandomnessPortOut:
  def getRandom(die: Die): Int