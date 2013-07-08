package net.saga.ag.checkers.vo

import net.saga.ag.checkers.vo.Color

import java.awt.*

class User {
    String userName
    transient byte[] password

}

class Move {
    Point startPos, endPos
}

class Board {
    Tile[] tiles = new Tile[64]

    Tile getTileAt(x , y) {
        tiles[x*8 + y]
    }

    Tile putTile(x , y, Tile tile) {
        tiles[x*8 + y] = tile
    }

}


class Tile {
    Color color
    Piece piece
}

enum Color {
    RED, BLACK
}

class Piece {
    User player
    Color color
    boolean isKing
}

class Game {
    User player1, player2
    User currentPlayer

}