package net.saga.ag.checkers.vo

import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBObject
import groovy.transform.EqualsAndHashCode
import net.saga.ag.checkers.vo.Color
import org.apache.tools.ant.taskdefs.condition.Equals
import org.bson.types.ObjectId

import java.awt.*

@EqualsAndHashCode
class User {
    String userName
    String password
    String sessionId

    public Object asType(Class type) {
        if (type == DBObject) {
            new BasicDBObject([userName:userName, password:password, sessionId:sessionId]);
        }
    }
}

@EqualsAndHashCode
class Move {
    Point startPos, endPos
}

@EqualsAndHashCode
class Board {
    Tile[] tiles = new Tile[64]

    Tile getTileAt(int x ,int y) {
        tiles[x*8 + y]
    }

    Tile putTile(int x ,int y, Tile tile) {
        tiles[x*8 + y] = tile
    }

    public Object asType(Class type) {
        if (type == DBObject) {
            BasicDBObject object = new BasicDBObject();
            object['tiles'] = new BasicDBList();
            tiles.eachWithIndex{ Tile entry, int i -> object['tiles'].put(i as String, entry as DBObject)}
            return object
        }
    }
}


@EqualsAndHashCode
class Tile {
    Color color
    Piece piece

    public Object asType(Class type) {
        if (type == DBObject) {
            BasicDBObject object = new BasicDBObject();
            object['color'] = color.name()
            object['piece'] = piece as DBObject
            return object
        }
    }

}

enum Color {
    RED, BLACK
}

@EqualsAndHashCode
class Piece {
    User player
    Color color
    boolean isKing = false

    public Object asType(Class type) {
        if (type == DBObject) {
            BasicDBObject object = new BasicDBObject();
            object['player'] = player as DBObject
            object['color'] = color.name()
            object['isKing'] = isKing
            return object
        }
    }
}

@EqualsAndHashCode
class Game {
    ObjectId _id;
    User player1, player2
    User currentPlayer
    Board board;

    public Object asType(Class type) {
        if (type == DBObject) {
            BasicDBObject object = new BasicDBObject();
            object['_id'] = _id;
            object['player1'] = player1 as DBObject
            object['player2'] = player2 as DBObject
            object['currentPlayer'] = currentPlayer as DBObject
            object['board'] = board as DBObject
            return object
        }
    }
}