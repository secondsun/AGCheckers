package net.saga.ag.checkers.handler

import com.gmongo.GMongo
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBObject
import net.saga.ag.checkers.vo.Board
import net.saga.ag.checkers.vo.Color
import net.saga.ag.checkers.vo.Game
import net.saga.ag.checkers.vo.Piece
import net.saga.ag.checkers.vo.Tile
import net.saga.ag.checkers.vo.User
import org.bson.types.ObjectId

class GameHandler {


    private final GMongo mongo = new GMongo()
    private final DB db = mongo.getDB("checkers")

    Game createGame(User player1) {
        def game = new Game();
        game.player1 = player1;
        game.board = new Board();
        game.currentPlayer = player1;
        Color color = Color.RED
        8.times { x ->
            8.times { y ->
                Tile tile = new Tile();
                tile.color = color;
                game.board.putTile(x,y,tile)
                color = color==Color.RED?Color.BLACK:Color.RED
            }
            color = color==Color.RED?Color.BLACK:Color.RED
        }
    DBObject obj = game as DBObject

    db.games.insert(obj);
    game._id = new ObjectId(obj['_id'].toString())
    return game;
    }

    Game joinGame(ObjectId gameId, User player2) {
        Game game = db.games.findOne(gameId)
        assert game != null

        (0..2).each { y ->
            (0..3).each { x ->
            x = x*2 + y%2
            Piece p = new Piece(color: Color.RED, player: game.player1)
            Tile t = game.board.getTileAt(x,y)
            t.piece = p
            game.board.putTile(x,y, t)
          }
         }

        (5..7).each { y ->
            (0..3).each { x ->
                x = x*2 + y%2
                Piece p = new Piece(color: Color.BLACK, player: player2)
                Tile t = game.board.getTileAt(x,y)
                t.piece = p
                game.board.putTile(x,y, t)
            }
        }

        db.games.update([_id:gameId], [$set:[player2:player2 as DBObject, board: game.board as DBObject]])
        return db.games.findOne(gameId)
    }

    List<Game> getOpenGames(User user) {
        def games = []
        db.games.find(['player1.userName' : user.userName, player2:null]).each {record ->
            games.add(new Game(record))
        }
        games
    }

    Game getGame(ObjectId gameId) {
        db.games.findOne(gameId)
    }

}
