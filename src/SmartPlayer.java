/**
 * A smart automatic player optimized for 4x4 boards with win streak of 3.
 * Strategy: Win now, block opponent, create threats, take corners, then first available.
 * This player is designed to win at least 80% of games against NaivePlayer and WhateverPlayer
 * on the default board configuration (4x4 board, win streak 3).
 */
public class SmartPlayer implements Player {

    /** Default win streak for threat detection. */
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
     * 4. Take any empty corner
     * 5. Fall back to first available cell
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
        
        // 4. Take any empty corner
        move = findEmptyCorner(board);
        if (move != null) {
            board.putMark(mark, move[0], move[1]);
            return;
        }
        
        // 5. Fall back to first available (like NaivePlayer)
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
        
        // For win streak 3, a threat is 2 consecutive marks
        int threatLength = DEFAULT_WIN_STREAK - 1;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getMark(row, col) == Mark.BLANK) {
                    // Check if placing mark here would create 2 in a row
                    if (wouldCreateThreat(board, row, col, mark, threatLength)) {
                        return new int[] {row, col};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if placing mark at (row, col) would create a threat of the given length.
     * Simplified check for default configuration (win streak 3, threat length 2).
     *
     * @param board the board to analyze
     * @param row the row to check
     * @param col the column to check
     * @param mark the mark to place
     * @param threatLength the required consecutive marks for a threat (winStreak - 1)
     * @return true if this move would create a threat
     */
    private boolean wouldCreateThreat(Board board, int row, int col, Mark mark, int threatLength) {
        // Check row
        if (checkLineThreat(board, row, 0, 0, 1, mark, row, col, threatLength)) {
            return true;
        }
        
        // Check column
        if (checkLineThreat(board, 0, col, 1, 0, mark, row, col, threatLength)) {
            return true;
        }
        
        // Check main diagonal
        int size = board.getSize();
        if (row == col && checkLineThreat(board, 0, 0, 1, 1, mark, row, col, threatLength)) {
            return true;
        }
        
        // Check anti-diagonal
        if (row + col == size - 1 && 
            checkLineThreat(board, 0, size - 1, 1, -1, mark, row, col, threatLength)) {
            return true;
        }
        
        return false;
    }

    /**
     * Checks if a line would contain a threat after placing mark at (testRow, testCol).
     *
     * @param board the board to analyze
     * @param startRow starting row of the line
     * @param startCol starting column of the line
     * @param dRow row direction step
     * @param dCol column direction step
     * @param mark the mark to check for
     * @param testRow row to treat as containing mark
     * @param testCol column to treat as containing mark
     * @param threatLength required consecutive marks for a threat
     * @return true if this line would contain a threat
     */
    private boolean checkLineThreat(Board board, int startRow, int startCol,
            int dRow, int dCol, Mark mark, int testRow, int testCol, int threatLength) {
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
        return maxRun >= threatLength;
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
        int size = board.getSize();
        
        // Check row
        if (checkLineWin(board, row, 0, 0, 1, mark, row, col)) {
            return true;
        }
        
        // Check column
        if (checkLineWin(board, 0, col, 1, 0, mark, row, col)) {
            return true;
        }
        
        // Check main diagonal (if on diagonal)
        if (row == col && checkLineWin(board, 0, 0, 1, 1, mark, row, col)) {
            return true;
        }
        
        // Check anti-diagonal (if on anti-diagonal)
        if (row + col == size - 1 && 
            checkLineWin(board, 0, size - 1, 1, -1, mark, row, col)) {
            return true;
        }
        
        return false;
    }

    /**
     * Checks if a line would contain a win after placing mark at (testRow, testCol).
     *
     * @param board the board to analyze
     * @param startRow starting row of the line
     * @param startCol starting column of the line
     * @param dRow row direction step
     * @param dCol column direction step
     * @param mark the mark to check for
     * @param testRow row to treat as containing mark
     * @param testCol column to treat as containing mark
     * @return true if this line would contain a win
     */
    private boolean checkLineWin(Board board, int startRow, int startCol,
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
