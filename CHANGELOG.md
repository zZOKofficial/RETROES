# Changelog

All notable changes to RETROES are documented here.
Versioning: `MAJOR.MINOR` (currently `1.00`).

## [1.00] – 2026-10-09

First feature-complete release of the hub + three games.

### Added
- **Windows installer** – `build-installer.ps1` produces `dist/RETROES-1.00.exe` (jpackage + WiX, per-user install, trimmed ~40 MB runtime; no Java required on the target machine)
- **Background music** – Dragonscale loops for the whole session after login; 2s fade-in on the Homepage, 2s fade-out on exit, Music ON/OFF toggle
- **Password hashing** – PBKDF2WithHmacSHA256 (16-byte salt, 120k iterations); legacy plaintext accounts migrate on first successful login
- **Username flow** – logged-in username preserved across every screen (games, high scores, credits); working Homepage back buttons everywhere
- **StreakTacToe series scores** – cumulative `X=n | O=m` wins persisted to the high-score file
- **App version constant** – `app/AppVersion.java` (`1.00`); shown in Login/Homepage titles and the startup log
- **Portable paths** – `app/AppPaths.java` resolves assets from the install directory and saves (`users.txt`, `highscore.txt`) to `%APPDATA%\RETROES\`, so installs work without admin rights

### Fixed
- **WhatTheSnake unreachable** – was launched as a bare JPanel (clicking it closed the hub and showed nothing); now wrapped in a proper JFrame
- **WhatTheSnake layout** – panel uses absolute layout so the Start button positions correctly; button hides during play and reappears on game over
- **Flapocalypse high score** – file/directory created if missing; score line appended when absent (previously threw FileNotFoundException or silently dropped the score)
- **Flapocalypse back button** – returned to a hardcoded "Player" instead of the real username
- **StreakTacToe exit behavior** – `EXIT_ON_CLOSE` killed the whole app; now `DISPOSE_ON_CLOSE` with a Homepage button
- **Font path case** – `fonts/04B03.ttf` → `fonts/04b03.ttf` (broke silently on Linux/macOS)
- **Audio noise/crackling** – jorbis's built-in `read()` mangles channel indexes on this platform; replaced with a correct little-endian PCM packer (`VorbisBridge`)
- **Register/login inconsistencies** – passwords no longer trimmed; duplicate-check trims username like login; commas rejected in usernames

### Changed
- **README** rewritten to match the real layout, run steps, and data formats
- **.gitignore** now covers build output (`out/`, `build/`, `dist/`), IntelliJ files, OS junk, and runtime data (`data/users.txt`, `highScores/highscore.txt`)
- Removed unused `fonts/cinzel.ttf`, dead `HighScores.saveScore`, unused game fields, and stale per-game `main()` methods
- All file access goes through `AppPaths` instead of hardcoded relative paths (fixes assets not loading when launched from a shortcut)

### Dependencies
- Added `lib/jorbis-0.0.17.jar` (pure-Java Ogg Vorbis decoder, ~97 KB) for background music

---

## [0.x] – pre-release

Initial prototype: login/signup (plaintext), game hub, Flapocalypse, WhatTheSnake, StreakTacToe, high-score viewer, credits. No versioned releases before 1.00.
