/**
 * A human-controlled player that reads its moves from standard input.
 * Input is read via {@code KeyboardInput.readInt()} when {@link #playTurn(Board, Mark)} is called.
 */
public class HumanPlayer implements Player {

    /**
     * Constructs a new human player.
     */
    public HumanPlayer() {
    }

    @Override
    /**
     * Performs a human player's move.
     * Prints an initial prompt based on the mark, reads a two-digit number using
     * {@code KeyboardInput.readInt()} where the tens digit is the row and the
     * ones digit is the column. Validates bounds and occupancy, and places the mark.
     * On invalid input, prints the error message and retries.
     *
     * @param board the board on which to play
     * @param mark the player's mark (X or O)
     */
    public void playTurn (Board board, Mark mark) {
        if (mark == Mark.X) {
            System.out.println("Player X, type coordinates: ");
        } else {
            System.out.println("Player O, type coordinates: ");
        }
        int size = board.getSize();
        while (true) {
            int coordinates = KeyboardInput.readInt();
            int row = coordinates / 10;
            int col = coordinates % 10;
            boolean inBounds = row >= 0 && row < size && col >= 0 && col < size;
            if (!inBounds) {
                System.out.print("Invalid mark position. Please choose a valid position: ");
                continue;
            }
            if (!board.putMark(mark, row, col)) {
                System.out.print(
                    "Mark position is already occupied. Please choose a valid position: ");
                continue;
            }
            return;
        }
    }

}

