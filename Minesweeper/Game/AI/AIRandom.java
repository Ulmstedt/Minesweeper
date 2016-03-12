package Minesweeper.Game.AI;

import Minesweeper.Game.Enums.MoveType;
import Minesweeper.Game.Game;
import Minesweeper.Game.Interfaces.IAIPlayer;
import Minesweeper.Game.Interfaces.IObserver;
import Minesweeper.Game.Move;
import Pzyber.Loki.Minesweeper.Loki;
import Pzyber.Loki.Minesweeper.Utils;

import java.awt.*;
import java.net.InetAddress;
import java.util.Random;

public class AIRandom implements IAIPlayer{

    private Game game;

    public AIRandom(Game game) {
        this.game = game;
    }

    @Override
    public Move makeMove() {
        boolean notfound = true;
        Random r = new Random();
        Point p = new Point(0, 0);
        while(notfound)
        {
            p = new Point(r.nextInt(game.getWidth()), r.nextInt(game.getHeight()));
            if(game.getRevealed()[p.x][p.y] == -1) {
                notfound = false;
            }
            else{
                System.out.println("Tried move " + p.x + " : " + p.y);
            }
        }
        System.out.println("Move made " + p.x + " : " + p.y);
        return new Move(p.x, p.y, MoveType.REVEAL);
    }

    @Override
    public int[][] getPointGrid() {
        return new int[0][];
    }
}
