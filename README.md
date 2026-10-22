# Connect Four

[![CI](https://github.com/NiharParikh17/Connect-Four/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/NiharParikh17/Connect-Four/actions/workflows/ci.yml)

A classic two-player **Connect Four** board game built in Java with a graphical user interface powered by **Java Swing**. The project demonstrates clean software design through the **MVC (Model-View-Controller)** architectural pattern combined with the **Observer** design pattern.

**See how it works:** <https://connectfour.nhparikh.com> follows one winning move through the View, Controller and Model, step by step.

---

## Table of Contents

- [About the Game](#about-the-game)
- [Tech Stack](#tech-stack)
- [Architecture & Design Patterns](#architecture--design-patterns)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [How to Run](#how-to-run)
- [How to Run Tests](#how-to-run-tests)
- [Code Style (Checkstyle)](#code-style-checkstyle)
- [CI/CD](#cicd)
- [Gameplay](#gameplay)
- [Reporting Issues](#reporting-issues)
- [License](#license)
- [Author](#author)

---

## About the Game

Connect Four is a two-player strategy game played on a **6-row × 7-column** vertical grid. Players take turns dropping colored chips into columns. A chip falls to the lowest available slot in the chosen column. The first player to align **four chips in a row** — horizontally, vertically, or diagonally — wins. If the board fills up without a winner, the game is declared a **draw**.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| **Java** (JDK 21) | Core programming language |
| **Java Swing** (`javax.swing`) | GUI framework for the game window |
| **Java AWT** (`java.awt`) | Layout management and color handling |
| **Lombok** | Boilerplate reduction (`@Getter`, `@AllArgsConstructor`) |
| **Gradle** | Build tool & dependency management |
| **JUnit Jupiter** | Unit testing framework |
| **Mockito** | Mocking framework for isolated unit tests |
| **Checkstyle** | Static analysis enforcing Google Java Style |
| **GitHub Actions** | Continuous integration, release automation, and website deployment |
| **GitHub Pages** | Hosts the project website |

---

## Architecture & Design Patterns

### MVC (Model-View-Controller)

| Layer | Class | Responsibility |
|---|---|---|
| **Model** | `ConnectFourModel` | Game state, board logic, win/draw detection |
| **View** | `ConnectFourView` | Swing GUI — renders the board and handles user input |
| **Controller** | `ConnectFourController` | Bridges user actions from the View to the Model |

The Model and Controller are accessed through their interfaces (`ConnectFourModelInterface`, `ConnectFourControllerInterface`), which keeps components loosely coupled and easy to swap or test. The View has no separate interface; the Controller holds the concrete `ConnectFourView`.

### Observer Pattern

Two observer interfaces decouple the Model from the View:

- **`ConnectFourObserver`** — notified after every chip drop to update the board visually.
- **`WinnerObserver`** — notified when the game ends (win or draw) to trigger an automatic reset.

---

## Project Structure

```
Connect-Four/
├── .github/
│   ├── ISSUE_TEMPLATE/                  # Bug report & feature request forms
│   ├── dependabot.yml                   # Monthly dependency & action updates
│   └── workflows/
│       ├── ci.yml                       # Build, test & Checkstyle on push / PR
│       ├── pages.yml                    # Publish web/ to GitHub Pages
│       └── release.yml                  # Publish a GitHub release on version tags
├── configuration/
│   └── checkstyle/
│       └── google_checks.xml            # Checkstyle ruleset
├── gradle/
│   └── wrapper/                         # Gradle wrapper JAR & properties
├── build.gradle                         # Build configuration
├── settings.gradle                      # Project name
├── gradlew / gradlew.bat                # Gradle wrapper scripts
├── web/                                 # Project website (index.html, styles.css, app.js)
└── src/
    ├── main/
    │   └── java/
    │       └── connectfour/
    │           ├── controller/
    │           │   ├── ConnectFourController.java           # Controller implementation
    │           │   └── ConnectFourControllerInterface.java  # Controller contract
    │           ├── model/
    │           │   ├── ConnectFourModel.java                # Game logic & state (Model)
    │           │   ├── ConnectFourModelInterface.java       # Model contract
    │           │   └── Player.java                          # Player data (name, id, color)
    │           ├── observer/
    │           │   ├── ConnectFourObserver.java             # Observer interface (per-turn)
    │           │   └── WinnerObserver.java                  # Observer interface (end-game)
    │           └── view/
    │               └── ConnectFourView.java                 # Swing GUI (View) + main entry point
    └── test/
        ├── java/
        │   └── connectfour/
        │       ├── controller/
        │       │   └── ConnectFourControllerTest.java       # Controller unit tests
        │       ├── model/
        │       │   └── ConnectFourModelTest.java            # Model unit tests
        │       └── view/
        │           └── ConnectFourViewTest.java             # View unit tests
        └── resources/
            └── mockito-extensions/
                └── org.mockito.plugins.MockMaker            # Enables Mockito's inline mock maker
```

---

## Prerequisites

- **JDK 21 or newer.** The project compiles to Java 21 bytecode.
- **No Gradle installation needed.** The included Gradle wrapper (`gradlew`) downloads the Gradle version pinned in `gradle/wrapper/gradle-wrapper.properties` on first use.
- **A graphical display.** Both the game and the `ConnectFourView` tests create Swing windows. On a headless Linux machine, wrap Gradle commands with `xvfb-run` (see [How to Run Tests](#how-to-run-tests)).

---

## How to Run

### Using an IDE

1. **Clone the repository:**
   ```bash
   git clone https://github.com/NiharParikh17/Connect-Four.git
   ```

2. **Open the project** in any Java IDE that supports Gradle (e.g., IntelliJ IDEA, Eclipse, VS Code). The IDE should automatically detect and import the Gradle build file.

3. **Run the application:**
   - Navigate to `src/main/java/connectfour/view/ConnectFourView.java`.
   - Run the `main` method directly from the IDE, or use the Gradle `run` task from the IDE's Gradle tool window.

### Using the Gradle Wrapper (Command Line)

On Windows, use `gradlew.bat` in place of `./gradlew`.

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

3. **Build a distribution archive** (`.tar` / `.zip` with launch scripts):
   ```bash
   ./gradlew assembleDist
   ```
   Archives are placed in `build/distributions/` (e.g., `Connect-Four-1.0.0.zip`). To get an unpacked copy instead, run `./gradlew installDist` and launch `build/install/Connect-Four/bin/Connect-Four`.

4. **Compile** only (no run):
   ```bash
   ./gradlew compileJava
   ```

5. **Full build** (compile, tests, Checkstyle, JAR, and distributions; the same command CI runs):
   ```bash
   ./gradlew clean build
   ```

---

## How to Run Tests

The project has a comprehensive unit test suite covering all three MVC layers.

| Test Class | Tests Cover |
|---|---|
| `ConnectFourModelTest` | Board initialization, chip placement, player alternation, observer registration / removal and notifications, horizontal / vertical / diagonal win detection, column-fill disabling, and reset |
| `ConnectFourControllerTest` | Correct delegation order to Model and View for `dropChip`, `reset`, and `exit` |
| `ConnectFourViewTest` | Observer registration, slot painting, button enabling/disabling, `updateGame` & `updateWinner` callbacks, and all button `ActionEvent` routes |

Run the full test suite with:
```bash
./gradlew test
```

On a headless Linux machine (no display), run the tests under a virtual display:
```bash
xvfb-run --auto-servernum ./gradlew test
```

An HTML report is generated at `build/reports/tests/test/index.html`.

> **Implementation notes:**
> - Tests use **JUnit Jupiter** with `@ParameterizedTest` and `@ValueSource` for exhaustive column coverage.
> - **Mockito** mocks are injected via `@ExtendWith(MockitoExtension.class)` to isolate each layer.
> - `JOptionPane` static calls are intercepted with `MockedStatic` so win tests never block on a dialog.
> - All Swing assertions run on the Event Dispatch Thread via `SwingUtilities.invokeAndWait`.

---

## Code Style (Checkstyle)

The project enforces **Google Java Style** through Checkstyle. The ruleset is located at `configuration/checkstyle/google_checks.xml`.

Run style checks manually:
```bash
# Check main sources
./gradlew checkstyleMain

# Check test sources
./gradlew checkstyleTest
```

Both checks also run as part of `./gradlew check` and `./gradlew build`. HTML and XML reports are generated under `build/reports/checkstyle/`. The build fails on any violation (`ignoreFailures = false`, `maxWarnings = 0`).

---

## CI/CD

The project uses **GitHub Actions** for continuous integration, releases, and the project website.

### Continuous Integration (`.github/workflows/ci.yml`)

Runs on every push and pull request to `main`, and can be started manually from the **Actions** tab. The workflow:

1. Validates the Gradle wrapper JAR checksum.
2. Sets up JDK 21 (Temurin) with Gradle dependency caching.
3. Runs `./gradlew clean build` under Xvfb, so compilation, all unit tests, and Checkstyle must pass.
4. Uploads the test and Checkstyle reports (even when the build fails) and the built JAR and distributions as workflow artifacts.

### Releases (`.github/workflows/release.yml`)

Pushing a tag that starts with `v` builds and tests the project, then creates a GitHub release with generated notes and attaches `connect-four.jar` plus the `.zip` and `.tar` distributions. To publish a release:

1. Update `version` in `build.gradle` (e.g., `1.1.0`) and merge the change to `main`.
2. Tag the commit with the matching version and push the tag:
   ```bash
   git tag v1.1.0
   ```
   ```bash
   git push origin v1.1.0
   ```

The workflow fails if the tag does not match the version in `build.gradle`.

### Website (`.github/workflows/pages.yml`)

The explainer at <https://connectfour.nhparikh.com> is a static page in `web/`, with no build step. When a push to `main` changes `web/`, the game's sources, or the build files, the workflow builds `connect-four.jar` from the same commit and publishes it to GitHub Pages alongside the page, so the site's download link always serves the latest game. It can also be run manually from the **Actions** tab.

To preview the site locally, serve the `web/` folder with any static file server, for example:
```bash
python3 -m http.server --directory web
```

### Dependency Updates (`.github/dependabot.yml`)

Dependabot checks monthly for new Gradle dependency and GitHub Actions versions, grouping each into a single pull request that CI verifies.

---

## Gameplay

| Control | Action |
|---|---|
| **Drop** button (above each column) | Drop a chip into that column |
| **Reset** button | Clear the board and start a new game |
| **Exit** button | Close the application |

- **Player 1** plays with 🔴 **Red** chips and always starts a new game.  
- **Player 2** plays with 🟡 **Yellow** chips.  
- The current player's turn is displayed at the top of the window.  
- A column's **Drop** button is automatically disabled when the column is full.  
- A dialog box announces the winner or a draw, and the board resets automatically.  
- Closing the game window also exits the application.

---

## Reporting Issues

Found a bug or have an idea? [Open an issue](https://github.com/NiharParikh17/Connect-Four/issues/new/choose) and pick a form:

- **Bug report** — for gameplay problems, list the columns played in order (counting from 0 on the left) so the game can be replayed exactly. Include your Java version and operating system.
- **Feature request** — describe the problem first, then the change you would like.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Author

**Nihar Parikh**
