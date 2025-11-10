/**
 * Renders a board snapshot to the chosen output.
 */
public interface Renderer {
    /**
     * Renders the current board state.
     *
     * @param board the board to render
     */
    void renderBoard(Board board);
}