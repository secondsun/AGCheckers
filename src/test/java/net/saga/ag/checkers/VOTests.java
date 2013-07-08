package net.saga.ag.checkers;

import net.saga.ag.checkers.vo.*;
import org.junit.Assert;
import org.junit.Test;

public class VOTests {

    @Test
    public void testTile() {

        User user = new User();
        user.setUserName("summers");

        Piece piece = new Piece();
        piece.setColor(Color.BLACK);
        piece.setIsKing(false);
        piece.setPlayer(user);

        Tile tile = new Tile();
        tile.setColor(Color.BLACK);
        tile.setPiece(piece);

        Board board = new Board();
        board.putTile(0,0,tile);

        Tile test = board.getTileAt(0,0);

        Assert.assertEquals(tile, test);
    }
}
