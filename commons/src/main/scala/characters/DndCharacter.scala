package characters

case class DndCharacter(
                         id: String,
                         name: String,
                         dndRace: DndRace,
                         dndClass: DndClass,
                         shout: String,
                         armorClass: Int,
                         hp: Int,
                         gold: Int
                       )