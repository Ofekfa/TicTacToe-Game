Tic‑Tac‑Toe Tournament Engine
==========================================


### Project Overview

This project implements a **generic Tic‑Tac‑Toe engine** that supports:

- **Configurable board size** (`n x n`, default `4 x 4`)
- **Configurable win streak** (default `k = 3`)
- **Multiple player types** (human and automatic)
- **Tournament mode** where players compete over multiple rounds and alternate between `X` and `O`

The focus of the implementation is on **clean object‑oriented design** and **extensibility**: new player
strategies and renderers can be added without modifying the core game engine.

---

### Architecture & Main Classes

- **`Board`**
  - Represents an `n x n` grid of `Mark` values (`X`, `O`, `BLANK`).
  - Responsible only for **state and validity**:
    - `getSize()`, `getMark(row, col)`, `putMark(mark, row, col)`.
  - Encapsulates the internal 2D array and enforces bounds & legality checks.

- **`Game`**
  - Runs a **single game** on a given board and win‑streak.
  - Alternates turns between two `Player` objects (`PlayerX`, `PlayerO`).
  - Uses a `Renderer` to display the board after each move.
  - Implements the game loop: win detection, draw detection and turn switching.

- **`Tournament`**
  - Runs a **multi‑round tournament** between two players.
  - Players alternate roles: in even rounds player1 is `X`, in odd rounds player2 is `X`.
  - Collects statistics: wins for each player and ties, and prints a summary at the end.
  - Uses `PlayerFactory` and `RendererFactory` to create the appropriate objects from command‑line arguments.

- **`Player` (interface)**
  - Common contract for all players:
    - `void playTurn(Board board, Mark mark)`
  - The game engine (`Game`, `Tournament`) depends only on this abstraction, not on concrete strategies.

- **Renderers**
  - `Renderer` interface (and concrete implementations such as `VoidRenderer`) decouple **game logic** from
    **presentation**.
  - Makes it easy to change how the board is displayed (e.g., console vs. GUI) without touching game logic.

---

### Player Strategies

The project includes several `Player` implementations, each encapsulating a different strategy:

- **`HumanPlayer`**
  - Reads moves from standard input.
  - Demonstrates how a human‑controlled player can plug into the same engine.

- **`WhateverPlayer`**
  - Uses a **purely random** strategy.
  - On each turn, repeatedly picks random coordinates until `board.putMark()` succeeds.
  - Good as a simple baseline for comparison.

- **`NaivePlayer`**
  - Very simple deterministic strategy.
  - Scans the board from **top‑left to bottom‑right** and plays in the **first empty cell** it finds.

- **`SmartPlayer`**
  - An intelligent strategy optimized for the default configuration (**4x4 board, win streak 3**).
  - Designed to win **at least 80% of the games** against `NaivePlayer` and `WhateverPlayer` in that setup.
  - Decision priority:
    1. **Win now** – if there is a move that immediately wins, play it.
    2. **Block opponent** – if the opponent can win next turn, block that move.
    3. **Create threats** – place a mark that creates a situation with `winStreak - 1` in a row, forcing the opponent to react.
    4. **Take corners** – if any corner is free, prefer it.
    5. **Fallback** – if nothing special is available, behave like `NaivePlayer` (first available cell).

Thanks to the shared `Player` interface, all of these strategies are **fully interchangeable** inside the
same game and tournament framework.

---

### Object‑Oriented Design Highlights

This project was intentionally designed to demonstrate **core OOP principles**:

- **Abstraction**
  - `Player`, `Renderer`, and `Board` expose clear public interfaces and hide implementation details.
  - The `Game` and `Tournament` classes interact only with abstractions, not with concrete implementations.

- **Encapsulation**
  - Each class is responsible for a single, well‑defined aspect:
    - `Board` manages the grid and move legality.
    - `Game` manages the lifecycle of a single match.
    - `Tournament` manages multiple games and statistics.
    - Each `Player` encapsulates its own decision‑making logic.
  - Internal state (like the `Mark[][]` array inside `Board`) is private and cannot be modified from the outside.

- **Polymorphism**
  - The use of the `Player` interface allows `Game` and `Tournament` to work with **any** player implementation
    (human, naive, random, smart, future strategies).
  - Renderers are also polymorphic via the `Renderer` interface.
  - This makes it trivial to add a new player class without changing existing code.

- **Open/Closed Principle**
  - New behaviors (e.g., `SmartPlayer`, new renderers) are added by creating **new classes**, not by modifying
    `Game` or `Tournament`.
  - `PlayerFactory` and `RendererFactory` map simple strings (like `"human"`, `"naive"`, `"smart"`) to concrete
    implementations, keeping the main flow clean and extensible.

The result is code that is **flexible**, **maintainable**, and **easy to test**, and that mirrors real‑world
object‑oriented design patterns.

---

### Running the Tournament

The entry point of the project is the `Tournament` class.

Command‑line format:

```bash
java Tournament [rounds] [size] [winStreak] [renderer] [player1] [player2]
```

Example – play 100 rounds on a `4x4` board with win‑streak `3`, using a quiet renderer and two automatic players:

```bash
java Tournament 100 4 3 void smart naive
```

- **`rounds`** – number of games in the tournament (e.g., `100`)
- **`size`** – board size `n` (for an `n x n` board)
- **`winStreak`** – number of consecutive marks needed to win
- **`renderer`** – rendering strategy (e.g., `void` for no output, or other renderers as implemented)
- **`player1` / `player2`** – player types (e.g., `human`, `naive`, `whatever`, `smart`)

After the tournament ends, a summary is printed with the number of wins for each player and the number of ties.

---

### Extending the Project

To add a new automatic strategy (for example, a minimax‑based AI):

1. Create a new class that implements the `Player` interface.
2. Implement the `playTurn(Board board, Mark mark)` method with your algorithm.
3. Register the new strategy in `PlayerFactory` so it can be selected via the command line.

No changes are required in `Game` or `Tournament`, thanks to the **polymorphic and modular OOP design**.
