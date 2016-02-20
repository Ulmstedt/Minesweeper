package minesweeper.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import minesweeper.Game.Constants.Constants;
import minesweeper.Game.Game;
import minesweeper.Game.GameListener;

/**
 * The GameComponent class is a graphical representation of a Game object.
 *
 * @author Sehnsucht
 */
public class GameComponent extends JComponent implements GameListener, MouseListener {

    private final Game game;
    final private int width, height;

    /**
     *
     * @param game game that the component should display
     */
    public GameComponent(Game game) {
        this.game = game;
        this.width = Constants.SQUARE_SIZE * game.getWidth() + Constants.LINE_THICKNESS;
        this.height = Constants.SQUARE_SIZE * game.getHeight() + Constants.PADDING_TOP + Constants.LINE_THICKNESS + Constants.PADDING_BOTTOM;
        addMouseListener(this);
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
        //Draw mines
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                if (board[x][y] == 1) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(x * Constants.SQUARE_SIZE + 7, y * Constants.SQUARE_SIZE + 7, 2 * Constants.SQUARE_SIZE / 3, 2 * Constants.SQUARE_SIZE / 3);
                }

                if (revealed[x][y] == 1) {
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(x * Constants.SQUARE_SIZE + 7, y * Constants.SQUARE_SIZE + 7, 5, 5);
                }
            }
        }

        if (true != true) {
            g2d.setColor(new Color(0f, 0f, 0f, 0.4f));
            g2d.fillRect(0, 0, width, height + Constants.PADDING_TOP + Constants.PADDING_BOTTOM);
            g2d.setFont(new Font("Serif", Font.BOLD, 50));
            g2d.drawString("Player wins!", width / 2 - 143, height / 2 - 10);
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
        if (true == true) {
            if (SwingUtilities.isRightMouseButton(e)) { //Right mouse click

            } else { //Left mouse click
                int x = e.getX() / Constants.SQUARE_SIZE;
                int y = e.getY() / Constants.SQUARE_SIZE;
                game.revealBlock(x, y);
                System.out.println("Revealed (" + x + ", " + y + ")");
            }
        } else {
            game.resetGame();
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

}
