package Minesweeper.GUI;

import Minesweeper.Game.Constants.Constants;
import Minesweeper.Game.Enums.MoveType;
import Minesweeper.Game.Game;
import Minesweeper.Game.GameState;
import Minesweeper.Game.Interfaces.IGameListener;
import Minesweeper.Game.Interfaces.IObserver;
import Minesweeper.Game.Move;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * The GameComponent class is a graphical representation of a Game object.
 *
 * @author Sehnsucht
 */
public class GameComponent extends JComponent implements IGameListener, MouseListener, MouseMotionListener {

    private final Game game;
    final private int width, height;

    BufferedImage mineImage, flagImage, questionmarkImage;

    private final Color[] colorList = {
        Color.BLUE,
        Color.GREEN,
        Color.ORANGE,
        Color.BLACK,
        Color.RED,
        Color.CYAN,
        Color.PINK,
        Color.magenta
    };

    /**
     * Visually displays a Minesweeper game
     *
     * @param game game that the component should display
     */
    public GameComponent(Game game) {
        this.game = game;
        this.width = Constants.SQUARE_SIZE * game.getWidth() + Constants.LINE_THICKNESS;
        this.height = Constants.SQUARE_SIZE * game.getHeight() + Constants.PADDING_TOP + Constants.LINE_THICKNESS + Constants.PADDING_BOTTOM;
        try {
            this.mineImage = ImageIO.read(getClass().getResource("Images/mine3.png"));
            this.flagImage = ImageIO.read(getClass().getResource("Images/flag.png"));
            this.questionmarkImage = ImageIO.read(getClass().getResource("Images/questionmark.png"));
        } catch (IOException e) {
            System.out.println("Error loading image.");
            e.printStackTrace();
        }
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draw background
        g2d.setColor(new Color(0, 90, 190));
        g2d.fillRect(0, Constants.PADDING_TOP, game.getWidth() * Constants.SQUARE_SIZE, game.getHeight() * Constants.SQUARE_SIZE);

        //Draw square lines
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < width; i += Constants.SQUARE_SIZE) {
            g2d.fillRect(i, Constants.PADDING_TOP, Constants.LINE_THICKNESS, height - Constants.PADDING_TOP - Constants.PADDING_BOTTOM);
        }
        for (int i = 0; i < height; i += Constants.SQUARE_SIZE) {
            g2d.fillRect(0, i + Constants.PADDING_TOP, width, Constants.LINE_THICKNESS);
        }

        int[][] board = game.getBoard();
        int[][] revealed = game.getRevealed();
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                //Draw other background color for highlighted block
                if (x == game.getHighlighted().x && y == game.getHighlighted().y) {
                    g2d.setColor(new Color(50, 150, 220));
                    g2d.fillRect(x * Constants.SQUARE_SIZE + Constants.LINE_THICKNESS, y * Constants.SQUARE_SIZE + Constants.LINE_THICKNESS, Constants.SQUARE_SIZE - Constants.LINE_THICKNESS, Constants.SQUARE_SIZE - Constants.LINE_THICKNESS);
                }
                //Draw mines (if lost)
                if (game.getGameState() == GameState.GAMEOVER && board[x][y] == 1) {
                    g2d.drawImage(mineImage, x * Constants.SQUARE_SIZE + 3, y * Constants.SQUARE_SIZE + 3, Constants.SQUARE_SIZE - 5, Constants.SQUARE_SIZE - 5, null); //Magic numbers are good mkay
                }

                if (revealed[x][y] != -1) {
                    if (revealed[x][y] == -2) {
                        g2d.drawImage(flagImage, x * Constants.SQUARE_SIZE + 3, y * Constants.SQUARE_SIZE + 3, Constants.SQUARE_SIZE - 5, Constants.SQUARE_SIZE - 5, null); //Magic numbers are good mkay
                        continue;
                    } else if (revealed[x][y] == -3) {
                        g2d.drawImage(questionmarkImage, x * Constants.SQUARE_SIZE + 1, y * Constants.SQUARE_SIZE + 1, Constants.SQUARE_SIZE, Constants.SQUARE_SIZE, null); //Magic numbers are good mkay
                        continue;
                    } else {
                        //Draw other background color for revealed blocks
                        g2d.setColor(new Color(50, 150, 220));
                        g2d.fillRect(x * Constants.SQUARE_SIZE + Constants.LINE_THICKNESS, y * Constants.SQUARE_SIZE + Constants.LINE_THICKNESS, Constants.SQUARE_SIZE - Constants.LINE_THICKNESS, Constants.SQUARE_SIZE - Constants.LINE_THICKNESS);
                        if (revealed[x][y] != 0) {
                            //g2d.setColor(new Color(revealed[x][y] * 30, 50, 50));
                            g2d.setColor(colorList[revealed[x][y] - 1]);
                            g2d.setFont(new Font("Serif", Font.BOLD, 30));
                            g2d.drawString("" + revealed[x][y], x * Constants.SQUARE_SIZE + Constants.SQUARE_SIZE / 3, y * Constants.SQUARE_SIZE + 3 * Constants.SQUARE_SIZE / 4);
                        }
                    }
                }
            }
        }

        if (game.getGameState() == GameState.GAMEOVER) {
            g2d.setColor(new Color(0f, 0f, 0f, 0.4f));
            g2d.fillRect(0, 0, width, height + Constants.PADDING_TOP + Constants.PADDING_BOTTOM);
            g2d.setFont(new Font("Serif", Font.BOLD, 50));
            g2d.setColor(Color.RED);
            g2d.drawString("Game over!", width / 2 - 143, height / 2 - 10);
        } else if (game.getGameState() == GameState.VICTORY) {
            g2d.setColor(new Color(0f, 0f, 0f, 0.4f));
            g2d.fillRect(0, 0, width, height + Constants.PADDING_TOP + Constants.PADDING_BOTTOM);
            g2d.setFont(new Font("Serif", Font.BOLD, 50));
            g2d.setColor(Color.GREEN);
            g2d.drawString("Victory!", width / 2 - 143, height / 2 - 10);
        }
    }

    //Called when GameListeners are notified of a change
    @Override
    public void gameChanged() {
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (game.getGameState() == GameState.PLAYING && !game.AIPlaying()) {
            int x = e.getX() / Constants.SQUARE_SIZE;
            int y = e.getY() / Constants.SQUARE_SIZE;
            if (SwingUtilities.isRightMouseButton(e)) { //Right mouse click
                game.markBlockCycle(x, y);
            } else { //Left mouse click
                if (!game.gameInitialized()) {
                    game.placeMines(x, y);
                }
                game.revealBlock(x, y);

                //Notify observers of move
                ArrayList<IObserver> gameObservers = game.getGameObservers();
                for (IObserver o : gameObservers) {
                    o.moveMade(new Move(x, y, MoveType.REVEAL));
                }
            }
        } else {
            game.newGame();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX() / Constants.SQUARE_SIZE;
        int y = e.getY() / Constants.SQUARE_SIZE;
        game.highlightBlock(x, y);
    }

}
