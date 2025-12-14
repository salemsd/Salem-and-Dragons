import actions.{CardinalDirection, NextAction}
import characters.{DndCharacter, DndClass, DndRace}
import domain.{FightingEngine, MapManager, MovementEngine, RandomMapGenerator}
import data.MutableCollectionDataStorageAdapter
import randomness.MachineDefaultRandomnessAdapter
import rendering.ConsoleRenderingAdapter
import model.{Coordinates, DndMapState}

import scala.annotation.tailrec
import scala.io.{Source, StdIn}
import scala.util.{Failure, Success, Try, Using}

@main def Main(): Unit =
  // Infra
  val dataStorage    = new MutableCollectionDataStorageAdapter()
  val rendering      = new ConsoleRenderingAdapter()
  val randomness     = new MachineDefaultRandomnessAdapter()

  // Core
  val mapManager     = new MapManager(dataStorage)
  val movementEngine = new MovementEngine(dataStorage)
  val fightingEngine = new FightingEngine(randomness, rendering, dataStorage)

  print("\u001b[2J") // clear
  println("=============================")
  println("   E5 & DRAGONS: THE GAME    ")
  println("=============================")
  println("1. Story Mode (Load Map)")
  println("2. Roguelike Mode (Create Character & Random Map)")
  print("\nChoose mode (1/2): ")

  val choice = StdIn.readLine().trim

  choice match
    case "2" =>
      val player = createCharacter()
      println("\nGenerating map...")
      val randomMap = RandomMapGenerator.generate(player, width = 20)
      dataStorage.saveMapState(randomMap)
      println("Map generated. Good luck!")
      Thread.sleep(1000)

    case _ =>
      println("\nEnter map file (default: e5-dungeon.dndmap):")
      print("> ")
      val input = StdIn.readLine().trim
      val mapRes = if input.isEmpty then "e5-dungeon.dndmap" else input

      println(s"Loading map '$mapRes'...")
      val loadResult = Using(Source.fromResource(mapRes)) { source =>
        mapManager.validateAndStoreMap(source.getLines().toList, None)
      }

      loadResult match
        case Failure(e) =>
          println(s"Fatal error: ${e.getMessage}")
          System.exit(1)
        case Success(Left(err)) =>
          println(s"Map error: $err")
          System.exit(1)
        case Success(Right(_)) =>
          println("Map loaded.")
          Thread.sleep(1000)

  var gameRunning = true

  while gameRunning do
    val currentState = dataStorage.getMapState

    rendering.renderMapState(currentState)

    println("\n Actions: (Z) North (S) South (Q) West (D) East | (X) Exit")
    print("> ")
    val input = StdIn.readLine()

    parseDirection(input) match
      case Some(direction) =>
        val action = movementEngine.move(direction)
        val status = handleAction(action, direction, currentState, fightingEngine, dataStorage)

        status match
          case GameStatus.GameOver =>
            gameRunning = false
            println("\nG A M E  O V E R")
          case GameStatus.Quit =>
            gameRunning = false
            println("\nGoodbye adventurer!")
          case GameStatus.Continue =>
            ()

      case None if input.equalsIgnoreCase("x") =>
        gameRunning = false
      case None =>
        println("Invalid direction!")
        Thread.sleep(800)

// --- HELPERS ---

