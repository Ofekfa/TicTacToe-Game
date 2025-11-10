/**
 * A Renderer implementation that performs no output.
 * Useful for running games or tournaments without displaying the board.
 */
public class VoidRenderer implements Renderer {
    @Override
    public void renderBoard(Board board) {
        // intentionally empty
    }
}