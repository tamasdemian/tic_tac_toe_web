import org.scalajs.dom
import org.scalajs.dom.{document, HTMLElement}
import scala.scalajs.js
import ujson.*

object Main:
  private case class Round(winner: Option[Int])
  private val CookieName = "scala_ttt"
  private val CookieDays = 365
  private val root = document.getElementById("app").asInstanceOf[HTMLElement]

  private var names = Vector("Player 1", "Player 2")
  private var rounds = Vector.empty[Round]
  private var board = Array.fill[Option[Int]](9)(None)
  private var current = 0
  private var finished = false
  private var notice = "Your turn"
  private var popupVisible = false

  def main(args: Array[String]): Unit =
    loadCookie()
    render()
    //root.addEventListener("click", (event: dom.Event) => handleClick(event))
    // To prevent rendering when input field is clicked
    root.addEventListener("click", (event: dom.Event) =>
      val target = event.target.asInstanceOf[dom.Element]
      if target.closest("[data-cell]") != null ||
        target.closest("[data-action]") != null then
        handleClick(event)
    )
  
  

  private def loadCookie(): Unit =
    document.cookie.split(";").iterator.map(_.trim)
      .find(_.startsWith(CookieName + "="))
      .map(_.substring(CookieName.length + 1)).foreach { encoded =>
        try
          val parsed = ujson.read(js.URIUtils.decodeURIComponent(encoded))
          val loadedNames = parsed("names").arr.map(_.str).toVector
          val loadedRounds = parsed("rounds").arr.map { item =>
            val winner = item("winner").num.toInt
            if winner == 0 || winner == 1 then Round(Some(winner)) else Round(None)
          }.toVector
          if loadedNames.size == 2 then names = loadedNames
          rounds = loadedRounds
        catch
          case _: Throwable => ()
      }

  private def saveCookie(): Unit =
    val json = Obj(
      "names" -> Arr.from(names.map(Str(_))),
      "rounds" -> Arr.from(rounds.map(r =>
        Obj("winner" -> Num(r.winner.getOrElse(-1)))
      ))
    )
    val value = js.URIUtils.encodeURIComponent(ujson.write(json))
    document.cookie =
      s"$CookieName=$value; Max-Age=${CookieDays * 86400}; Path=/; SameSite=Lax"

  private def handleClick(event: dom.Event): Unit =
    val target = event.target.asInstanceOf[dom.Element]
    val cellButton = target.closest("[data-cell]")
    if cellButton != null then
      play(cellButton.getAttribute("data-cell").toInt)
    else
      val actionButton = target.closest("[data-action]")
      if actionButton != null then
        actionButton.getAttribute("data-action") match
          case "play-again" => newRound()
          case "clear-results" => clearResults()
          case "save-names" => updateNames()
          case "dismiss-popup" => popupVisible = false
          case _ => ()
    render()

  private def play(index: Int): Unit =
    if !finished && index >= 0 && index < 9 && board(index).isEmpty then
      board(index) = Some(current)
      winnerOf(board) match
        case Some(winner) =>
          rounds :+= Round(Some(winner))
          finished = true
          notice = s"${names(winner)} wins this round!"
          saveCookie()
          popupVisible = true
        case None if board.forall(_.nonEmpty) =>
          rounds :+= Round(None)
          finished = true
          notice = "It's a draw!"
          saveCookie()
          popupVisible = true
        case _ =>
          current = 1 - current
          notice = s"${names(current)}'s turn"

  private def winnerOf(cells: Array[Option[Int]]): Option[Int] =
    val lines = Seq(
      (0, 1, 2), (3, 4, 5), (6, 7, 8),
      (0, 3, 6), (1, 4, 7), (2, 5, 8),
      (0, 4, 8), (2, 4, 6)
    )
    lines.collectFirst {
      case (a, b, c)
          if cells(a).nonEmpty && cells(a) == cells(b) && cells(b) == cells(c) =>
        cells(a).get
    }

  private def newRound(): Unit =
    popupVisible = false
    board = Array.fill[Option[Int]](9)(None)
    current = rounds.size % 2
    finished = false
    notice = s"${names(current)} starts"

  private def clearResults(): Unit =
    rounds = Vector.empty
    newRound()
    saveCookie()

  private def updateNames(): Unit =
    val first = document.getElementById("name-0").asInstanceOf[dom.HTMLInputElement].value.trim
    val second = document.getElementById("name-1").asInstanceOf[dom.HTMLInputElement].value.trim
    if first.nonEmpty && second.nonEmpty then
      names = Vector(first.take(24), second.take(24))
      rounds = Vector.empty
      newRound()
      saveCookie()
    else
      notice = "Please enter both player names."

  private def escape(value: String): String =
    value.replace("&", "&amp;").replace("<", "&lt;")
      .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")

  private def score(player: Int): Int = rounds.count(_.winner.contains(player))
  private def draws: Int = rounds.count(_.winner.isEmpty)

  private def render(): Unit =
    val cells = board.zipWithIndex.map { case (value, i) =>
      val mark = value.map(v => if v == 0 then "X" else "O").getOrElse("")
      val markClass = if mark == "X" then "mark-x" else if mark == "O" then "mark-o" else ""
      val disabled = if value.nonEmpty || finished then "disabled" else ""
      s"""<button class="cell $markClass" data-cell="$i" aria-label="Square ${i + 1}" $disabled>$mark</button>"""
    }.mkString

    val history = if rounds.isEmpty then
      """<p class="empty-history">No completed rounds yet. Let's play!</p>"""
    else
      rounds.zipWithIndex.reverse.map { case (round, index) =>
        val result = round.winner match
          case Some(w) => s"<strong>${escape(names(w))}</strong> won"
          case None => "Draw"
        s"""<li><span>Round ${index + 1}</span><span>$result</span></li>"""
      }.mkString
    
    val popup =
      if popupVisible then
        s"""
          <div class="modal-backdrop">
            <section class="modal" role="dialog" aria-modal="true">
              <p class="eyebrow">ROUND COMPLETE</p>
              <h2>${escape(notice)}</h2>
              <p>Ready for another round?</p>
              <div class="game-actions">
                <button class="button button-primary"
                        data-action="play-again">
                  Play again ↗
                </button>
                <button class="button button-quiet"
                        data-action="dismiss-popup">
                  Close
                </button>
              </div>
            </section>
          </div>
        """
      else ""

    root.innerHTML =
      s"""
      <section class="hero">
        <h1>TIC<br><span>TAC</span><br>TOE</h1>
      </section>
      <section class="name-panel panel">
        <div class="section-heading"><div><h2>PLAYERS</h2></div>
          <span class="tiny-note">Editing names resets results</span></div>
        <div class="player-inputs">
          <label class="player-field"><span class="player-symbol x-symbol">X</span><input id="name-0" maxlength="24" value="${escape(names(0))}" aria-label="Player X name"></label>
          <label class="player-field"><span class="player-symbol o-symbol">O</span><input id="name-1" maxlength="24" value="${escape(names(1))}" aria-label="Player O name"></label>
          <button class="button button-dark save-button" data-action="save-names">Save players</button>
        </div>
      </section>
      <div class="game-layout">
        <section class="game-panel panel">
          <div class="game-heading"><div><h2>ROUND ${rounds.size + 1}</h2></div>
            <span class="turn-pill">${if finished then "ROUND COMPLETE" else "● LIVE ROUND"}</span></div>
          <div class="status-line">${escape(notice)}</div>
          <div class="board" role="group" aria-label="Tic-tac-toe board">${cells}</div>
          <div class="game-actions">
            <button class="button button-primary" data-action="play-again">${if finished then "Play again ↗" else "Restart round ↗"}</button>
            <button class="button button-quiet" data-action="clear-results">Clear results</button>
          </div>
        </section>
        <aside class="side-column">
          <section class="score-panel panel">
            <h2>SCOREBOARD</h2>
            <div class="score-row"><span class="score-mark x-symbol">X</span><span class="score-name">${escape(names(0))}</span><strong>${score(0)}</strong></div>
            <div class="score-row"><span class="score-mark o-symbol">O</span><span class="score-name">${escape(names(1))}</span><strong>${score(1)}</strong></div>
            <div class="draw-row"><span>Draws</span><strong>$draws</strong></div>
            <div class="total-row"><span>Rounds played</span><strong>${rounds.size}</strong></div>
          </section>
          <section class="history-panel panel">
            <h2>MATCH HISTORY</h2>
            <ul class="history-list">$history</ul>
          </section>
        </aside>
      </div>
      <footer><span>Tamás Demián, 2026-11-19</span><span><a href="https://github.com/tamasdemian/tic_tac_toe_web">source code</a></span></footer>
      ${popup}
      """
