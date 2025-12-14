package social

import out.ConsoleRenderingAdapter

import scala.util.Random

class RandomInteractionAdapter extends ConsoleRenderingAdapter:
  private val quotes = List(
    "Le gras, c'est la vie !",
    "Vous êtes alchimiste pas vrai ? Vous pouvez me faire une bière ?",
    "Vous ne passerez pas !",
    "Avant, moi aussi j'étais aventurier comme vous... puis j'ai pris une flèche dans le genou.",
    "Laissez-moi devinez, quelqu'un vous a volé votre pâtisserie ?",
    "Fuyez, pauvres fous !",
    "Oui vous m'intéressez, et moi, je vous intéresse ?"
  )

  override def getRandomDialogue: String =
    quotes(Random.nextInt(quotes.length))