/**
 * Loki AI Minesweeper implementation
 *
 * AILoki.java
 * Created on 2016-03-12
 * Version 0.7.0 Beta
 *
 * Written by Jimmy Nordström.
 * © 2016 Jimmy Nordström.
 *
 * Licenced under GNU GPLv3.
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * If you have questions, contact me at pzyber@pzyber.net
 */

package Minesweeper.Game.AI;

import Minesweeper.Game.Enums.MoveType;
import Minesweeper.Game.Game;
import Minesweeper.Game.Interfaces.IAIPlayer;
import Minesweeper.Game.Interfaces.IObserver;
import Minesweeper.Game.Move;
import Pzyber.Loki.Minesweeper.Loki;
import Pzyber.Loki.Minesweeper.LokiResult;
import Pzyber.Loki.Minesweeper.Utils;

import java.awt.*;
import java.net.InetAddress;

public class AILoki implements IAIPlayer, IObserver {

    private Loki loki;
    private Game game;
    private int[][] pointGrid;

    // Memory DB
    public AILoki(Game game, int searchWidth) {
        this.game = game;
        pointGrid = new int[game.getHeight()][game.getWidth()];
        loki = new Loki(game.getWidth(), game.getHeight(), searchWidth);
    }

    // Folder DB
    public AILoki(Game game, String folderPath, int searchWidth) {
        this.game = game;
        pointGrid = new int[game.getHeight()][game.getWidth()];
        loki = new Loki(folderPath, game.getWidth(), game.getHeight(), searchWidth);
    }

    // File DB
    public AILoki(Game game, String folderPath, String filename, int searchWidth) {
        this.game = game;
        pointGrid = new int[game.getHeight()][game.getWidth()];
        loki = new Loki(folderPath, filename, game.getWidth(), game.getHeight(), searchWidth);
    }

    // SQL DB
    public AILoki(Game game, InetAddress address, int port, String database, String username, String password,
                  int searchWidth) {
        this.game = game;
        pointGrid = new int[game.getHeight()][game.getWidth()];
        loki = new Loki(address, port, database, username, password, game.getWidth(), game.getHeight(), searchWidth);
    }

    @Override
    public Move makeMove() {
        // Get move.
        LokiResult lr = loki.thinkOfAMove(Utils.changeToYXBoard(game.getRevealed()));

        // Get pointGrid.
        //pointGrid = Utils.cloneMatrix(Utils.changeToYXBoard(lr.getCalculatedDataRoundedScaled(999999)));

        // Play move.
        Point move = lr.getMove();
        return new Move(move.x, move.y, MoveType.REVEAL);
    }

    @Override
    public int[][] getPointGrid() {
        return new int[game.getWidth()][game.getHeight()];
    }

    @Override
    public void gameEnded(boolean win, int blocksRevealed, int blocksLeft){
        loki.storeRegisteredMovesInDB(win);
    }

    @Override
    public void moveMade(Move move) {
        loki.registerMoveInDB(Utils.changeToYXBoard(game.getRevealed()), new Point(move.x, move.y));
    }
}
