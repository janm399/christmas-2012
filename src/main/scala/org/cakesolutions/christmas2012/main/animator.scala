package org.cakesolutions.christmas2012.main

/**
 * Performs single-threaded blocking sparkling from the mixed-in ``Sparkler``
 */
trait BlockingSparkler {
  this: Sparkler =>

  implicit class TreeAnimation(tree: Tree) {
    def ||| { stages.foreach { s =>
      sparkle(tree)(s)
      Thread.sleep(500)
      }
    }
  }
}

trait Sparkler {
  type Stage
  def stages: Stream[Stage]
  def sparkle(tree: Tree)(stage: Stage)
}

trait ConsoleSparkler extends Sparkler {
  type Stage = ConsoleSparkleStage

  case class AnsiString(ansiText: String, realLength: Int) {
    def +(that: AnsiString) = AnsiString(ansiText + that.ansiText, realLength + that.realLength)
    def +(that: String)     = AnsiString(ansiText + that, realLength + that.length)
    override def toString = ansiText
  }
  object AnsiString {
    def apply(text: String): AnsiString = AnsiString(text, text.length)
    def zero = apply("")
  }

  sealed trait ConsoleSparkleStage {
    def ansiColour(f: Tree => String)(tree: Tree): AnsiString
  }
  case object One extends ConsoleSparkleStage {
    def ansiColour(f: Tree => String)(tree: Tree): AnsiString = {
      val c = tree match {
        case _: LeftNeedle     => ANSI_GREEN
        case _: RightNeedle    => ANSI_GREEN
        case _: Spike          => ANSI_PURPLE
        case _: Ball           => ANSI_RED
        case _: BigBall        => ANSI_CYAN
        case _: ElectricCandle => ANSI_BLUE
        case _                 => ANSI_WHITE
      }
      val s = f(tree)
      AnsiString(ANSI_BOLD + c + s, s.length)
    }
  }
  case object Two extends ConsoleSparkleStage {
    def ansiColour(f: Tree => String)(tree: Tree): AnsiString = {
      val c = tree match {
        case _: LeftNeedle     => ANSI_GREEN
        case _: RightNeedle    => ANSI_GREEN
        case _: ElectricCandle => ANSI_YELLOW
        case _                 => ANSI_WHITE
      }
      val s = f(tree)
      AnsiString(ANSI_BOLD + c + s, s.length)
    }
  }

  val stages = Stream.from(0).map { s =>
    if (s % 2 == 0) One else Two
  }

  private val ANSI_CLEAR  = "\033[2J\033[;H"

  private val ANSI_BOLD  = "\u001B[1m"
  private val ANSI_BLACK  = "\u001B[30m"
  private val ANSI_RED    = "\u001B[31m"
  private val ANSI_YELLOW = "\u001B[33m"
  private val ANSI_CYAN   = "\u001B[36m"
  private val ANSI_WHITE  = "\u001B[37m"

  private val ANSI_GREEN  = "\u001B[32m"
  private val ANSI_PURPLE = "\u001B[35m"
  private val ANSI_BLUE   = "\u001B[34m"
  private val ANSI_RESET  = "\u001B[0m"

  def sparkle(tree: Tree)(stage: Stage) {
    val printNode = stage.ansiColour {
        case _: LeftNeedle => "/"
        case _: RightNeedle => "\\"
        case _: Trunk => "|"
        case _: Ball => "o"
        case _: Spike => "x"
        case _: Candle => "*"
        case _: BigBall => "oxo"
        case _: DoubleBall => "oo"
        case _: ElectricCandle => "***"
      } _

    def walk(t: Tree, depth: Int): List[AnsiString] = {
      def walkLevel(t: Tree, acc: AnsiString,
                    f: (Tree) => AnsiString): (Tree, AnsiString) = {
        t match {
          case l: LeftNeedle => (l.left, f(l) + "." + acc)
          case t: TreeNode => walkLevel(t.left, f(t) + "." + acc, f)
        }
      }

      t match {
        case r: RightNeedle =>
          val (tree, text) = walkLevel(r, AnsiString.zero, printNode)
          text +: walk(tree, depth + 1)
        case s: Star =>
          List(AnsiString("-->*<-- "), AnsiString("\\-/."))
      }
    }

    val console = AnsiString("||| ") +: walk(tree, 0)

    print(ANSI_CLEAR)
    console.reverse.foreach({l =>
      val numSpaces = 30 - (l.realLength / 2)
      val padding = " " * numSpaces
      print(padding)
      println(l)
    })
  }
}