/**
 * A player that performs a single legal move on its turn.
 */
public interface Player {
    /**
     * Performs the player's move on the given board using the given mark.
     *
     * @param board the board on which to play
     * @param mark  the mark assigned to this player
     */
    void playTurn(Board board, Mark mark);
}