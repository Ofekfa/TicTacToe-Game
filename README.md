Strategies for Automatic Players:

WhateverPlayer: This player uses a random strategy. On each turn, it randomly selects an empty cell on the
board until it finds a valid position to place its mark. The player continues trying random coordinates
until board.putMark() succeeds.

NaivePlayer: This player scans the board row by row, column by column, from top-left to bottom-right.
It places its mark in the first empty cell (BLANK) it encounters.

SmartPlayer: This player uses an intelligent multi-level strategy with the following priority:
1. Win now: If the player can win in the current move, it takes that winning move.
2. Block opponent: If the opponent can win on their next turn, the player blocks that winning move.
3. Create threats: The player extends existing sequences of its marks to create threats (positions that
 would create consecutive marks, forcing the opponent to block). This is done by finding the position
 that maximizes the length of consecutive marks in any direction.
4. Take center: If the center of the board is available, the player takes it
 (center is strategically valuable).
5. Take corners: If any corner is available, the player takes an empty corner.
6. Fallback: If none of the above are available, the player takes the first available cell
 (same as NaivePlayer).

Advantage of Shared Player Interface:

The advantage of designing all player classes to implement a shared Player interface is that it enables
polymorphism and follows the Open/Closed Principle. The Game and Tournament classes can work with any
Player implementation without needing to know the specific type of player. This makes the code:

1. Flexible: New player types can be added without modifying existing code (Open/Closed Principle).
2. Maintainable: Changes to player implementations don't affect the Game or Tournament classes.
3. Testable: Easy to test different player strategies by swapping implementations.

The design is based on these OOP pillars:
- Polymorphism: Different player implementations can be used interchangeably through the Player interface.
- Encapsulation: Each player encapsulates its own strategy logic internally.
- Abstraction: The Player interface abstracts away the details of how each player makes moves.
