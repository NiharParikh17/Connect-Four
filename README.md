# Connect Four

A classic two-player **Connect Four** board game built in Java with a graphical user interface powered by **Java Swing**. The project demonstrates clean software design through the **MVC (Model-View-Controller)** architectural pattern combined with the **Observer** design pattern.

---

## Table of Contents

- [About the Game](#about-the-game)
- [Tech Stack](#tech-stack)
- [Architecture & Design Patterns](#architecture--design-patterns)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [How to Run Tests](#how-to-run-tests)
- [Gameplay](#gameplay)
- [Author](#author)

---

## About the Game

Connect Four is a two-player strategy game played on a **6-row × 7-column** vertical grid. Players take turns dropping colored chips into columns. A chip falls to the lowest available slot in the chosen column. The first player to align **four chips in a row** — horizontally, vertically, or diagonally — wins. If the board fills up without a winner, the game is declared a **draw**.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| **Java** (JDK 11+) | Core programming language |
| **Java Swing** (`javax.swing`) | GUI framework for the game window |
| **Java AWT** (`java.awt`) | Layout management and color handling |
| **Gradle 9.4.1** | Build tool & dependency management |
| **IntelliJ IDEA** | Recommended IDE |

---

## Architecture & Design Patterns

### MVC (Model-View-Controller)

| Layer | Class | Responsibility |
|---|---|---|
| **Model** | `ConnectFourModel` | Game state, board logic, win/draw detection |
| **View** | `ConnectFourView` | Swing GUI — renders the board and handles user input |
| **Controller** | `ConnectFourController` | Bridges user actions from the View to the Model |

Each layer communicates through its corresponding interface (`ConnectFourModelInterface`, `ConnectFourControllerInterface`), which keeps components loosely coupled and easy to swap or test.

### Observer Pattern

Two observer interfaces decouple the Model from the View:

- **`ConnectFourObserver`** — notified after every chip drop to update the board visually.
- **`WinnerObserver`** — notified when the game ends (win or draw) to trigger an automatic reset.

---

## Project Structure

```
Connect-Four/
└── src/
    └── connectfour/
        ├── ConnectFourModel.java            # Game logic & state (Model)
        ├── ConnectFourModelInterface.java   # Model contract/interface
        ├── ConnectFourView.java             # Swing GUI (View) + main entry point
        ├── ConnectFourController.java       # Coordinator (Controller)
        ├── ConnectFourControllerInterface.java # Controller contract/interface
        ├── ConnectFourObserver.java         # Observer interface for game updates
        ├── WinnerObserver.java              # Observer interface for win/draw events
        └── Player.java                      # Player data (name, id, color)
```

---

## How to Run

### Using IntelliJ IDEA (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/<your-username>/Connect-Four.git
   ```

2. **Open the project** in IntelliJ IDEA (`File → Open → select the Connect-Four folder`). IntelliJ will automatically detect the Gradle build file.

3. **Run the application:**
   - Navigate to `src/connectfour/view/ConnectFourView.java`.
   - Click the green **▶ Run** button next to the `main` method, or right-click the file and select **Run 'ConnectFourView.main()'**.
   - Alternatively, use the Gradle tool window and run the `run` task.

### Using the Gradle Wrapper (Command Line)

1. **Run** the application directly:
   ```bash
   ./gradlew run
   ```

2. **Build** a standalone JAR:
   ```bash
   ./gradlew jar
   ```
   The JAR will be output to `build/libs/connect-four.jar`. Run it with:
   ```bash
   java -jar build/libs/connect-four.jar
   ```

3. **Compile** only (no run):
   ```bash
   ./gradlew compileJava
   ```

---

## How to Run Tests

The project currently does not include a dedicated test suite. To add unit tests:

1. Write test classes targeting `ConnectFourModel` for win-detection logic, board state management, and player switching.
2. Run tests with:
   ```bash
   ./gradlew test
   ```

---

## Gameplay

| Control | Action |
|---|---|
| **Drop** button (above each column) | Drop a chip into that column |
| **Reset** button | Clear the board and start a new game |
| **Exit** button | Close the application |

- **Player 1** plays with 🔴 **Red** chips.  
- **Player 2** plays with 🟡 **Yellow** chips.  
- The current player's turn is displayed at the top of the window.  
- A column's **Drop** button is automatically disabled when the column is full.  
- A dialog box announces the winner or a draw, and the board resets automatically.

---

## Author

**Nihar Parikh**
