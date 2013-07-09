package net.saga.ag.checkers;

import net.saga.ag.checkers.handler.GameHandler;
import net.saga.ag.checkers.handler.LoginHandler;
import net.saga.ag.checkers.vo.Color;
import net.saga.ag.checkers.vo.Game;
import net.saga.ag.checkers.vo.Move;
import net.saga.ag.checkers.vo.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(JUnit4.class)
public class GameTests {

    private LoginHandler loginHandler = new LoginHandler();
    private GameHandler gameHandler = new GameHandler();

    @Test
    public void createGame() {
        User user = getUser();
        Game game = gameHandler.createGame(user);

        Assert.assertEquals(Color.RED, game.getBoard().getTileAt(0,0).getColor());
        Assert.assertEquals(Color.BLACK, game.getBoard().getTileAt(6,7).getColor());
        Assert.assertEquals(Color.BLACK, game.getBoard().getTileAt(1,0).getColor());

    }


    @Test
    public void getGames() {
        User user = getUser();
        Game game = null;
        for (int i = 0 ; i < 10; i++) {
             game = gameHandler.createGame(user);
        }
        gameHandler.joinGame(game.get_id(), getUser());
        Assert.assertEquals(9, gameHandler.getOpenGames(user).size());
    }

    @Test
    public void joinGames() {
        User user = getUser();
        User player2;
        Game game = gameHandler.createGame(user);

        gameHandler.joinGame(game.get_id(), player2 = getUser());
        game = gameHandler.getGame(game.get_id());
        Assert.assertEquals(player2, game.getPlayer2());
        Assert.assertEquals(null, game.getBoard().getTileAt(0,7).getPiece());
        Assert.assertEquals(null, game.getBoard().getTileAt(7,0).getPiece());
        Assert.assertEquals(player2, game.getBoard().getTileAt(0,6).getPiece().getPlayer());
        Assert.assertEquals(user, game.getBoard().getTileAt(6,0).getPiece().getPlayer());
    }


    @Test
    public void cantJoinGameTwice() {
        User user = getUser();

        Game game = gameHandler.createGame(user);

        gameHandler.joinGame(game.get_id(), getUser());
        try {
            gameHandler.joinGame(game.get_id(), getUser());
        } catch (Throwable ex) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testGameMoves() {
        User player1 = getUser();
        boolean caught = false;
        User player2;
        Game game = gameHandler.createGame(player1);
        gameHandler.joinGame(game.get_id(), player2 = getUser());
        game = gameHandler.getGame(game.get_id());

        Move move = new Move();//illegal
        move.setStartPosX(0);
        move.setStartPosY(0);
        move.setEndPosX(1);
        move.setEndPosY(1);

        try {
            gameHandler.processMove(player1, game.get_id(), move);
        } catch (Throwable t) {
            caught = true;
        }
        if (!caught) {
            Assert.fail();
        }

        caught = false;
        game = gameHandler.getGame(game.get_id());

        move = new Move();//illegal
        move.setStartPosX(0);
        move.setStartPosY(0);
        move.setEndPosX(0);
        move.setEndPosY(1);

        try {
            gameHandler.processMove(player1, game.get_id(), move);
        } catch (Throwable t) {
            caught = true;
        }
        if (!caught) {
            Assert.fail();
        }

        caught = false;


        move = new Move();//legal
        move.setStartPosX(0);
        move.setStartPosY(2);
        move.setEndPosX(1);
        move.setEndPosY(3);

        try {
            gameHandler.processMove(player1, game.get_id(), move);
        } catch (Throwable t) {
            Assert.fail();
        }

        caught = false;

        move = new Move();//fail red move 2x in a row
        move.setStartPosX(1);
        move.setStartPosY(3);
        move.setEndPosX(2);
        move.setEndPosY(4);

        try {
            gameHandler.processMove(player1, game.get_id(), move);
        } catch (Throwable t) {
            caught = true;
        }

        if (!caught) {
            Assert.fail();
        }
        caught = false;

        caught = false;

        move = new Move();//fail black can't move reds piece move 2x in a row
        move.setStartPosX(1);
        move.setStartPosY(3);
        move.setEndPosX(2);
        move.setEndPosY(4);

        try {
            gameHandler.processMove(player2, game.get_id(), move);
        } catch (Throwable t) {
            caught = true;
        }

        if (!caught) {
            Assert.fail();
        }
        caught = false;

        move = new Move();//legal
        move.setStartPosX(3);
        move.setStartPosY(5);
        move.setEndPosX(2);
        move.setEndPosY(4);

        try {
            gameHandler.processMove(player2, game.get_id(), move);
        } catch (Throwable t) {
            Assert.fail();
        }

        caught = false;

        move = new Move();//legal jump
        move.setStartPosX(1);
        move.setStartPosY(3);
        move.setEndPosX(3);
        move.setEndPosY(5);

        try {
            gameHandler.processMove(player1, game.get_id(), move);
        } catch (Throwable t) {
            Assert.fail();
        }

        caught = false;

        move = new Move();//legal move
        move.setStartPosX(7);
        move.setStartPosY(5);
        move.setEndPosX(6);
        move.setEndPosY(4);

        try {
            gameHandler.processMove(player2, game.get_id(), move);
        } catch (Throwable t) {
            Assert.fail();
        }

        caught = false;

        move = new Move();//legal move
        move.setStartPosX(4);
        move.setStartPosY(2);
        move.setEndPosX(3);
        move.setEndPosY(3);

        try {
            gameHandler.processMove(player1, game.get_id(), move);
        } catch (Throwable t) {
            Assert.fail();
        }

        caught = false;


        move = new Move();//illegal move after jump
        move.setStartPosX(1);
        move.setStartPosY(3);
        move.setEndPosX(2);
        move.setEndPosY(4);

        Move move2 = new Move();
        move2.setStartPosX(2);
        move2.setStartPosY(4);
        move2.setEndPosX(1);
        move2.setEndPosY(3);

        try {
            gameHandler.processMove(player2, game.get_id(), move, move2);
        } catch (Throwable t) {
            caught = true;
        }

        if (!caught) {
            Assert.fail();
        }
        caught = false;

        move = new Move();//legal double jump
        move.setStartPosX(4);
        move.setStartPosY(6);
        move.setEndPosX(2);
        move.setEndPosY(4);

        move2 = new Move();//legal double jump
        move2.setStartPosX(2);
        move2.setStartPosY(4);
        move2.setEndPosX(4);
        move2.setEndPosY(2);


        try {
            gameHandler.processMove(player2, game.get_id(), move,move2);
        } catch (Throwable t) {
            Assert.fail();
        }

        caught = false;

    }


    private User getUser() {
        String randomUsername = UUID.randomUUID().toString();
        String randomPassword = UUID.randomUUID().toString();

        Map<String, String> userData = new HashMap<>();
        userData.put("userName", randomUsername);
        userData.put("password", randomPassword);

        User user = loginHandler.enroll(userData);
        return user;
    }
}
