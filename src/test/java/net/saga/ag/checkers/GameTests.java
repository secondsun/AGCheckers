package net.saga.ag.checkers;

import net.saga.ag.checkers.handler.GameHandler;
import net.saga.ag.checkers.handler.LoginHandler;
import net.saga.ag.checkers.vo.Color;
import net.saga.ag.checkers.vo.Game;
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
