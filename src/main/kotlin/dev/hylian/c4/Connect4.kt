package dev.hylian.c4

import dev.hylian.c4.exception.JoinException
import dev.hylian.c4.exception.LockedException
import dev.hylian.c4.exception.SelectException
import java.lang.StringBuilder
import java.util.*

class Connect4 {
    private val cells: LinkedList<LinkedList<String?>> = create()
    private val players: LinkedList<String> = LinkedList()
    private var turn: String? = null

    fun join(id: String) {

        if(id.isEmpty()) {
            throw JoinException("invalid user id!")
        }

        if (players.contains(id)) {
            throw JoinException("already in game!")
        }

        if (players.size == 2) {
            throw JoinException("game already full!")
        }

        if(players.add(id) && players.size == 2) {
            turn = players.random()
        }
    }

    fun insert(id: String, selection: Int): Boolean {

        if (!players.contains(id)) {
            throw SelectException("you're not in the game!")
        }

        if (id != turn) {
            throw SelectException("not your turn!")
        }

        if(selection > 7 || selection <= 0) {
            throw SelectException("invalid selection!")
        }

        val column = selection - 1
        if (!cells[column].any { r -> r == null }) {
            throw SelectException("column already full!")
        }

        cells[column][cells[column].indexOfFirst { r -> r == null }] = id
        if(isFilled()) {
            throw LockedException()
        }

        turn = players.first { p -> p != turn }
        return isWin()
    }

    private fun isWin(): Boolean {

        // vertical check
        for (column in cells) {
            var previous = column.firstOrNull()
            var count = 0

            for (id in column) {
                count = if (previous != null && previous == id) count + 1 else 0
                previous = id

                if(count == 4) {
                    return true
                }
            }
        }

        // horizontal check
        for (i in (0..5)) {
            var previous: String? = null
            var count = 0

            for (column in cells) {
                count = if (previous != null && previous == column[i]) count + 1 else 0
                previous = column[i]

                if(count == 4) {
                    return true
                }
            }
        }

        return isDiagonalWin(cells) || isDiagonalWin(reversedCell())
    }

    private fun reversedCell(): LinkedList<LinkedList<String?>> {

        val reversed: LinkedList<LinkedList<String?>> = create()
        for ((c, column) in cells.withIndex()) {
            for ((i, id) in column.withIndex()) {
                reversed[c][i] = id
            }
        }

        reversed.forEach { r -> r.reverse() }
        return reversed
    }

    private fun isDiagonalWin(cells: LinkedList<LinkedList<String?>>): Boolean {

        for (column in cells.lastIndex - 2 downTo 0) {
            var id = cells[column][5]
            var hit = 1
            var i = 5

            for (previous in column until cells.size) {
                val previousID: String? = cells[previous].getOrNull(i)
                hit = if (isHit(id, previousID)) ++ hit else 1
                id = previousID

                if(hit >= 4) {
                    return true
                }

                --i
            }
        }

        for (column in 3 until cells.size) {
            var id = cells[column][0]
            var hit = 0

            for ((i, previous) in (column downTo 0).withIndex()) {
                val previousID: String? = cells[previous].getOrNull(i)
                hit = if (isHit(id, previousID)) ++hit else 0
                id = previousID

                if (hit >= 4) {
                    return true
                }
            }
        }

        return false
    }

    private fun create(): LinkedList<LinkedList<String?>> {
        val list = LinkedList<LinkedList<String?>>()

        for(i in (0..6)) {
            list.add(LinkedList(arrayOfNulls<String?>(6).toList()))
        }

        return list
    }

    private fun isHit(id: String?, previous: String?): Boolean = id != null && previous == id;

    private fun isFilled(): Boolean = cells.all { l -> l.all { i -> i != null } }

    fun getTurn(): String? = turn

    fun build(): String {
        val builder = StringBuilder()

        for (i in 5 downTo 0) {
            for (column in cells) {
                val id = column[i]

                if (id == null) {
                    builder.append("⚪")
                } else {
                    if (players.indexOf(id) == 0) {
                        builder.append("\uD83D\uDD34")
                    } else {
                        builder.append("\uD83D\uDD35")
                    }
                }
            }

            builder.append("\n")
        }

        return builder.toString()
    }

    fun getCells(): LinkedList<LinkedList<String?>> {
        return cells
    }
}