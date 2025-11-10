/**
 * An automatic player that places its mark in the first empty cell found.
 * The player scans the board row by row, column by column, and places the mark
 * in the first BLANK cell encountered.
 */
public class NaivePlayer implements Player {

    /**
     * Constructs a new Naive player.
     */
    public NaivePlayer() {
    }

    /**
     * Places the mark in the first empty cell found, scanning row by row,
     * column by column from top-left to bottom-right.
     *
     * @param board the board on which to play
     * @param mark  the player's mark (X or O)
     */
    @Override
    public void playTurn(Board board, Mark mark) {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.getMark(row, col) == Mark.BLANK) {
                    board.putMark(mark, row, col);
                    return;
                }
            }
        }
    }
}
