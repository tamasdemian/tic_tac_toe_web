# TIC TAC TOE Online

[Gameplay](https://en.wikipedia.org/wiki/Tic-tac-toe)

## Requirements
 - Implement the standard Tic Tac Toe game
   - Fill cells with mouseclick
   - Switch players after each click
   - Check winner after each click
   - Alternate starting player
   - Basic controls: Clear results, set player names, restart game
 - Store usernames and results in cookies
 - Popup at the end of the game
 - Sections: Game Panel, Players, ScoreBoard, History

## Run

```bash
cd deployment
python3 -m http.server 8080 # or any other web servier
```

Then wisit `localhost:8080` in your browser.

## Update page

Make modifications in `render()` then execute:

```bash
sbt fastLinkJS
cp target/scala-3.3.6/scala-tictactoe-fastopt/main.js deployment/
```