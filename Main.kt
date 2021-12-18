package chess

import kotlin.math.abs

fun main() {
    println("Pawns-Only Chess")

    println("First Player's name:")
    val players = mutableListOf(readLine()!!)
    println("Second Player's name:")
    players.add(readLine()!!)

    val board = createBoard()

    var input: String
    val coords = MutableList(4) { 0 }
    var color = "white"
    var pawn = 'W'
    var enemy = 'B'
    var currentPlayer = 0
    val enPassant = MutableList(2) { -1 }

    game@while (true) {
        printBoard(board)
        turn@while (true) {
            println("${players[currentPlayer]}'s turn:")
            input = readLine()!!
            if (input == "exit") {
                break@game
            }
            if (!isInputFormatted(input)) {
                println("Invalid Input")
                continue
            }
            coords[0] = letterToNumber(input[0])
            coords[1] = input[1].digitToInt() - 1
            coords[2] = letterToNumber(input[2])
            coords[3] = input[3].digitToInt() - 1
            when (currentPlayer) {
                0 -> {
                    color = "white"
                    pawn = 'W'
                    enemy = 'B'
                }
                1 -> {
                    color = "black"
                    pawn = 'B'
                    enemy = 'W'
                }
            }
            if (!isOriginValid(coords[0], coords[1], board, pawn)) {
                println("No $color pawn at ${input.substring(0..1)}")
                continue
            }
            if (!isMovePossible(coords, board, currentPlayer)) {
                println("Invalid Input")
                continue
            }
            when (getActionType(coords, board, enemy, enPassant)) {
                "enPassant" -> executeEnPassant(coords, board, enPassant)
                "capture" -> executeCapture(coords, board)
                "move" -> {
                    if (coords[0] != coords[2] || board[coords[3]][coords[2]] != ' ') {
                        println("Invalid Input")
                        continue
                    }
                    executeMove(coords, board)
                }
            }
            if (abs(coords[3] - coords[1]) == 2) {
                enPassant[0] = coords[2]
                enPassant[1] = coords[3]
            } else {
                enPassant[0] = -1
                enPassant[1] = -1
            }
            break
        }

        if (hasBlackWon(board)) {
            printBoard(board)
            println("Black Wins!")
            break
        } else if (hasWhiteWon(board)) {
            printBoard(board)
            println("White Wins!")
            break
        } else if (isStalemate(board)) {
            printBoard(board)
            println("Stalemate!")
            break
        }
        currentPlayer = 1 - currentPlayer
    }
    println("Bye!")
}

fun isInputFormatted(input: String): Boolean {
    val regex = "\\b[a-h][1-8][a-h][1-8]\\b".toRegex()
    if (!input.matches(regex)) return false
    return true
}

fun isOriginValid(x: Int, y: Int, board: MutableList<MutableList<Char>>, pawn: Char): Boolean {
    if (board[y][x].lowercaseChar() != pawn.lowercaseChar()) return false
    return true
}

fun isMovePossible(coords: List<Int>, board: MutableList<MutableList<Char>>, player: Int): Boolean {
    val (x1, y1, _, y2) = coords
    val dy = y2 - y1
    if (dy <= 0 && player == 0) return false
    if (dy >= 0 && player == 1) return false
    if (abs(dy) == 2 && board[y1][x1].isLowerCase()) return false
    if (abs(dy) > 2) return false
    return true
}

fun getActionType(coords: List<Int>, board: MutableList<MutableList<Char>>, enemy: Char, enPassant: List<Int>): String {
    val (x1, y1, x2, y2) = coords
    when (enemy.lowercaseChar()) {
        'b' -> {
            if (x2 == x1 + 1 || x2 == x1 - 1) {
                if (board[y2][x2].lowercaseChar() == enemy.lowercaseChar() && y2 == y1 + 1) return "capture"
                if (board[y2 - 1][x2].lowercaseChar() == enemy.lowercaseChar() && enPassant[0] == x2 && enPassant[1] == y2 - 1 && y2 == y1 + 1) {
                    return "enPassant"
                }
            }
            return "move"
        }
        'w' -> {
            if (x2 == x1 + 1 || x2 == x1 - 1) {
                if (board[y2][x2].lowercaseChar() == enemy.lowercaseChar() && y2 == y1 - 1) return "capture"
                if (board[y2 + 1][x2].lowercaseChar() == enemy.lowercaseChar() && enPassant[0] == x2 && enPassant[1] == y2 + 1 && y2 == y1 - 1) {
                    return "enPassant"
                }
            }
            return "move"
        }
    }
    return "unknown"
}

