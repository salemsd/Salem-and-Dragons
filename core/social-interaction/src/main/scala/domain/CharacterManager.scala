package domain

import characters.DndCharacter
import in.ForInteracting
import out.ConsoleRenderingAdapter

class CharacterManager(dataPort: ConsoleRenderingAdapter) extends ForInteracting:
  override def talkTo(character: DndCharacter): String =
    val quote = dataPort.getRandomDialogue
    s"${character.name}: '$quote'"