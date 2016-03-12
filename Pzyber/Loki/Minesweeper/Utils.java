/**
 * Loki AI
 *
 * Utils.java
 * Created on 2015-12-30
 * Version 0.7.0 Beta
 *
 * Written by Jimmy Nordström.
 * © 2015-2016 Jimmy Nordström.
 *
 * Licenced under GNU GPLv3.
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * If you have questions, contact me at pzyber@pzyber.net
 */

package Pzyber.Loki.Minesweeper;

import java.awt.Point;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String calculateHash(int[][] searchPattern) {
        // Get search pattern as String.
        String stringboard = getSearchPatternAsString(searchPattern);

        // Calculate and return SHA hash.
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(stringboard.getBytes());
            byte[] digest = md.digest();

            // Convert to String.
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                if ((0xff & b) < 0x10) {
                    hexString.append("0").append(Integer.toHexString((0xFF & b)));
                } else {
                    hexString.append(Integer.toHexString(0xFF & b));
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return stringboard;
        }
    }

    public static int[][] changeToYXBoard(int[][] board) {
        int width = board[0].length;
        int height = board.length;

        int[][] fixedBoard = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                fixedBoard[y][x] = board[x][y];
            }
        }

        return fixedBoard;
    }

    public static int[][] cloneMatrix(int[][] matrix) {
        int[][] clonedMatrix = new int[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            clonedMatrix[i] = matrix[i].clone();
        }

        return clonedMatrix;
    }

    public static int[][] getSearchPattern(int[][] board, int startX, int startY, int endX, int endY) {
        int[][] searchPattern = new int[(endY - startY) + 1][(endX - startX) + 1];

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                searchPattern[y - startY][x - startX] = board[y][x];
            }
        }

        return searchPattern;
    }

    public static String getSearchPatternAsString(int[][] searchPattern) {
        String result = "";

        for (int y = 0; y < searchPattern.length; y++) {
            for (int x = 0; x < searchPattern[0].length; x++) {
                result += Integer.toString(searchPattern[y][x]);
            }
        }

        return result;
    }

    public static Point getMoveByBoardComparison(int[][] previous, int[][] current) {
        for (int y = 0; y < current.length; y++) {
            for (int x = 0; x < current[0].length; x++) {
                if (current[y][x] > previous[y][x]) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public static boolean hasAdjecentMoveOrFullSize(int[][] board, int startX, int startY, int endX, int endY) {
        if (startX == 0 && startY == 0 && endX == board[0].length - 1 && endY == board.length - 1) {
            return true;
        }

        int value = 0;
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                value += board[y][x];
            }
        }
        return value > 0;
    }

    public static int[][] initNewBoard(int width, int height){
        int[][] newBoard = new int[width][height];

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                newBoard[y][x] = -1;
            }
        }
    }

    public static Point mirrorMoveVertically(Point move, int size) {
        return new Point((size - 1) - move.x, move.y);
    }

    public static int[][] mirrorSearchPatternVertically(int[][] searchPattern) {
        int size = searchPattern.length;
        int[][] mirrored = new int[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                mirrored[y][x] = searchPattern[y][(size - 1) - x];
            }
        }

        return mirrored;
    }

    public static int[][] rotateSearchPatternClockwise(int[][] searchPatern) {
        int size = searchPatern.length;
        int[][] rotatedBoard = new int[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                rotatedBoard[y][x] = searchPatern[(size - 1) - x][y];
            }
        }

        return rotatedBoard;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Point rotateMoveAntiClockwise(Point move, int size) {
        return new Point(move.y, size - 1 - move.x);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Point rotateMoveClockwise(Point move, int size) {
        return new Point(size - 1 - move.y, move.x);
    }

    public static Point scaleMirrorAndRotate(Point move, int startX, int startY, boolean mirror, int rotations,
                                             int size) {
        // Copy to new point.
        Point m = new Point(move.x, move.y);

        // Rotate.
        for (int i = 4 - rotations; i < 4; i++) {
            m = Utils.rotateMoveAntiClockwise(m, size);
        }

        // Mirror.
        if (mirror) {
            m = Utils.mirrorMoveVertically(m, size);
        }

        // Scale m to board and return.
        return new Point(startX + m.x, startY + m.y);
    }
}
