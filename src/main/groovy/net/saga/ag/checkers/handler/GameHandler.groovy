package net.saga.ag.checkers.handler
import com.gmongo.GMongo
import com.mongodb.DB
import com.mongodb.DBObject
import net.saga.ag.checkers.vo.*
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
        assert game.player2 == null

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

        db.games.update([_id:gameId, player2: null], [$set:[player2:player2 as DBObject, board: game.board as DBObject, currentPlayer: game.player1 as DBObject]])
        return db.games.findOne(gameId)
    }

    List<Game> getOpenGames(User user) {
        def games = []
        db.games.find(['player1.username' : user.username, player2:null]).each {record ->
            games.add(new Game(_id: record._id, player1: record.player1))
        }
        games
    }

    List<Game> getOpenGames() {
        def games = []
        db.games.find([player2:null]).each {record ->
            games.add(new Game(_id: record._id, player1: record.player1))
        }
        games
    }

    Game getGame(ObjectId gameId) {
        db.games.findOne(gameId)
    }

    MoveResponse processMove(User player, ObjectId gameId, Move... moves) {
        Game game = db.games.findOne(gameId)
        MoveResponse response = new MoveResponse();
        assert game.currentPlayer.username == player.username

        Board gameBoard = game.board
        moves.each {move ->
            Tile startTile = gameBoard.getTileAt(move.startPosX, move.startPosY);
            Tile endTile = gameBoard.getTileAt(move.endPosX, move.endPosY);

            move.with {
                assert (0 <= startPosY  && startPosY <= 7)
                assert (0 <= startPosX  && startPosX <= 7)
                assert (0 <= endPosY  && endPosY <= 7)
                assert (0 <= endPosX  && endPosX <= 7)
            }

            startTile.with {
                assert it != null
                assert it.piece != null
                assert it.piece.player.username == player.username
            }

            endTile.with {
                assert it != null
                assert it.piece == null
            }

            Piece piece = gameBoard.getTileAt(move.startPosX, move.startPosY).piece

            Color oppositeColor = piece.color == Color.BLACK?Color.RED:Color.BLACK

            int endRow = piece.color == Color.BLACK?0:7

            if (!piece.isKing) {
                switch (piece.color) {
                    case Color.RED:
                        assert (move.endPosY - move.startPosY > 0)//Moves down
                        break;
                    case Color.BLACK:
                        assert (move.endPosY - move.startPosY < 0)//Moves up
                        break;
                    default:
                        throw new IllegalStateException("Illegal piece color")
                }
            }

            assert (move.endPosX != move.startPosX)//Moves Left or Right

            if (isJump(move, piece)) {
                int jumpX = (move.endPosX + move.startPosX)/2
                int jumpY = (move.endPosY + move.startPosY)/2
                Piece testPiece = gameBoard.getTileAt(jumpX, jumpY).piece
                assert piece != null
                assert testPiece.color == oppositeColor
                gameBoard.getTileAt(jumpX, jumpY).piece = null

            } else {
                assert (Math.abs(move.endPosX - move.startPosX) == 1)
            }

            if (move.endPosY == endRow) {
                piece.isKing = true;
                response.madeKing = true;
            }

            gameBoard.getTileAt(move.startPosX, move.startPosY).piece = null
            gameBoard.getTileAt(move.endPosX, move.endPosY).piece = piece

        }

        game.with {
            currentPlayer = (currentPlayer.username == player1.username?player2:player1)//Switch players
        }

        int black = 0;
        int red = 0;

        gameBoard.tiles.each {
            if (it.piece != null) {
                if (it.piece.color == Color.BLACK) {
                    black++
                } else {
                    red++
                }
            }
        }

        if (black == 0 || red == 0) {
            response.hasWon = true;
            game.winner = player
        }

        db.games.update([_id:gameId], [$set:[board: game.board as DBObject, currentPlayer:game.currentPlayer as DBObject, winner:game.winner as DBObject]])
        response;

    }

    private boolean isJump(Move move, Piece piece) {
        Math.abs(move.endPosX - move.startPosX) == 2
    }

}
