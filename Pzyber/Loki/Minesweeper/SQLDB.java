/**
 * Loki AI
 *
 * SQLDB.java
 * Created on 2016-01-07
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
import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;

public class SQLDB implements ILokiDB {
    public static final byte DRAW = 0;
    public static final byte LOSS = 1;
    public static final byte WIN = 2;
    public static final byte DRAWS = 0;
    public static final byte LOSSES = 1;
    public static final byte WINS = 2;

    private Statement stmt;

    public SQLDB(InetAddress address, int port, String database, String username, String password) throws SQLException {
        // Connect to SQL-server.
        String url = "jdbc:mysql://" + address.getHostAddress() + ":" + port + "/" + database;
        Connection con;
        con = DriverManager.getConnection(url, username, password);
        stmt = con.createStatement();
    }

    @Override
    public void addToDB(String hash, Point move, byte result) {
        if (hashAndMoveExistsInDB(hash, move)) {
            try {
                // Get draws, losses and wins.
                long[] dbData = new long[3];
                String query = "SELECT Draws, Losses, Wins FROM data WHERE Hash = '" + hash + "' AND X = '" + move.x +
                        "' AND Y = '" + move.y + "';";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    dbData[DRAWS] = rs.getLong("Draws") + (result == DRAW ? 1 : 0);
                    dbData[LOSSES] = rs.getLong("Losses") + (result == LOSS ? 1 : 0);
                    dbData[WINS] = rs.getLong("Wins") + (result == WIN ? 1 : 0);
                }

                // Update data.
                stmt.executeUpdate("UPDATE data SET Draws = '" + dbData[DRAWS] + "', Losses = '" + dbData[LOSSES] +
                        "', Wins = '" + dbData[WINS] + "' WHERE Hash = '" + hash + "' AND X = '" + move.x + "'" +
                        " AND Y = '" + move.y + "';");
            } catch (SQLException ignored) {
            }
        } else {
            // Insert data.
            try {
                stmt.executeUpdate("INSERT INTO data VALUES ('" + hash + "', '" + move.x + "', '" + move.y + "'," +
                        " '" + (result == DRAW ? 1 : 0) + "', '" + (result == LOSS ? 1 : 0) + "', '" +
                        (result == WIN ? 1 : 0) + "');");
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public void addToDBDone() {

    }

    private boolean hashAndMoveExistsInDB(String hash, Point move) {
        try {
            String query = "SELECT Hash, X, Y FROM data WHERE Hash = '" + hash + "' AND X = '" + move.x + "' AND " +
                    "Y = " + "'" + move.y + "';";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    @Override
    public ArrayList<MoveData> getAvailableMovesFromDB(String hash, int startX, int startY, boolean mirror,
                                                       int rotations, int size) {
        ArrayList<MoveData> availableMoves = new ArrayList<>();

        try {
            String query = "SELECT X, Y, Draws, Losses, Wins FROM data WHERE Hash = '" + hash + "';";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                // Get move.
                Point move = new Point(rs.getInt("X"), rs.getInt("Y"));

                // De-rotate, de-mirror and de-scale move.
                move = Utils.scaleMirrorAndRotate(move, startX, startY, mirror, rotations, size);

                // Get draws, losses and wins.
                long dbData[] = {rs.getLong("Draws"), rs.getLong("Losses"), rs.getLong("Wins")};

                // Add to available moves.
                availableMoves.add(new MoveData(move, dbData[DRAWS], dbData[LOSSES], dbData[WINS]));
            }
        } catch (SQLException ignored) {
        }

        return availableMoves;
    }
}