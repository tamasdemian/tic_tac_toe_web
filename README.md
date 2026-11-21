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
 - UI Sections: Game Panel, Players, ScoreBoard, History
 
 See also:
  - `GameLogicSuite.scala` for unit tests of game logic
  - `game.spec.ts` for an E2E test of the generated static page

## Run

#### Web Page (local)
```bash
cd deployment
python3 -m http.server 8080 # or any other web servier
```

Then wisit `localhost:8080` in your browser.

#### Web Page (deployed)

Visit the corresponding [github page](https://tamasdemian.github.io/tic_tac_toe_web/).

#### CLI

```bash
scala run src/main/scala/CLI_version.scala src/main/scala/GameLogic.scala \
  --workspace target/scala-cli \
  --scala 3.3.6
```

## Generate webpage

After modifying `Main.scala` (or game logic) you should regenerate the corresponding JS file:

```bash
sbt fastLinkJS # fullLinkJS in prod
cp -f target/scala-3.3.6/scala-tictactoe-fastopt/main.js deployment/
```

## Local Tests
#### Game Logic (Scala)

```bash
sbt test
```

#### WebPage

End-to-end test covering webpage test using NodeJS.

Install dependencies if necessary:
```bash
npm install
npm install --save-dev @types/node
nvm install 22 # install nodejs v22
nvm use 22
npx playwright install chromium # WARNING! Requires ~330MB extra space!
```

Generate webpage and run the test:

```bash
sbt fastLinkJS
cp -f target/scala-3.3.6/scala-tictactoe-fastopt/main.js deployment/
npm run test:e2e # finally, run the test
```

## CI Tests

See `.github/workflows/deploy.yml`.

Warning: 
 - `fastLinkJS` is used for page generation
 - Expensive PlayWright tests are turned off in CI.