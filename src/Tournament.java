/**
 * Runs a tournament of multiple Tic-Tac-Toe games between two players.
 * Players alternate playing as X and O across rounds.
 */
public class Tournament {
    private final int rounds;
    private final Renderer renderer;
    private final Player player1;
    private final Player player2;

    /**
     * Constructs a new tournament with the specified number of rounds,
     * renderer, and players.
     *
     * @param rounds number of rounds to play
     * @param renderer the renderer to use for displaying boards
     * @param player1 the first player
     * @param player2 the second player
     */
    public Tournament(int rounds, Renderer renderer, Player player1, Player player2) {
        this.rounds = rounds;
        this.renderer = renderer;
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Plays the tournament with the specified board size and win streak.
     * Players alternate being X and O: in even rounds player1 is X,
     * in odd rounds player2 is X.
     *
     * @param size board size (n x n)
     * @param winStreak number of consecutive marks required to win
     * @param playerName1 name/type of player1
     * @param playerName2 name/type of player2
     */
    public void playTournament(int size, int winStreak, String playerName1,
            String playerName2) {
        int player1Wins = 0;
        int player2Wins = 0;
        int ties = 0;
        
        for (int i = 0; i < this.rounds; i++) {
            Player playerX;
            Player playerO;
            
            // Alternate: even rounds (0, 2, 4...) player1 is X, odd rounds player2 is X
            if (i % 2 == 0) {
                playerX = this.player1;
                playerO = this.player2;
            } else {
                playerX = this.player2;
                playerO = this.player1;
            }
            
            Game game = new Game(playerX, playerO, size, winStreak, this.renderer);
            Mark winner = game.run();
            
            // Count wins and ties
            if (winner == Mark.X) {
                if (playerX == this.player1) {
                    player1Wins++;
                } else {
                    player2Wins++;
                }
            } else if (winner == Mark.O) {
                if (playerO == this.player1) {
                    player1Wins++;
                } else {
                    player2Wins++;
                }
            } else {
                // BLANK means tie
                ties++;
            }
        }
        
        // Print results
        System.out.println("######### Results #########");
        System.out.println("Player 1, " + playerName1 + " won: " + player1Wins
                + " rounds");
        System.out.println("Player 2, " + playerName2 + " won: " + player2Wins
                + " rounds");
        System.out.println("Ties: " + ties);
    }

    /**
     * Main method that parses command-line arguments and runs the tournament.
     * Command line format: java Tournament [rounds] [size] [winStreak]
     * [renderer] [player1] [player2]
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Parse command-line arguments
        int rounds = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        int winStreak = Integer.parseInt(args[2]);
        String rendererType = args[3];
        String player1Type = args[4];
        String player2Type = args[5];
        
        // Create factories
        RendererFactory rendererFactory = new RendererFactory();
        PlayerFactory playerFactory = new PlayerFactory();
        
        // Create renderer and players
        Renderer renderer = rendererFactory.buildRenderer(rendererType, size);
        Player player1 = playerFactory.buildPlayer(player1Type);
        Player player2 = playerFactory.buildPlayer(player2Type);
        
        // Create and run tournament
        Tournament tournament = new Tournament(rounds, renderer, player1, player2);
        tournament.playTournament(size, winStreak, player1Type, player2Type);
    }
}
