package randomness

import out.RandomnessPortOut
import rolls.Die

import scala.util.Random

class MachineDefaultRandomnessAdapter extends RandomnessPortOut:
  private val random = new Random()

  override def getRandom(die: Die): Int = die match
    case Die.D4  => random.nextInt(4) + 1
    case Die.D6  => random.nextInt(6) + 1
    case Die.D8  => random.nextInt(8) + 1
    case Die.D10 => random.nextInt(10) + 1
    case Die.D12 => random.nextInt(12) + 1
    case Die.D20 => random.nextInt(20) + 1