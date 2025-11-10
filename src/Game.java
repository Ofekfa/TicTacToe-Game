/**
 * Runs a single game on an n x n board with a given win-streak.
 * X plays first, then O, alternating turns until someone wins or the board is full.
 */
public class Game {

    /** Default number of consecutive marks required to win. */
    private static final int DEFAULT_WIN_STREAK = 3;

    /** The game board. */
    private final Board board;

    /** Number of consecutive marks required to win. */
    private final int winStreak;

    /** Renderer to display board. */
    private final Renderer renderer;

    /** Player assigned the X mark. */
    private final Player PlayerX;

    /** Player assigned the O mark. */
    private final Player PlayerO;

    /**
     * Constructs a new game with default board size and a default win-streak.
     * The default size is taken from {@code new Board().getSize()}.
     *
     * @param PlayerX the player for X
     * @param PlayerO the player for O
     * @param renderer the renderer to use
     */
    public Game (Player PlayerX, Player PlayerO, Renderer renderer) {
         this.PlayerX = PlayerX;
         this.PlayerO = PlayerO;
         this.renderer = renderer;
         this.board = new Board();
         this.winStreak = DEFAULT_WIN_STREAK;
    }

    /**
     * Constructs a new game with a specific board size and win-streak.
     * X plays first. After each turn, the board is rendered via the provided renderer.
     *
     * @param PlayerX the player for X
     * @param PlayerO the player for O
     * @param size the board size (n), creating an n-by-n board
     * @param winStreak the number of consecutive marks required to win
     * @param renderer the renderer to use
     */
    public Game (Player PlayerX, Player PlayerO, int size, int winStreak, Renderer renderer) {
        this.PlayerX = PlayerX;
        this.PlayerO = PlayerO;
        this.renderer = renderer;
        this.board = new Board(size);
        this.winStreak = winStreak;
    }
    
    /**
     * Returns the number of consecutive marks required to win this game.
     *
     * @return the win-streak length (k)
     */
    public int getWinStreak() {
        return this.winStreak;
    }

    /**
     * Returns the board size (n) for this game.
     *
     * @return the size of the board
     */
    public int getBoardSize() {
        return this.board.getSize();
    }

    /**
     * Runs a single game of Tic-Tac-Toe.
     * X plays first. After each turn, the board is rendered via the provided renderer.
     *
     * @return the mark of the winning player (X or O), or BLANK if the game ends in a draw
     */
    public Mark run() {
        Mark current = Mark.X;
        while (true) {
            if (current == Mark.X) {
                this.PlayerX.playTurn(this.board, Mark.X);
            } else {
                this.PlayerO.playTurn(this.board, Mark.O);
            }
            this.renderer.renderBoard(this.board);
            if (hasWon(current)) {
                return current;
            }
            if (isBoardFull()) {
                return Mark.BLANK;
            }
            if (current == Mark.X) {
                current = Mark.O;
            } else {
                current = Mark.X;
            }
        }
    }
    
    /**
     * Checks whether the board contains no BLANK cells.
     *
     * @return true if all cells are occupied; false otherwise
     */
    private boolean isBoardFull() {
        for (int row = 0; row < this.board.getSize(); row++) {
            for (int col = 0; col < this.board.getSize(); col++) {
                if (this.board.getMark(row, col) == Mark.BLANK) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Scans a straight line on the board starting at (startRow, startCol), advancing by
     * (dRow, dCol) each step, and checks for a consecutive run of at least {@code winStreak}
     * cells equal to {@code mark}.
     *
     * @param startRow starting row index
     * @param startCol starting column index
     * @param dRow row delta per step (0, 1, -1)
     * @param dCol column delta per step (0, 1, -1)
     * @param mark the mark to match along the line
     * @return true if a run of length {@code winStreak} (or longer) is found; false otherwise
     */
    private boolean checkLine(int startRow, int startCol, int dRow, int dCol, Mark mark) {
        int run = 0;
        int r = startRow, c = startCol;
        int n = this.board.getSize();
        while (r >= 0 && r < n && c >= 0 && c < n) {
            if (this.board.getMark(r, c) == mark) {
                run++;
                if (run >= this.winStreak) {
                    return true;
                }
            } else {
                run = 0;
            }
            r += dRow;
            c += dCol;
        }
        return false;
    }

    /**
     * Checks whether the given mark has a winning run on the board.
     * A win is a sequence of at least {@code winStreak} consecutive cells
     * equal to {@code mark}, in any row, column, or diagonal.
     *
     * @param mark the mark to check (X or O)
     * @return true if the mark has a winning sequence; false otherwise
     */
    private boolean hasWon(Mark mark) {
        int n = this.board.getSize();
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (this.board.getMark(row, col) == mark) {
                    if (checkLine(row, col, 0, 1, mark)
                            || checkLine(row, col, 1, 0, mark)
                            || checkLine(row, col, 1, 1, mark)
                            || checkLine(row, col, 1, -1, mark)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}