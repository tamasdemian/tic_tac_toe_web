type Player = Int // 0 or 1
type Board = IndexedSeq[Option[Player]] // immutable
type History = Iterable[Option[Player]]

object GameLogic:
  val Empty: Board = Array.fill[Option[Player]](9)(None)

  private val winningLines = Vector(
    (0, 1, 2), (3, 4, 5), (6, 7, 8), // rows
    (0, 3, 6), (1, 4, 7), (2, 5, 8), // columns
    (0, 4, 8), (2, 4, 6)             // diagonals
  )

  def winnerOf(cells: Board): Option[Player] =
    require(cells.size == 9, "A Tic-Tac-Toe board must contain exactly 9 cells")
    winningLines.collectFirst {
      case (a, b, c)
          if cells(a).nonEmpty &&
             cells(a) == cells(b) &&
             cells(b) == cells(c) =>
        cells(a).get
    }

  def isDraw(cells: Board): Boolean =
    cells.forall(_.nonEmpty) && winnerOf(cells).isEmpty

  def canPlay(
    cells: Board,
    finished: Boolean,
    index: Int
  ): Boolean =
    !finished &&
    index >= 0 &&
    index < cells.size &&
    cells(index).isEmpty

  def nextPlayer(current: Player): Player =
    1 - current

  def nextStarter(completedRounds: Int): Player =
    completedRounds % 2

  def score(roundWinners: History, player: Player): Int =
    roundWinners.count(_.contains(player))

  def drawCount(roundWinners: History): Int =
    roundWinners.count(_.isEmpty)
