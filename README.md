# 🎮 RETROES – A Java Retro Game Collection

RETROES is a classic-style game launcher and collection built with Java Swing. It features nostalgic 2D games like **WhatTheSnake**, **StreakTacToe**, and **Flapocalypse** — all accessible from a single user-friendly platform.

> ⚠️ This project is developed using **Java (Swing)** and follows a modular structure. All assets (images, fonts, sounds) are stored locally and are organized by game packages.



## 🧠 Features

- 🚪 **Login & Signup** system with user data stored in `.txt` files
- 🎮 **Game Hub** with clickable buttons to launch individual games
- 🐍 **WhatTheSnake** – A twist on the classic snake game with pause/resume, growing snake, and score tracking
- ❌ **StreakTacToe** – A customizable Tic-Tac-Toe game
- 🕊️ **Flapocalypse** – A fast-paced side-scrolling tap game
- 🎨 **Custom UI** with warm color palettes and pixel-style fonts
- 🗂️ Asset management through organized `assets/` folder
- 📏 Fixed game resolution: **1280x720p**


## 🗂️ Directory Structure

```
RETROES/
│
├── assets/                  # All global assets (fonts, images, etc.)
│
├── src/
│   ├── main/
│   │   ├── Main.java        # App launcher
│   │   ├── HomePage.java    # Game launcher GUI
│   │
│   └── games/
│       ├── whatTheSnake/    # WhatTheSnake game logic & UI
│       ├── streakTacToe/    # StreakTacToe game logic & UI
│       └── flapocalypse/    # Flapocalypse game logic & UI
│
├── users.txt                # Stores user credentials
└── README.md
```

## 🚀 How to Run

### ✅ Requirements:
- Java JDK 8 or above
- An IDE like IntelliJ IDEA or Eclipse

### 🧭 Steps:
1. **Clone the repository**  
   ```bash
   git clone https://github.com/zZOKofficial/RETROES.git

2.	Open in IntelliJ or any IDE
Import the src/ directory as your source root.
3.	Run Main.java
This will open the login screen. From there, sign up or log in to access the game hub.



### 🛠️ Customization
- Assets: Replace images in the assets/ folder to re-theme the games.
- Fonts: Add or change fonts by placing them in assets/fonts/ and loading them in the code.
- Games: Add your own games by creating a new package in games/, then integrate it with the homepage.

---

### 💡 Planned Features
- 🔒 Encrypted user data
- 🏆 Leaderboard with top scores
- 🖱️ Mouse-controlled UI in all games
- 🌐 Online multiplayer (experimental)

---

### 🤝 Contributors
- Md. Maruf Hossain (a.k.a. Zareef) – Core Developer, Game Designer, UI/UX Lead
- More contributors coming soon…

---

### 📜 License

This project is for educational and demo purposes.  
Contact **@zZOKofficial** if you’d like to collaborate, modify, or extend for production use.
