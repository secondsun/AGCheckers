package net.saga.ag.checkers;

import net.saga.ag.checkers.vo.*;
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
        Board board = new Board();
    }
}