fun executeEnPassant(coords: List<Int>, board: MutableList<MutableList<Char>>, enPassant: List<Int>) {
    val (x1, y1, x2, y2) = coords
    board[y2][x2] = board[y1][x1].lowercaseChar()
    board[y1][x1] = ' '
    board[enPassant[1]][enPassant[0]] = ' '
}

fun executeCapture(coords: List<Int>, board: MutableList<MutableList<Char>>) {
    val (x1, y1, x2, y2) = coords
    board[y2][x2] = board[y1][x1].lowercaseChar()
    board[y1][x1] = ' '
}

fun executeMove(coords: List<Int>, board: MutableList<MutableList<Char>>) {
    val (x1, y1, x2, y2) = coords
    board[y2][x2] = board[y1][x1].lowercaseChar()
    board[y1][x1] = ' '
}

fun createBoard(): MutableList<MutableList<Char>> {
    val board = MutableList(8) { MutableList(8) { ' ' } }
    board[1].fill('W')
    board[board.lastIndex-1].fill('B')

    return board
}

fun printBoard(board: MutableList<MutableList<Char>>) {
    for (i in board.indices) {
        printLine()
        printRow(board.lastIndex + 1 - i, board[board.lastIndex - i])
    }
    printLine()
    print("  ")
    for (c in 'a'..'h') {
        print("  $c ")
    }
    println()
}

fun printLine() {
    println("  ${"+---".repeat(8)}+")
}

fun printRow(rowNumber: Int, line: MutableList<Char>) {
    print("$rowNumber ")
    for (cell in line) {
        print("| ${cell.uppercaseChar()} ")
    }
    println("|")
}

fun letterToNumber(letter: Char): Int {
    return letter-'a'
}

fun hasBlackWon(board: MutableList<MutableList<Char>>): Boolean {
    if (countPawns('W', board) == 0) return true
    if ('B' in board[0].map { it.uppercaseChar() }) return true
    for (line in board) {
        for (cell in line) {
            if (cell.uppercaseChar() == 'W') return false
        }
    }
    return true
}

fun hasWhiteWon(board: MutableList<MutableList<Char>>): Boolean {
    if (countPawns('B', board) == 0) return true
    if ('W' in board[board.lastIndex].map { it.uppercaseChar() }) return true
    for (line in board) {
        for (cell in line) {
            if (cell.uppercaseChar() == 'B') return false
        }
    }
    return true
}

fun isStalemate(board: MutableList<MutableList<Char>>) = isBlackStuck(board) || isWhiteStuck(board)

fun isBlackStuck(board: MutableList<MutableList<Char>>): Boolean {
    for (row in board.indices) {
        for (col in board[row].indices) {
            if (board[row][col].uppercaseChar() == 'B') {
                if (board[row - 1][col].uppercaseChar() == 'W') {
                    if (col == 0) {
                        if (board[row - 1][1].uppercaseChar() != 'W') {
                            continue
                        }
                    } else if (col == 7) {
                        if (board[row - 1][6].uppercaseChar() != 'W') {
                            continue
                        }
                    } else if (board[row - 1][col + 1].uppercaseChar() != 'W') {
                        if (board[row - 1][col - 1].uppercaseChar() != 'W') {
                            continue
                        }
                    }
                }
                return false
            }
        }
    }
    return true
}

fun isWhiteStuck(board: MutableList<MutableList<Char>>): Boolean {
    for (row in board.indices) {
        for (col in board[row].indices) {
            if (board[row][col].uppercaseChar() == 'W') {
                if (board[row + 1][col].uppercaseChar() == 'B') {
                    if (col == 0) {
                        if (board[row + 1][1].uppercaseChar() != 'B') {
                            continue
                        }
                    } else if (col == 7) {
                        if (board[row + 1][6].uppercaseChar() != 'B') {
                            continue
                        }
                    } else if (board[row + 1][col + 1].uppercaseChar() != 'B') {
                        if (board[row + 1][col - 1].uppercaseChar() != 'B') {
                            continue
                        }
                    }
                }
                return false
            }
        }
    }
    return true
}

fun countPawns(pawn: Char, board: MutableList<MutableList<Char>>): Int {
    var count = 0
    for (row in board) {
        for (cell in row) {
            if (cell.uppercaseChar() == pawn.uppercaseChar()) count++
        }
    }
    return count
}