def createCharacter(): DndCharacter =
  println("\n--- HERO CREATION ---")

  print("Enter Name: ")
  val name = Option(StdIn.readLine()).filter(_.nonEmpty).getOrElse("Traveler")

  print("Enter Battle Shout (Max 30 chars): ")
  val rawShout = StdIn.readLine().trim
  val shout = if rawShout.isEmpty then "Aaaaaaaaaa!" else rawShout.take(30)

  println("\nSelect Race:")
  DndRace.values.zipWithIndex.foreach { case (r, i) => println(s"${i + 1}. $r") }
  print("> ")
  val rIdx = Try(StdIn.readLine().toInt - 1).getOrElse(0)
  val race = DndRace.values.lift(rIdx).getOrElse(DndRace.HUMAN)

  println("\nSelect Class:")
  val classes = List(DndClass.PALADIN(1), DndClass.WIZARD(1), DndClass.FIGHTER(1))
  classes.zipWithIndex.foreach { case (c, i) => println(s"${i + 1}. ${c.toString.takeWhile(_ != '(')}") }
  print("> ")
  val cIdx = Try(StdIn.readLine().toInt - 1).getOrElse(0)
  val cls = classes.lift(cIdx).getOrElse(DndClass.PALADIN(1))

  val (baseHp, baseAc) = cls match
    case DndClass.PALADIN(_) => (20, 12)
    case DndClass.FIGHTER(_) => (22, 11)
    case DndClass.WIZARD(_)  => (14, 10)

  println(s"\n--- STAT ALLOCATION ---")
  println(s"Class Base Stats: HP=$baseHp | AC=$baseAc")
  println("You have 5 points to spend.")
  println("Costs: [1 Point = +5 HP] or [1 Point = +1 AC]")

  val (finalHp, finalAc) = allocateStats(points = 5, currentHp = baseHp, currentAc = baseAc)

  println(s"\nHero Created: $name ($race ${cls.toString.takeWhile(_ != '(')})")
  println(s"   HP: $finalHp | AC: $finalAc | Shout: '$shout'")
  Thread.sleep(1500)

  DndCharacter("PLAYER", name, race, cls, shout, finalAc, finalHp, 0)

@tailrec
def allocateStats(points: Int, currentHp: Int, currentAc: Int): (Int, Int) =
  if points <= 0 then
    (currentHp, currentAc)
  else
    println(s"\nPoints remaining: $points")
    println(s"Current Stats: HP=$currentHp | AC=$currentAc")
    println("1. +5 HP")
    println("2. +1 AC (Max 18)")
    print("Choose upgrade: ")

    StdIn.readLine().trim match
      case "1" =>
        allocateStats(points - 1, currentHp + 5, currentAc)
      case "2" =>
        if currentAc >= 18 then
          println("Max AC reached! Choose HP instead.")
          allocateStats(points, currentHp, currentAc)
        else
          allocateStats(points - 1, currentHp, currentAc + 1)
      case _ =>
        println("Invalid choice.")
        allocateStats(points, currentHp, currentAc)


enum GameStatus:
  case Continue, GameOver, Quit

def parseDirection(input: String): Option[CardinalDirection] =
  Option(input).map(_.toLowerCase).flatMap {
    case "z" | "n" => Some(CardinalDirection.NORTH)
    case "s"       => Some(CardinalDirection.SOUTH)
    case "q" | "o" | "w" => Some(CardinalDirection.WEST)
    case "d" | "e" => Some(CardinalDirection.EAST)
    case _ => None
  }

def handleAction(
                  action: NextAction,
                  direction: CardinalDirection,
                  stateBeforeAction: DndMapState,
                  fightingEngine: FightingEngine,
                  dataOps: MutableCollectionDataStorageAdapter
                ): GameStatus =
  action match
    case NextAction.MOVE => GameStatus.Continue
    case NextAction.LOOT =>
      println("You found gold!")
      Thread.sleep(800)
      GameStatus.Continue
    case NextAction.TALK =>
      println("NPC: 'Run Forrest, run...'")
      Thread.sleep(1000)
      GameStatus.Continue
    case NextAction.FIGHT =>
      val fresh = dataOps.getMapState
      val target = getTargetCoordinates(fresh.characterCoordinates, fresh.characterDirection)

      fresh.villains.find(_._1 == target) match
        case Some((_, villain)) =>
          fightingEngine.fight(fresh.character, villain) match
            case Left(_) => GameStatus.GameOver
            case Right(_) =>
              val post = dataOps.getMapState
              if post.villains.find(_._1 == target).exists(_._2.hp <= 0) then
                println(s"Enemy defeated!")
                val alive = post.villains.filterNot(_._1 == target)
                dataOps.saveMapState(post.copy(villains = alive))
                Thread.sleep(1000)
              GameStatus.Continue
        case None => GameStatus.Continue

def getTargetCoordinates(pos: Coordinates, dir: CardinalDirection): Coordinates =
  dir match
    case CardinalDirection.NORTH => Coordinates(pos.x, pos.y - 1)
    case CardinalDirection.SOUTH => Coordinates(pos.x, pos.y + 1)
    case CardinalDirection.EAST  => Coordinates(pos.x + 1, pos.y)
    case CardinalDirection.WEST  => Coordinates(pos.x - 1, pos.y)