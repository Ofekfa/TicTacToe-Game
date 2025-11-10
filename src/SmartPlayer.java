/**
 * An intelligent automatic player that uses strategic decision-making.
 * Strategy: Win now, block opponent, create threats, take center, take corners, then first available.
 */
public class SmartPlayer implements Player {

    /** Default win streak. */
    private static final int DEFAULT_WIN_STREAK = 3;

    /**
     * Constructs a new Smart player.
     */
    public SmartPlayer() {
    }

    /**
     * Makes an intelligent move using the strategy:
     * 1. Win if possible
     * 2. Block opponent if they can win
     * 3. Create a threat (position that would create winStreak-1 marks in a row)
     * 4. Take center if available
     * 5. Take any empty corner
     * 6. Fall back to first available cell
     *
     * @param board the board on which to play
     * @param mark the player's mark (X or O)
     */
    @Override
    public void playTurn(Board board, Mark mark) {
        int[] move;
        
        // 1. Win now if possible
        move = findWinningMove(board, mark);
        if (move != null) {
            board.putMark(mark, move[0], move[1]);
            return;
        }
        
        // 2. Block opponent if they can win
        Mark opponent = getOpponentMark(mark);
        move = findWinningMove(board, opponent);
        if (move != null) {
            board.putMark(mark, move[0], move[1]);
            return;
        }
        
        // 3. Create a threat
        move = findThreatMove(board, mark);
        if (move != null) {
            board.putMark(mark, move[0], move[1]);
            return;
        }
        
        // 4. Take center if available
        move = getCenter(board.getSize());
        if (move != null && board.getMark(move[0], move[1]) == Mark.BLANK) {
            board.putMark(mark, move[0], move[1]);
            return;
        }
        
        // 5. Take any empty corner
        move = findEmptyCorner(board);
        if (move != null) {
            board.putMark(mark, move[0], move[1]);
            return;
        }
        
        // 6. Fall back to first available (like NaivePlayer)
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.getMark(row, col) == Mark.BLANK) {
                    board.putMark(mark, row, col);
                    return;
                }
            }
        }
    }

    /**
     * Finds a winning move for the given mark, if one exists.
     * Checks each empty cell to see if placing the mark there would win.
     *
     * @param board the board to analyze
     * @param mark the mark to check for winning moves
     * @return array [row, col] of winning move, or null if none exists
     */
    private int[] findWinningMove(Board board, Mark mark) {
        int size = board.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getMark(row, col) == Mark.BLANK) {
                    if (wouldWin(board, row, col, mark)) {
                        return new int[] {row, col};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a move that creates a threat.
     * This creates a position that the opponent must block on their next turn.
     *
     * @param board the board to analyze
     * @param mark the mark to check for
     * @return array [row, col] of threat move, or null if none exists
     */
    private int[] findThreatMove(Board board, Mark mark) {
        int size = board.getSize();
        int bestScore = -1;
        int[] bestMove = null;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getMark(row, col) == Mark.BLANK) {
                    int score = countThreatScore(board, row, col, mark);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[] {row, col};
                    }
                }
            }
        }
        
        // Only create threat if it's meaningful (at least 1 mark in sequence)
        if (bestScore > 0) {
            return bestMove;
        }
        return null;
    }

    /**
     * Calculates a threat score for placing mark at (row, col).
     * Returns the maximum number of consecutive marks that would be created
     * in any direction after placing the mark.
     *
     * @param board the board to analyze
     * @param row the row to check
     * @param col the column to check
     * @param mark the mark to place
     * @return threat score (max consecutive marks in any direction)
     */
    private int countThreatScore(Board board, int row, int col, Mark mark) {
        int maxScore = 0;
        
        // Check row
        int rowScore = countLineScore(board, row, 0, 0, 1, mark, row, col);
        if (rowScore > maxScore) {
            maxScore = rowScore;
        }
        
        // Check column
        int colScore = countLineScore(board, 0, col, 1, 0, mark, row, col);
        if (colScore > maxScore) {
            maxScore = colScore;
        }
        
        // Check main diagonal
        int size = board.getSize();
        if (row == col) {
            int diagScore = countLineScore(board, 0, 0, 1, 1, mark, row, col);
            if (diagScore > maxScore) {
                maxScore = diagScore;
            }
        }
        
        // Check anti-diagonal
        if (row + col == size - 1) {
            int antiDiagScore = countLineScore(board, 0, size - 1, 1, -1, mark, row, col);
            if (antiDiagScore > maxScore) {
                maxScore = antiDiagScore;
            }
        }
        
        return maxScore;
    }

    /**
     * Counts the maximum consecutive marks in a line after placing mark at (row, col).
     *
     * @param board the board to analyze
     * @param startRow starting row of the line
     * @param startCol starting column of the line
     * @param dRow row direction step
     * @param dCol column direction step
     * @param mark the mark to check for
     * @param testRow row to treat as containing mark
     * @param testCol column to treat as containing mark
     * @return maximum consecutive marks in this line
     */
    private int countLineScore(Board board, int startRow, int startCol,
            int dRow, int dCol, Mark mark, int testRow, int testCol) {
        int maxRun = 0;
        int run = 0;
        int r = startRow;
        int c = startCol;
        int size = board.getSize();
        
        while (r >= 0 && r < size && c >= 0 && c < size) {
            Mark cellMark;
            if (r == testRow && c == testCol) {
                cellMark = mark;
            } else {
                cellMark = board.getMark(r, c);
            }
            
            if (cellMark == mark) {
                run++;
                if (run > maxRun) {
                    maxRun = run;
                }
            } else {
                run = 0;
            }
            r += dRow;
            c += dCol;
        }
        return maxRun;
    }

    /**
     * Checks if placing a mark at (row, col) would create a win.
     * Checks all lines (row, column, diagonals) passing through this cell.
     *
     * @param board the board to analyze
     * @param row the row to check
     * @param col the column to check
     * @param mark the mark to place
     * @return true if this move would win
     */
    private boolean wouldWin(Board board, int row, int col, Mark mark) {
        // Check row
        if (checkRowWin(board, row, col, mark)) {
            return true;
        }
        // Check column
        if (checkColumnWin(board, row, col, mark)) {
            return true;
        }
        // Check main diagonal (if on diagonal)
        int size = board.getSize();
        if (row == col && checkMainDiagonalWin(board, row, col, mark)) {
            return true;
        }
        // Check anti-diagonal (if on anti-diagonal)
        if (row + col == size - 1 && checkAntiDiagonalWin(board, row, col, mark)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if placing mark at (row, col) would create a win in that row.
     *
     * @param board the board to analyze
     * @param row the row to check
     * @param col the column where mark would be placed
     * @param mark the mark to check for
     * @return true if this row would contain a win
     */
    private boolean checkRowWin(Board board, int row, int col, Mark mark) {
        int run = 0;
        int maxRun = 0;
        int size = board.getSize();
        
        for (int c = 0; c < size; c++) {
            Mark cellMark;
            if (c == col) {
                cellMark = mark;
            } else {
                cellMark = board.getMark(row, c);
            }
            
            if (cellMark == mark) {
                run++;
                if (run > maxRun) {
                    maxRun = run;
                }
            } else {
                run = 0;
            }
        }
        return maxRun >= DEFAULT_WIN_STREAK;
    }

    /**
     * Checks if placing mark at (row, col) would create a win in that column.
     *
     * @param board the board to analyze
     * @param row the row where mark would be placed
     * @param col the column to check
     * @param mark the mark to check for
     * @return true if this column would contain a win
     */
    private boolean checkColumnWin(Board board, int row, int col, Mark mark) {
        int run = 0;
        int maxRun = 0;
        int size = board.getSize();
        
        for (int r = 0; r < size; r++) {
            Mark cellMark;
            if (r == row) {
                cellMark = mark;
            } else {
                cellMark = board.getMark(r, col);
            }
            
            if (cellMark == mark) {
                run++;
                if (run > maxRun) {
                    maxRun = run;
                }
            } else {
                run = 0;
            }
        }
        return maxRun >= DEFAULT_WIN_STREAK;
    }

    /**
     * Checks if placing mark at (row, col) would create a win in main diagonal.
     *
     * @param board the board to analyze
     * @param row the row where mark would be placed
     * @param col the column where mark would be placed
     * @param mark the mark to check for
     * @return true if main diagonal would contain a win
     */
    private boolean checkMainDiagonalWin(Board board, int row, int col, Mark mark) {
        int run = 0;
        int maxRun = 0;
        int size = board.getSize();
        
        for (int i = 0; i < size; i++) {
            Mark cellMark;
            if (i == row && i == col) {
                cellMark = mark;
            } else {
                cellMark = board.getMark(i, i);
            }
            
            if (cellMark == mark) {
                run++;
                if (run > maxRun) {
                    maxRun = run;
                }
            } else {
                run = 0;
            }
        }
        return maxRun >= DEFAULT_WIN_STREAK;
    }

    /**
     * Checks if placing mark at (row, col) would create a win in anti-diagonal.
     *
     * @param board the board to analyze
     * @param row the row where mark would be placed
     * @param col the column where mark would be placed
     * @return true if anti-diagonal would contain a win
     */
    private boolean checkAntiDiagonalWin(Board board, int row, int col, Mark mark) {
        int run = 0;
        int maxRun = 0;
        int size = board.getSize();
        
        for (int i = 0; i < size; i++) {
            int r = i;
            int c = size - 1 - i;
            Mark cellMark;
            if (r == row && c == col) {
                cellMark = mark;
            } else {
                cellMark = board.getMark(r, c);
            }
            
            if (cellMark == mark) {
                run++;
                if (run > maxRun) {
                    maxRun = run;
                }
            } else {
                run = 0;
            }
        }
        return maxRun >= DEFAULT_WIN_STREAK;
    }

    /**
     * Gets the opponent's mark.
     *
     * @param mark the current player's mark
     * @return the opponent's mark (X if mark is O, O if mark is X)
     */
    private Mark getOpponentMark(Mark mark) {
        if (mark == Mark.X) {
            return Mark.O;
        }
        return Mark.X;
    }

    /**
     * Gets the center position of the board, if it exists.
     *
     * @param size the board size
     * @return array [row, col] of center, or null if size is even (no exact center)
     */
    private int[] getCenter(int size) {
        if (size % 2 == 1) {
            int center = size / 2;
            return new int[] {center, center};
        }
        return null;
    }

    /**
     * Finds any empty corner on the board.
     *
     * @param board the board to analyze
     * @return array [row, col] of an empty corner, or null if no corners are empty
     */
    private int[] findEmptyCorner(Board board) {
        int size = board.getSize();
        int[] corners = new int[] {
            0, 0,
            0, size - 1,
            size - 1, 0,
            size - 1, size - 1
        };
        for (int i = 0; i < corners.length; i += 2) {
            int row = corners[i];
            int col = corners[i + 1];
            if (board.getMark(row, col) == Mark.BLANK) {
                return new int[] {row, col};
            }
        }
        return null;
    }
}
