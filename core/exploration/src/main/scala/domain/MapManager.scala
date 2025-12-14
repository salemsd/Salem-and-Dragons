package domain

import characters.{DndCharacter, DndClass, DndRace}
import actions.CardinalDirection
import errors.MapError
import in.ForValidatingMap
import model.{Coordinates, DndMapState}
import out.ExplorationDataPortOut

import java.util.UUID
import scala.util.{Failure, Success, Try}

class MapManager(dataPort: ExplorationDataPortOut) extends ForValidatingMap:

  // We overload the method to allow passing a custom character
  def validateAndStoreMap(dataLines: List[String], customPlayer: Option[DndCharacter] = None): Either[MapError, Unit] =
    val sanitizedLines = dataLines.map(_.trim).filterNot(_.isEmpty).filterNot(_.startsWith("--"))

    tryParseMap(sanitizedLines, customPlayer) match
      case Success(dndMapState) =>
        dataPort.saveMapState(dndMapState)
        Right(())
      case Failure(_) =>
        Left(MapError.IllegalMapFormat())

  // Base method from trait (defaults to no custom player for backward compatibility if needed)
  override def validateAndStoreMap(dataLines: List[String]): Either[MapError, Unit] =
    validateAndStoreMap(dataLines, None)

  private def tryParseMap(lines: List[String], customPlayer: Option[DndCharacter]): Try[DndMapState] = Try {
    var width = 0
    var height = 0
    var character: Option[DndCharacter] = None
    var charCoords: Option[Coordinates] = None
    var charDir: Option[CardinalDirection] = None
    var villains: List[(Coordinates, DndCharacter)] = List.empty
    var npcs: List[Coordinates] = List.empty
    var treasures: List[(Coordinates, Int)] = List.empty

    lines.foreach { line =>
      val cols = line.split(" - ").map(_.trim)
      cols.head match
        case "M" =>
          width = cols(1).toInt
          height = cols(2).toInt
        case "C" =>
          val x = cols(1).toInt
          val y = cols(2).toInt
          val dir = CardinalDirection.valueOf(mappingDirection(cols(8)))

          charCoords = Some(Coordinates(x, y))
          charDir = Some(dir)
          
          character = customPlayer match
            case Some(player) => Some(player)
            case None =>
              val race = DndRace.valueOf(cols(4))
              Some(DndCharacter(
                id = "PLAYER",
                name = "Hero",
                dndRace = race,
                dndClass = parseClass(cols(5), cols(3).toInt),
                shout = "I am ready!",
                armorClass = cols(6).toInt,
                hp = cols(7).toInt,
                gold = 0
              ))

        case "PC" =>
          val race = DndRace.valueOf(cols(4))
          val villain = DndCharacter(
            id = UUID.randomUUID().toString,
            name = s"$race Enemy",
            dndRace = race,
            dndClass = parseClass(cols(5), cols(3).toInt),
            shout = "Die mortal!",
            armorClass = cols(6).toInt,
            hp = cols(7).toInt,
            gold = 0
          )
          villains = villains :+ (Coordinates(cols(1).toInt, cols(2).toInt), villain)

        case "NPC" => npcs = npcs :+ Coordinates(cols(1).toInt, cols(2).toInt)
        case "GP"  => treasures = treasures :+ (Coordinates(cols(1).toInt, cols(2).toInt), cols(3).toInt)
        case _     => // Ignore
    }

    if (width == 0 || height == 0 || character.isEmpty) throw new RuntimeException("Invalid Map: Missing dimensions or start pos")

    DndMapState(width, height, character.get, charCoords.get, charDir.get, villains, npcs, treasures)
  }

  private def parseClass(className: String, lvl: Int): DndClass =
    className match
      case "PALADIN" => DndClass.PALADIN(lvl)
      case "WIZARD"  => DndClass.WIZARD(lvl)
      case "FIGHTER" => DndClass.FIGHTER(lvl)
      case _         => throw new IllegalArgumentException(s"Unknown class: $className")

  private def mappingDirection(code: String): String =
    code match { case "N" => "NORTH"; case "S" => "SOUTH"; case "E" => "EAST"; case "W" => "WEST"; case o => o }