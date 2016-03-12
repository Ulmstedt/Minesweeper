package Minesweeper.Game;

import Minesweeper.Game.Enums.MoveType;
import Minesweeper.Game.Interfaces.IAIPlayer;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Sehnsucht
 */
public class TestAI implements IAIPlayer {

    Game game;

    public TestAI(Game game) {
        this.game = game;
    }

    @Override
    public Move makeMove() {
        int x = ThreadLocalRandom.current().nextInt(0, game.getWidth());
        int y = ThreadLocalRandom.current().nextInt(0, game.getHeight());
        return new Move(x, y, MoveType.REVEAL);
    }

    @Override
    public int[][] getPointGrid() {
        int[][] pointGrid = new int[game.getWidth()][game.getHeight()];
        return pointGrid;
    }

}
