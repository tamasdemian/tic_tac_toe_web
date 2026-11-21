import munit.FunSuite

class GameLogicSuite extends FunSuite:
  private val winningLines = Vector(
    Vector(0, 1, 2), Vector(3, 4, 5), Vector(6, 7, 8),
    Vector(0, 3, 6), Vector(1, 4, 7), Vector(2, 5, 8),
    Vector(0, 4, 8), Vector(2, 4, 6)
  )

  private def boardWithWin(line: Vector[Int], player: Int): Vector[Option[Int]] =
    val cells = Array.fill[Option[Int]](9)(None)
    line.foreach(index => cells(index) = Some(player))
    cells.toVector

  winningLines.zipWithIndex.foreach { case (line, lineIndex) =>
    test(s"detects X winning on winning line ${lineIndex + 1}") {
      assertEquals(GameLogic.winnerOf(boardWithWin(line, 0)), Some(0))
    }

    test(s"detects O winning on winning line ${lineIndex + 1}") {
      assertEquals(GameLogic.winnerOf(boardWithWin(line, 1)), Some(1))
    }
  }

  test("does not report a winner on an empty board") {
    val board = Vector.fill[Option[Int]](9)(None)
    assertEquals(GameLogic.winnerOf(board), None)
  }

  test("does not report a winner for an incomplete line") {
    val board = Vector(
      Some(0), Some(0), None,
      Some(1), None, Some(1),
      None, None, None
    )
    assertEquals(GameLogic.winnerOf(board), None)
  }

  test("does not report a winner for a mixed line") {
    val board = Vector(
      Some(0), Some(1), Some(0),
      None, None, None,
      None, None, None
    )
    assertEquals(GameLogic.winnerOf(board), None)
  }

  test("recognizes a full board without a winner as a draw") {
    val board = Vector(
      Some(0), Some(1), Some(0),
      Some(0), Some(1), Some(1),
      Some(1), Some(0), Some(0)
    )
    assertEquals(GameLogic.winnerOf(board), None)
    assert(GameLogic.isDraw(board))
  }

  test("an incomplete board is not a draw") {
    val board = Vector.fill[Option[Int]](9)(None)
    assert(!GameLogic.isDraw(board))
  }

  test("a full board with a winner is not a draw") {
    val board = Vector(
      Some(0), Some(0), Some(0),
      Some(1), Some(1), Some(0),
      Some(1), Some(0), Some(1)
    )
    assertEquals(GameLogic.winnerOf(board), Some(0))
    assert(!GameLogic.isDraw(board))
  }

  test("allows a move into an empty cell") {
    val board = Vector.fill[Option[Int]](9)(None)
    assert(GameLogic.canPlay(board, finished = false, index = 4))
  }

  test("rejects a move into an occupied cell") {
    val board = Vector(Some(0)) ++ Vector.fill[Option[Int]](8)(None)
    assert(!GameLogic.canPlay(board, finished = false, index = 0))
  }

  test("rejects a move after the round has finished") {
    val board = Vector.fill[Option[Int]](9)(None)
    assert(!GameLogic.canPlay(board, finished = true, index = 4))
  }

  test("rejects indexes below zero or outside the board") {
    val board = Vector.fill[Option[Int]](9)(None)
    assert(!GameLogic.canPlay(board, finished = false, index = -1))
    assert(!GameLogic.canPlay(board, finished = false, index = 9))
  }

  test("alternates between X and O") {
    assertEquals(GameLogic.nextPlayer(0), 1)
    assertEquals(GameLogic.nextPlayer(1), 0)
  }

  test("alternates the starting player between rounds") {
    assertEquals(GameLogic.nextStarter(0), 0)
    assertEquals(GameLogic.nextStarter(1), 1)
    assertEquals(GameLogic.nextStarter(2), 0)
    assertEquals(GameLogic.nextStarter(3), 1)
  }

  test("counts wins for each player") {
    val winners = Vector(Some(0), Some(1), Some(0), None, Some(1))
    assertEquals(GameLogic.score(winners, 0), 2)
    assertEquals(GameLogic.score(winners, 1), 2)
  }

  test("counts draws") {
    val winners = Vector(Some(0), None, Some(1), None)
    assertEquals(GameLogic.drawCount(winners), 2)
  }

  test("requires a nine-cell board for winner detection") {
    intercept[IllegalArgumentException] {
      GameLogic.winnerOf(Vector(Some(0), Some(0), Some(0)))
    }
  }
