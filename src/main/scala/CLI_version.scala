// scala run src/main/scala/TTT.scala 
object TicTacToeCLI:  
  def playerSymbol(idx: Int): Char = if (idx == 0) 'O' else 'X'

  def printBoard(board: Board): Unit =
    for row <- 0 until 3 do
      val cells = (0 until 3).map { col =>
        board(row * 3 + col).map(playerSymbol).getOrElse(' ')
      }
      println(s" ${cells.mkString(" | ")} ")
      if row < 2 then println("---+---+---")

  def main(args: Array[String]): Unit =
    var board = GameLogic.Empty
    var player = 1
    var finished = false
    println("Tic-Tac-Toe!")
    println("Enter positions from 1 to 9.")
    while !finished do
      printBoard(board)
      println(s"\nPlayer ${playerSymbol(player)}, choose a position:")
      val input = scala.io.StdIn.readLine()
      val index = input.toIntOption.filter(p => p >= 1 && p <= 9)
      index match
        case None =>
          println("Invalid input. Enter a number from 1 to 9.")
        case Some(p) if board(p - 1).isDefined =>
          println("Position already occupied!")
        case Some(p) =>
          board = board.updated(p-1, Some(player))
          GameLogic.winnerOf(board) match
            case Some(w) =>
              printBoard(board)
              println(s"\nPlayer ${playerSymbol(w)} wins!")
              finished = true
            case None if GameLogic.isDraw(board.toVector)  =>
              printBoard(board)
              println("\nIt's a draw!")
              finished = true
            case None =>
              player = GameLogic.nextPlayer(player)