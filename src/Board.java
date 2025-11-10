/**
 * Represents an n-by-n Tic-Tac-Toe board and provides operations
 * for querying and placing marks on the board.
 * 
 * Indices are 0-based. The default board size is 4x4.
 */
public class Board {

    /** Default board dimension (creates a 4 x 4 board). */
    private static final int DEFAULT_SIZE = 4;

    /** Board dimension n (the board is n x n). */
    private final int size;

    /** Grid storing marks, indices start at 0. */
    private final Mark[][] cells;

    /**
     * Constructs a 4 x 4 board with all cells set to BLANK.
     */
    public Board() {
        this(DEFAULT_SIZE);
    }

    /**
     * Constructs a size x size board with all cells set to BLANK.
     *
     * @param size the board dimension (number of rows and columns)
     */
    public Board(int size) {
        this.size = size;
        this.cells = new Mark[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                this.cells[row][col] = Mark.BLANK;
            }
        }
    }


    /**
     * Returns the board dimension n. The board has n rows and n columns.
     *
     * @return the board size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Returns the mark at the given coordinates, or BLANK if the coordinates are
     * outside the board bounds.
     *
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     * @return the mark at (row, col), or BLANK if out of bounds
     */
    public Mark getMark(int row, int col) {
        if (!inBounds(row, col)) {
            return Mark.BLANK;
        }
        return this.cells[row][col];
    }

    /**
     * Attempts to place the given mark at (row, col).
     * The placement succeeds only if the coordinates are in bounds and the cell is BLANK.
     *
     * @param mark the mark to place
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     * @return true if the move is legal and the cell was updated; false otherwise
     */
    public boolean putMark(Mark mark, int row, int col) {
        if (!inBounds(row, col)) {
            return false;
        }
        if (this.cells[row][col] != Mark.BLANK) {
            return false;
        }
        this.cells[row][col] = mark;
        return true;
    }

    /**
     * Returns true if (row, col) is inside the board's bounds.
     */
    private boolean inBounds(int row, int col) {
        return row >= 0 && row < this.size && col >= 0 && col < this.size;
    }

}