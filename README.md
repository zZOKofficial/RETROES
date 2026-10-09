# RETROES – A Java Retro Game Collection

**Version 1.00** · [Changelog](CHANGELOG.md)

RETROES is a classic-style game launcher and collection built with Java Swing. It features nostalgic 2D games — **WhatTheSnake**, **StreakTacToe**, and **Flapocalypse** — all accessible from a single hub with login, high scores, background music, and credits.

> All assets (images, fonts, sounds) are stored locally and organized by feature package. Fixed resolution: **1280x720**.

## Features

- **Login & Signup** with salted PBKDF2 password hashing (legacy plaintext entries are migrated on first successful login)
- **Game Hub** with clickable buttons to launch individual games
- **Background music** (Dragonscale) that loops for the whole session after login, with 2s fade-in on the Homepage, 2s fade-out on exit, and a mute toggle
- **WhatTheSnake** – Snake clone with pause/resume, growing snake, bonus food, and score tracking
- **StreakTacToe** – Progressive tic-tac-toe series (3x3 → 4x4 → 5x5) with persistent series wins
- **Flapocalypse** – Flappy Bird–style side-scroller with pause/resume
- **High Scores** screen backed by a shared text file
- **Credits** roll with custom pixel fonts

## Directory Structure

```
RETROES/
├── Main.java                     # Entry point → opens LoginFrame
├── app/AppVersion.java           # Single source of truth for the version string
├── login/LoginFrame.java         # Login screen + password hashing helpers
├── createAccount/CreateAccountFrame.java
├── homepage/Homepage.java        # Game hub
├── games/
│   ├── whatTheSnake/             # Snake game (JPanel, wrapped in a JFrame by Homepage)
│   ├── streakTacToe/             # Tic-tac-toe series
│   └── flapocalypse/             # Flappy Bird clone
├── highScores/HighScores.java    # High score viewer
├── credits/Credits.java          # Scrolling credits
├── data/users.txt                # Accounts: username,salt,hash (local, gitignored)
├── highScores/highscore.txt      # GameName: score lines (local, gitignored)
├── sounds/
│   ├── MusicPlayer.java          # Looping OGG playback with fade/mute
│   └── dragonscale.ogg           # Background track
├── com/jcraft/jorbis/VorbisBridge.java  # Correct LE PCM decoder for jorbis
├── lib/jorbis-0.0.17.jar         # Pure-Java Ogg Vorbis decoder
├── fonts/                        # 04b03.ttf, cinzeld.ttf, joystixmonospace.otf
├── homepage/assets/              # Shared background image
└── games/*/assets/               # Per-game sprites
```

## How to Run

### Requirements
- Java JDK 8 or above (project configured for JDK 23 in IntelliJ)
- IntelliJ IDEA, Eclipse, or a terminal
- `lib/jorbis-0.0.17.jar` (included) — required at runtime for background music

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/zZOKofficial/RETROES.git
   ```
2. Open in your IDE **with the working directory set to the project root** (relative asset/font/data paths depend on this).
3. Run `Main.java`.
4. Sign up or log in to access the game hub.

### Command line (from the project root)
```bash
# Windows PowerShell
$files = Get-ChildItem -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -cp "lib/jorbis-0.0.17.jar" -d out @files
java -cp "out;lib/jorbis-0.0.17.jar" Main

# Linux / macOS
javac -cp lib/jorbis-0.0.17.jar -d out $(find . -name "*.java")
java -cp out:lib/jorbis-0.0.17.jar Main
```

## Versioning

The app version lives in one place: [`app/AppVersion.java`](app/AppVersion.java). Window titles and the startup log line read from it. Release notes are in [CHANGELOG.md](CHANGELOG.md).

## Data Files

Runtime data is **not committed** (see `.gitignore`). Both files are created automatically on first run.

- `data/users.txt` – one account per line. New accounts use `username,saltBase64,hashBase64` (PBKDF2WithHmacSHA256, 120k iterations). Older two-field plaintext lines are upgraded automatically on login.
- `highScores/highscore.txt` – one line per game:
  - `Flapocalypse: <int>`
  - `WhatTheSnake: <int>`
  - `StreakTacToe: X=<int> | O=<int>` (cumulative series wins)

## Customization

- **Assets:** Replace PNGs under `homepage/assets/` and `games/*/assets/`.
- **Fonts:** Drop TTF/OTF files into `fonts/` and load them via `Font.createFont`.
- **Games:** Add a new package under `games/`, then wire a button in `homepage/Homepage.java`.

## Planned Features

- Online multiplayer (experimental)
- Mouse-controlled UI in all games
- Per-player high scores

## Contributors

- Md. Maruf Hossain (a.k.a. Zareef) – Core Developer, Game Designer, UI/UX Lead

## License

MIT – see [LICENSE](LICENSE). For collaboration or production use, contact **@zZOKofficial**.
